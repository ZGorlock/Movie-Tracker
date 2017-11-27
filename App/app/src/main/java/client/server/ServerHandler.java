/*
 * File:    ServerHandler.java
 * Package: client.server
 * Author:  Zachary Gill
 */

package client.server;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.content.*;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
//import com.sun.jna.platform.FileUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import client.pojo.Media;
import client.pojo.User;
import client.utility.CryptoUtility;

public class ServerHandler extends Application
{
    
    //Constants
    
    /**
     * The base uri for the server.
     */
    public static final String BASE_URI = "http://movie-tracker.dynu.net:4444/";
    
    /**
     * The size in bytes of the buffer used for file transfers.
     */
    public static final int BUFFER_SIZE = 16384;
    
    /**
     * The regex pattern for a Content-Disposition header.
     */
    public static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment; filename=(?<filename>.*); size=(?<filesize>.*);");
    
    
    //Static Fields
    
    /**
     * The resource configuration for the Client connection to the server.
     */
    //private static final ResourceConfig resourceConfig = new ResourceConfig();
    
    /**
     * The client connection to the server.
     */
    private static OkHttpClient client = null;
    
    /**
     * The DSA keys of the system.
     */
    private static KeyPair dsaKeys = null;
    
    /**
     * The RSA keys of the system.
     */
    private static KeyPair rsaKeys = null;
    
    /**
     * The auth token distributed by the server.
     */
    public static String authToken = "";
    
    /**
     * The id of the communication channel opened for communicating with the server.
     */
    public static long serverCommId = -1L;
    
    /**
     * The id of the user logged in.
     */
    public static int userId = -1;
    
    
    //Methods
    
    /**
     * Sets up the Server handler.
     */
    public static void setupServerHandler()
    {
        //set up the client connection
        client = new OkHttpClient();
    
        //generate the unique keys for the client
        generateKeys();
        openCommunicationChannel();
    }
    
    /**
     * Generates the unique keys for the client.
     */
    public static void generateKeys()
    {
        dsaKeys = CryptoUtility.generateDSAKeyPair();
        rsaKeys = CryptoUtility.generateRSAKeyPair();
        SecurityHandler.setDsaKeys(dsaKeys);
        SecurityHandler.setRsaKeys(rsaKeys);
    }
    
    /**
     * Shuts down the Server handler.
     */
    public static void shutdownServerHandler()
    {
        //close the client connection
        closeCommunicationChannel(serverCommId);
    }
    
    /**
     * Prints the response from the DLA server.
     *
     * @param response The response from the DLA server.
     * @return Whether the response from the DLA server was ok or not.
     */
    private static boolean printResponse(Response response)
    {
        String message = response.header("message");
        System.out.println("Server: Returned response: " + response.code() + " - " + ((message == null) ? "  Server: Encountered an exception" : message));
        return response.code() == 200;
    }
    
    
    //REST Endpoint Access
    
    public static boolean registerUser(String username, String password, String email, String firstName, String lastName, boolean producer)
    {
        String passHash = CryptoUtility.hashSHA512(password);

        String signature = ClientCommunicationHandler.signCommunication(serverCommId, passHash);

        String encryptedPassHash = ClientCommunicationHandler.encryptCommunication(serverCommId, passHash);

        if (encryptedPassHash.isEmpty()) {

            return false;
        }


        userId = registerUser(username, encryptedPassHash, signature, serverCommId, email, firstName, lastName, producer);

        return userId != -1;
    }

