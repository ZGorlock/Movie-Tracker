/*
 * File:    ServerHandler.java
 * Package: client.server
 * Author:  Zachary Gill
 */

package client.server;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import client.pojo.Media;
import client.pojo.User;
import client.utility.CryptoUtility;

public class ServerHandler
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
    private static final ResourceConfig resourceConfig = new ResourceConfig();
    
    /**
     * The client connection to the server.
     */
    private static Client client = null;
    
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
        resourceConfig.register(MultiPartFeature.class);
        client = ClientBuilder.newClient(resourceConfig);
    
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
    
        try {
            client.close();
        } catch (Exception ignored) {
        }
    }
    
    /**
     * Prints the response from the DLA server.
     *
     * @param response The response from the DLA server.
     * @return Whether the response from the DLA server was ok or not.
     */
    private static boolean printResponse(Response response)
    {
        String message = response.getHeaderString("message");
        if ((response == null) || (response.getStatus() != Response.Status.OK.getStatusCode())) {
            
            System.out.println("Server: Returned response: " + response.getStatus() + " - " + ((message == null) ? "  Server: Encountered an exception" : message));
            return false;
        } else {
            System.out.println("Server: Returned response: " + response.getStatus() + " - " + ((message == null) ? "  Server: Encountered an exception" : message));
            return true;
        }
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
    public static Media retrieveMedia(int mediaId)
    {
        String response = mediaRetrieve(String.valueOf(mediaId));
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
            media.setImage(new File("images" + File.separator + json.get("image")));
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
    private static long openCommunicationChannel()
    {
        String url = BASE_URI + "openCommunicationChannel";
        
        PublicKey rsaPublicKey = rsaKeys.getPublic();
        PrivateKey rsaPrivateKey = rsaKeys.getPrivate();
        String rsaPublicKeyStore = CryptoUtility.storeRSAPublicKey(rsaPublicKey);
        
        PublicKey dsaPublicKey = dsaKeys.getPublic();
        String dsaPublicKeyStore = CryptoUtility.storeDSAPublicKey(dsaPublicKey);
        
        long commId;
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("rsaPublicKeyStore", rsaPublicKeyStore)
                    .field("dsaPublicKeyStore", dsaPublicKeyStore);
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return -1;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return -1;
            }
            
            commId = Long.valueOf(response.getHeaderString("commId"));
            String aesKeyStore = response.getHeaderString("aesKeyStore");
            String rsaServerPublicKeyStore = response.getHeaderString("rsaServerPublicKeyStore");
            String dsaServerPublicKeyStore = response.getHeaderString("dsaServerPublicKeyStore");
            
            String decryptedAESKeyStore = CryptoUtility.decryptRSA(aesKeyStore, rsaPrivateKey);
            SecretKey aesKey = CryptoUtility.readAESSecret(decryptedAESKeyStore);
            PublicKey rsaServerPublicKey = CryptoUtility.readRSAPublicKey(rsaServerPublicKeyStore);
            PublicKey dsaServerPublicKey = CryptoUtility.readDSAPublicKey(dsaServerPublicKeyStore);
            
            if (!ClientCommunicationHandler.openCommunicationChannel(commId, aesKey, rsaServerPublicKey, dsaServerPublicKey)) {
                return -1;
            }
            
        } catch (IOException e) {
            return -1;
        }
        
        serverCommId = commId;
        return commId;
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
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("commId", String.valueOf(commId))
                    .field("aesKeyStore", aesKeyStore);
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return false;
            }
            
            if (!ClientCommunicationHandler.closeCommunicationChannel(commId)) {
                return false;
            }
            
        } catch (IOException e) {
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
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("user", user)
                    .field("passHash", passHash)
                    .field("signature", signature)
                    .field("commId", String.valueOf(commId))
                    .field("email", email)
                    .field("firstName", firstName)
                    .field("lastName", lastName)
                    .field("producer", producer ? "y" : "n");
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return -1;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return -1;
            }
            
            return Integer.valueOf(response.getHeaderString("userId"));
            
        } catch (IOException e) {
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
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("user", user)
                    .field("passHash", passHash)
                    .field("signature", signature)
                    .field("commId", String.valueOf(commId));
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return null;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return null;
            }
            
            return response.getHeaderString("userInfo");
            
        } catch (IOException e) {
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
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("user", user)
                    .field("passHash", passHash)
                    .field("signature", signature)
                    .field("commId", String.valueOf(commId));
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return false;
            }
            
            String encryptedAuthToken = response.getHeaderString("authToken");
            authToken = ClientCommunicationHandler.decryptCommunication(commId, encryptedAuthToken);
            return true;
            
        } catch (IOException e) {
            return false;
        }
    }
    
    private static boolean mediaAdd(File image, String title, String type, String description, String genre, String actors, String showtimes, String rating, String year)
    {
        String url = BASE_URI + "addMedia";
        
        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);
        
        try {
            FileDataBodyPart filePart = new FileDataBodyPart("file", image);
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = (FormDataMultiPart) formMultiPart
                    .field("title", title)
                    .field("type", type)
                    .field("description", description)
                    .field("genre", genre)
                    .field("actors", actors)
                    .field("showtimes", showtimes)
                    .field("rating", rating)
                    .field("year", year)
                    .field("authToken", encryptedAuthToken)
                    .field("commId", String.valueOf(serverCommId))
                    .bodyPart(filePart);
        
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
        
            formMultiPart.close();
            multiPart.close();
    
            return printResponse(response);
    
        } catch (IOException e) {
            return false;
        }
    }
    
    private static boolean mediaEdit(String mediaId, File image, String title, String type, String description, String genre, String actors, String showtimes, String rating, String year)
    {
        String url = BASE_URI + "editMedia";
    
        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);
    
        try {
            FileDataBodyPart filePart = new FileDataBodyPart("file", image);
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = (FormDataMultiPart) formMultiPart
                    .field("mediaId", mediaId)
                    .field("title", title)
                    .field("type", type)
                    .field("description", description)
                    .field("genre", genre)
                    .field("actors", actors)
                    .field("showtimes", showtimes)
                    .field("rating", rating)
                    .field("year", year)
                    .field("authToken", encryptedAuthToken)
                    .field("commId", String.valueOf(serverCommId))
                    .bodyPart(filePart);
        
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
        
            formMultiPart.close();
            multiPart.close();
        
            return printResponse(response);
        
        } catch (IOException e) {
            return false;
        }
    }
    
    private static boolean mediaDelete(String mediaId)
    {
        String url = BASE_URI + "deleteMedia";
    
        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);
    
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("mediaId", mediaId)
                    .field("authToken", encryptedAuthToken)
                    .field("commId", String.valueOf(serverCommId));
        
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
        
            formMultiPart.close();
            multiPart.close();
        
            return printResponse(response);
        
        } catch (IOException e) {
            return false;
        }
    }
    
    private static String mediaQuery(String title, String type, String producerId, String description, String genre, String actors, String showtimes, String rating, String year)
    {
        String url = BASE_URI + "queryMedia";
        
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("title", title)
                    .field("type", type)
                    .field("producerId", producerId)
                    .field("description", description)
                    .field("genre", genre)
                    .field("actors", actors)
                    .field("showtimes", showtimes)
                    .field("rating", rating)
                    .field("year", year);
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return "";
            }
            
            formMultiPart.close();
            multiPart.close();
            
            return response.getHeaderString("results");
            
        } catch (IOException e) {
            return "";
        }
    }
    
    public static String mediaRetrieve(String mediaId)
    {
        String url = BASE_URI + "retrieveMedia";
        
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("mediaId", mediaId);
        
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return "";
            }
        
            formMultiPart.close();
            multiPart.close();
        
            if (!printResponse(response)) {
                return "";
            }
        
            Matcher dispositionMatcher = CONTENT_DISPOSITION_PATTERN.matcher(response.getHeaderString("Content-Disposition"));
            if (!dispositionMatcher.matches()) {
                return "";
            }
            
            File imageStore = new File("images" + File.separator + dispositionMatcher.group("filename"));
            
            if (!imageStore.exists()) {
                File dir = new File("images");
                if (!dir.exists() || !dir.isDirectory()) {
                    Files.createDirectory(Paths.get(dir.getAbsolutePath()));
                }
                Files.createFile(Paths.get(imageStore.getAbsolutePath()));
                FileOutputStream fos = null;
                InputStream is = null;
    
                try {
                    fos = new FileOutputStream(imageStore);
                    is = (InputStream) response.getEntity();
        
                    int read;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                }
            }
    
            return response.getHeaderString("mediaInfo");
        
        } catch (IOException e) {
            return "";
        }
    }
    
    private static boolean subscriptionAdd(String mediaId)
    {
        String url = BASE_URI + "addSubscription";
        
        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);
        
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("mediaId", mediaId)
                    .field("authToken", encryptedAuthToken)
                    .field("commId", String.valueOf(serverCommId));
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            return printResponse(response);
            
        } catch (IOException e) {
            return false;
        }
    }
    
    private static boolean subscriptionRemove(String mediaId)
    {
        String url = BASE_URI + "removeSubscription";
        
        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);
        
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("mediaId", mediaId)
                    .field("authToken", encryptedAuthToken)
                    .field("commId", String.valueOf(serverCommId));
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return false;
            }
            
            formMultiPart.close();
            multiPart.close();
            
            return printResponse(response);
            
        } catch (IOException e) {
            return false;
        }
    }
    
    private static String subscriptionsGet()
    {
        String url = BASE_URI + "getSubscriptions";
        
        String encryptedAuthToken = ClientCommunicationHandler.encryptCommunication(serverCommId, authToken);
        
        try {
            FormDataMultiPart formMultiPart = new FormDataMultiPart();
            FormDataMultiPart multiPart = formMultiPart
                    .field("authToken", encryptedAuthToken)
                    .field("commId", String.valueOf(serverCommId));
            
            WebTarget target = client.target(url);
            Response response;
            try {
                response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
            } catch (ProcessingException ignored) {
                return "";
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return "";
            }
            
            return response.getHeaderString("subscriptions");
            
        } catch (IOException e) {
            return "";
        }
    }
    
}
