/*
 * File:    Test.java
 * Package: server
 * Author:  Zachary Gill
 */

package client;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utility.CryptoUtility;

import javax.crypto.SecretKey;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.regex.Pattern;

public class Test
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
     * The default value of the flag to enable server logging or not.
     */
    public static final boolean DEFAULT_LOG_SERVER = false;
    
    /**
     * The regex pattern for a Content-Disposition header.
     */
    public static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment; filename=(?<filename>.*); size=(?<filesize>.*);");
    
    
    //Fields
    
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
    private static String authToken = "";
    
    /**
     * The id of the communication channel opened for communicating with the server.
     */
    private static long serverCommId = -1L;
    
    /**
     * The id of the user logged in.
     */
    private static String userId = "";
    
    
    
    
    public static void main(String[] args)
    {
        //set up the client connection
        resourceConfig.register(MultiPartFeature.class);
        resourceConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
        client = ClientBuilder.newClient(resourceConfig);
        
        //generate the unique keys for the client
        generateKeys();
        openCommunicationChannel();
        
        
        //run test
        
        String testUser = "tmp" + ((int)(Math.random() * 10000) + 1);
        String testPass = "password";
        
        //register the user on the server
        boolean registered = testUserRegistration(testUser, testPass, "email@email.com", "Andersmith", "Sanderstein", false);
        if (registered) {
            System.out.println("User: " + testUser + " registered with id: " + userId);
        } else {
            System.out.println("User: " + testUser + " could not be registered");
            return;
        }
        
        //essentially logging in, making sure the credentials are good and returning a User object
        User user = testUserValidation(testUser, testPass);
        if (user == null) {
            return;
        }
        System.out.println("User data: " + user.toString());
        
        //authorize user, get an auth token allowing them to make changes to the database, the auth toke n is required for some endpoints
        String token = testUserAuthorization(testUser, testPass);
        System.out.println("User authorized with auth token: " + authToken);
    
    
        //close the client connection
        closeCommunicationChannel(serverCommId);
        
        try {
            client.close();
        } catch (Exception ignored) {
        }
    }
    
    public static void generateKeys()
    {
        dsaKeys = CryptoUtility.generateDSAKeyPair();
        rsaKeys = CryptoUtility.generateRSAKeyPair();
        SecurityHandler.setDsaKeys(dsaKeys);
        SecurityHandler.setRsaKeys(rsaKeys);
    }
    
    
    
    public static boolean testUserRegistration(String username, String password, String email, String firstName, String lastName, boolean producer)
    {
        String passHash = CryptoUtility.hashSHA512(password);
        String signature = ClientCommunicationHandler.signCommunication(serverCommId, passHash);
        String encryptedPassHash = ClientCommunicationHandler.encryptCommunication(serverCommId, passHash);
        if (encryptedPassHash.isEmpty()) {
            return false;
        }
    
        userId = registerUser(username, encryptedPassHash, signature, serverCommId, email, firstName, lastName, producer);
        return !userId.isEmpty();
    }
    
    public static User testUserValidation(String username, String password)
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
    
                user.setUserId((String) json.get("userId"));
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
    
    public static String testUserAuthorization(String username, String password)
    {
        String passHash = CryptoUtility.hashSHA512(password);
        String signature = ClientCommunicationHandler.signCommunication(serverCommId, passHash);
        String encryptedPassHash = ClientCommunicationHandler.encryptCommunication(serverCommId, passHash);
    
        authorizeUser(username, encryptedPassHash, signature, serverCommId);
        return authToken;
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
    public static boolean closeCommunicationChannel(long commId)
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
     * @return The unique hex id of the newly created user.
     */
    public static String registerUser(String user, String passHash, String signature, long commId, String email, String firstName, String lastName, boolean producer)
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
                return "";
            }
            
            formMultiPart.close();
            multiPart.close();
            
            if (!printResponse(response)) {
                return "";
            }
            
            return response.getHeaderString("userId");
            
        } catch (IOException e) {
            return "";
        }
    }
    
    
    /**
     * Validates a user with the server.
     *
     * @param user      The username of the user being validated.
     * @param passHash  The password hash of the user being validated encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @return The unique hex id of the user, null if there was an error.
     */
    public static String validateUser(String user, String passHash, String signature, long commId)
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
    public static boolean authorizeUser(String user, String passHash, String signature, long commId)
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
    
    
}