    public static User validateUser(String username, String password)
    {
        String passHash = CryptoUtility.hashSHA512(password);
        String signature = ClientCommunicationHandler.signCommunication(serverCommId, passHash);
        String encryptedPassHash = ClientCommunicationHandler.encryptCommunication(serverCommId, passHash);
        if (encryptedPassHash.isEmpty()) {
            return null;
        }

        String response = validateUser(username, encryptedPassHash, signature, serverCommId);
        boolean success = (response != null) && !response.isEmpty();

        if (success) {
            System.out.println("User: " + username + " validated");

            User user = new User();
            try {
                org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
                JSONObject json = (JSONObject) parser.parse(response);

                user.setUserId(Integer.valueOf((String) json.get("userId")));
                user.setUsername((String) json.get("userName"));
                user.setEmail((String) json.get("email"));
                user.setFirstName((String) json.get("firstName"));
                user.setLastName((String) json.get("lastName"));
                user.setProducer("y".equals(json.get("producer")));

                return user;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("User: " + username + " validated");
        }
        return null;
    }

    public static String authorizeUser(String username, String password)
    {
        String passHash = CryptoUtility.hashSHA512(password);
        String signature = ClientCommunicationHandler.signCommunication(serverCommId, passHash);
        String encryptedPassHash = ClientCommunicationHandler.encryptCommunication(serverCommId, passHash);

        authorizeUser(username, encryptedPassHash, signature, serverCommId);
        return authToken;
    }

    /**
     * Adds a Media.
     * @param media The Media to add.
     * @return Whether the operation was successful or not.
     */
    public static boolean addMedia(Media media)
    {
        return mediaAdd(media.getImage(), media.getTitle(), media.getType(), media.getDescription(), media.getGenre(), media.getActors(), media.getShowtimes(), media.getRating(), String.valueOf(media.getYear()));
    }

    /**
     * Edits a Media.
     * @param mediaId The id of the Media to edit.
     * @param media   The Media to set as the new Media for the specific id.
     * @return Whether the operation was successful or not.
     */
    public static boolean editMedia(int mediaId, Media media)
    {
        return mediaEdit(String.valueOf(mediaId), media.getImage(), media.getTitle(), media.getType(), media.getDescription(), media.getGenre(), media.getActors(), media.getShowtimes(), media.getRating(), String.valueOf(media.getYear()));
    }

    /**
     * Deletes a Media.
     * @param mediaId The id of the Media to delete.
     * @return Whether the operation was successful or not.
     */
    public static boolean deleteMedia(int mediaId)
    {
        return mediaDelete(String.valueOf(mediaId));
    }

    /**
     * Queries Media.
     * @param media The Media containing the query parameters.
     * @return The list of media ids returned as a result.
     */
    public static List<Integer> queryMedia(Media media)
    {
        String response = mediaQuery(media.getTitle(), media.getType(), String.valueOf(media.getProducerId()), media.getDescription(), media.getGenre(), media.getActors(), media.getShowtimes(), media.getRating(), String.valueOf(media.getYear()));
        if (response.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> resultList = new ArrayList<>();

        try {
            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);

            JSONArray results = (JSONArray) json.get("results");
            for (Object o : results) {
                resultList.add(Integer.valueOf(o.toString()));
            }

            return resultList;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves a Media.
     * @param mediaId The id of the Media to retrieve.
     * @return The Media that was retrieved, null if there was an error.
     */
    public static Media retrieveMedia(int mediaId, Context context)
    {
        String response = mediaRetrieve(String.valueOf(mediaId),context);
        if (response.isEmpty()) {
            return null;
        }

        Media media = new Media();
        try {
            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);

            media.setMediaId(Integer.valueOf((String) json.get("mediaId")));
            media.setTitle((String) json.get("title"));
            media.setType((String) json.get("type"));
            media.setProducerId(Integer.valueOf((String) json.get("producerId")));
            media.setDescription((String) json.get("description"));
            media.setGenre((String) json.get("genre"));
            media.setActors((String) json.get("actors"));
            media.setImage(new File((Environment.getExternalStorageDirectory()+"/poasters/"+json.get("image").toString())));
            media.setShowtimes((String) json.get("showtimes"));
            media.setRating((String) json.get("rating"));
            media.setYear(Integer.valueOf((String) json.get("year")));

            return media;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a Subscription.
     * @param mediaId The Media to add a subscription for.
     * @return Whether the operation was successful or not.
     */
    public static boolean addSubscription(int mediaId)
    {
        return subscriptionAdd(String.valueOf(mediaId));
    }

    /**
     * Removes a Subscription.
     * @param mediaId The Media to remove a subscription for.
     * @return Whether the operation was successful or not.
     */
    public static boolean removeSubscription(int mediaId)
    {
        return subscriptionRemove(String.valueOf(mediaId));
    }

    /**
     * Gets Subscriptions of the user.
     * @return A list of media id's that the user is subscribed to.
     */
    public static List<Integer> getSubscriptions()
    {
        String response = subscriptionsGet();
        if (response.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> subscriptionList = new ArrayList<>();

        try {
            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);

            JSONArray results = (JSONArray) json.get("subscriptions");
            for (Object o : results) {
                subscriptionList.add(Integer.valueOf(o.toString()));
            }

            return subscriptionList;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    
    //REST Endpoints
    
    /**
     * Opens a new communication channel with the server.
     *
     * @return The id of the communication channel that was opened, -1 if there was an error.
     */
    public static long openCommunicationChannel()
    {
        String url = BASE_URI + "openCommunicationChannel";

        PublicKey rsaPublicKey = rsaKeys.getPublic();
        PrivateKey rsaPrivateKey = rsaKeys.getPrivate();
        String rsaPublicKeyStore = CryptoUtility.storeRSAPublicKey(rsaPublicKey);

        PublicKey dsaPublicKey = dsaKeys.getPublic();
        String dsaPublicKeyStore = CryptoUtility.storeDSAPublicKey(dsaPublicKey);

        long commId;
        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("rsaPublicKeyStore", rsaPublicKeyStore)
                    .addFormDataPart("dsaPublicKeyStore", dsaPublicKeyStore).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return serverCommId;
            }

            commId = Long.valueOf(response.header("commId"));
            String aesKeyStore = response.header("aesKeyStore");
            String rsaServerPublicKeyStore = response.header("rsaServerPublicKeyStore");
            String dsaServerPublicKeyStore = response.header("dsaServerPublicKeyStore");

            String decryptedAESKeyStore = CryptoUtility.decryptRSA(aesKeyStore, rsaPrivateKey);
            SecretKey aesKey = CryptoUtility.readAESSecret(decryptedAESKeyStore);
            PublicKey rsaServerPublicKey = CryptoUtility.readRSAPublicKey(rsaServerPublicKeyStore);
            PublicKey dsaServerPublicKey = CryptoUtility.readDSAPublicKey(dsaServerPublicKeyStore);

            if (!ClientCommunicationHandler.openCommunicationChannel(commId, aesKey, rsaServerPublicKey, dsaServerPublicKey)) {
                return -1;
            }

        } catch (Exception e) {
            System.out.println(e.toString() + " : \n" + Arrays.toString(e.getStackTrace()));
            return -1;
        }

        serverCommId = commId;
        return serverCommId;
    }

    /**
     * Closes a communication channel with the server.
     *
     * @param commId The id of the communication channel to close.
     * @return Whether the communication channel was closed or not
     */
    private static boolean closeCommunicationChannel(long commId)
    {
        String url = BASE_URI + "closeCommunicationChannel";

        String aesKeyStore = ClientCommunicationHandler.getCommunicationChannelKey(commId);
        if (aesKeyStore.isEmpty()) {
            return false;
        }

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("commId", String.valueOf(commId))
                    .addFormDataPart("aesKeyStore", aesKeyStore).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return false;
            }

            if (!ClientCommunicationHandler.closeCommunicationChannel(commId)) {
                return false;
            }

        } catch (Exception e) {
            System.out.println(e.toString() + " : \n" + Arrays.toString(e.getStackTrace()));
            return false;
        }

        return true;
    }

    /**
     * Registers a user on the server.
     *
     * @param user      The username of the user being registered.
     * @param passHash  The password hash of the user being registered encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @return The unique id of the newly created user, -1 if there was an error.
     */
    private static int registerUser(String user, String passHash, String signature, long commId, String email, String firstName, String lastName, boolean producer)
    {
        String url = BASE_URI + "registerUser";

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("user", user)
                    .addFormDataPart("passHash", passHash)
                    .addFormDataPart("signature", signature)
                    .addFormDataPart("commId", String.valueOf(commId))
                    .addFormDataPart("email", email)
                    .addFormDataPart("firstName", firstName)
                    .addFormDataPart("lastName", lastName)
                    .addFormDataPart("producer", producer ? "y" : "n").build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return -1;
            }

            return Integer.valueOf(response.header("userId"));

        } catch (Exception e) {
            System.out.println(e.toString() + " : \n" + Arrays.toString(e.getStackTrace()));
            return -1;
        }
    }

    /**
     * Validates a user with the server.
     *
     * @param user      The username of the user being validated.
     * @param passHash  The password hash of the user being validated encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @return The information about the user, null if there was an error.
     */
    private static String validateUser(String user, String passHash, String signature, long commId)
    {
        String url = BASE_URI + "validateUser";

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("user", user)
                    .addFormDataPart("passHash", passHash)
                    .addFormDataPart("signature", signature)
                    .addFormDataPart("commId", String.valueOf(commId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return null;
            }

            return response.header("userInfo");

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Authorizes a user with the server.
     *
     * @param user      The username of the user being authorized.
     * @param passHash  The password hash of the user being authorized encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @return An auth token.
     */
    private static boolean authorizeUser(String user, String passHash, String signature, long commId)
    {
        String url = BASE_URI + "authorizeUser";

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("user", user)
                    .addFormDataPart("passHash", passHash)
                    .addFormDataPart("signature", signature)
                    .addFormDataPart("commId", String.valueOf(commId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return false;
            }

            String encryptedAuthToken = response.header("authToken");
            authToken = ClientCommunicationHandler.decryptCommunication(commId, encryptedAuthToken);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean mediaAdd(File image, String title, String type, String description, String genre, String actors, String showtimes, String rating, String year)
    {
        String url = BASE_URI + "addMedia";

        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);

        String imageDump = "";
        String imageType = "";
        try {
            imageDump = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(image.getAbsolutePath())));
        } catch (IOException e1) {
            imageDump = "";
        }
        imageType = image.getName().substring(image.getName().indexOf('.') + 1);

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("title", title)
                    .addFormDataPart("type", type)
                    .addFormDataPart("description", description)
                    .addFormDataPart("genre", genre)
                    .addFormDataPart("actors", actors)
                    .addFormDataPart("imageDump", imageDump)
                    .addFormDataPart("imageType", imageType)
                    .addFormDataPart("showtimes", showtimes)
                    .addFormDataPart("rating", rating)
                    .addFormDataPart("year", year)
                    .addFormDataPart("authToken", encryptedAuthToken)
                    .addFormDataPart("commId", String.valueOf(serverCommId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            return printResponse(response);

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean mediaEdit(String mediaId, File image, String title, String type, String description, String genre, String actors, String showtimes, String rating, String year)
    {
        String url = BASE_URI + "editMedia";

        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);

        String imageDump = "";
        String imageName = "";
        String imageType = "";
        try {
            imageDump = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(image.getAbsolutePath())));
        } catch (IOException e1) {
            imageDump = "";
        }
        imageName = image.getName();
        imageType = image.getName().substring(image.getName().indexOf('.') + 1);

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("mediaId", mediaId)
                    .addFormDataPart("title", title)
                    .addFormDataPart("type", type)
                    .addFormDataPart("description", description)
                    .addFormDataPart("genre", genre)
                    .addFormDataPart("actors", actors)
                    .addFormDataPart("imageDump", imageDump)
                    .addFormDataPart("imageName", imageName)
                    .addFormDataPart("imageType", imageType)
                    .addFormDataPart("showtimes", showtimes)
                    .addFormDataPart("rating", rating)
                    .addFormDataPart("year", year)
                    .addFormDataPart("authToken", encryptedAuthToken)
                    .addFormDataPart("commId", String.valueOf(serverCommId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            return printResponse(response);

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean mediaDelete(String mediaId)
    {
        String url = BASE_URI + "deleteMedia";

        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("mediaId", mediaId)
                    .addFormDataPart("authToken", encryptedAuthToken)
                    .addFormDataPart("commId", String.valueOf(serverCommId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            return printResponse(response);

        } catch (Exception e) {
            return false;
        }
    }

    private static String mediaQuery(String title, String type, String producerId, String description, String genre, String actors, String showtimes, String rating, String year)
    {
        String url = BASE_URI + "queryMedia";

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("title", title)
                    .addFormDataPart("type", type)
                    .addFormDataPart("producerId", producerId)
                    .addFormDataPart("description", description)
                    .addFormDataPart("genre", genre)
                    .addFormDataPart("actors", actors)
                    .addFormDataPart("showtimes", showtimes)
                    .addFormDataPart("rating", rating)
                    .addFormDataPart("year", year).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return "";
            }

            return response.header("results");

        } catch (Exception e) {
            return "";
        }
    }

    public static String mediaRetrieve(String mediaId, Context context)
    {
        String url = BASE_URI + "retrieveMedia";

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("mediaId", mediaId).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return "";
            }

            //TODO images
            String imageName = response.header("image");
            if (imageName != null || !(imageName.equals(""))) {
                File myDir = new File(Environment.getExternalStorageDirectory() + "/poasters/");
                myDir.mkdirs();
                File imageStore = new File(Environment.getExternalStorageDirectory() + "/poasters" + File.separator + imageName);
                if (!imageStore.exists()) {

//                    File dir = new File("images");
//                    if (!dir.exists() || !dir.isDirectory()) {
//                        System.out.println("Image response4:"+imageName);
//
//                        Files.createDirectory(Paths.get(dir.getAbsolutePath()));
//                    }

//                    Files.createFile(Paths.get(imageStore.getAbsolutePath()));
                    File file = new File(context.getFilesDir(), imageName);
//                    System.out.println(file);
                    String imageDump = response.header("imageDump");
                    System.out.println("Image dump:" + imageDump);
                    if (imageDump != null && !imageDump.isEmpty()) {

                        byte[] data = Base64.getDecoder().decode(imageDump.getBytes());


                        try {
                            FileOutputStream fos = new FileOutputStream(imageStore);
                            fos.write(data);
                            fos.close();
                            System.out.println(file);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return response.header("mediaInfo");

        } catch (IOException e) {
            return "";
        }
    }

    private static boolean subscriptionAdd(String mediaId)
    {
        String url = BASE_URI + "addSubscription";

        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("mediaId", mediaId)
                    .addFormDataPart("authToken", encryptedAuthToken)
                    .addFormDataPart("commId", String.valueOf(serverCommId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            return printResponse(response);

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean subscriptionRemove(String mediaId)
    {
        String url = BASE_URI + "removeSubscription";

        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("mediaId", mediaId)
                    .addFormDataPart("authToken", encryptedAuthToken)
                    .addFormDataPart("commId", String.valueOf(serverCommId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            return printResponse(response);

        } catch (Exception e) {
            return false;
        }
    }

    private static String subscriptionsGet()
    {
        String url = BASE_URI + "getSubscriptions";

        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);

        try {
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("authToken", encryptedAuthToken)
                    .addFormDataPart("commId", String.valueOf(serverCommId)).build();
            Request request = new Request.Builder().header("Content-type", "text/plain")
                    .url(url).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if (!printResponse(response)) {
                return "";
            }

            return response.header("subscriptions");

        } catch (Exception e) {
            return "";
        }
    }
    
}
