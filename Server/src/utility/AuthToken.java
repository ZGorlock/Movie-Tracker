/*
 * File:    AuthToken.java
 * Package: utility
 * Author:  Zachary Gill
 */

package utility;

import communication.CommunicationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores an auth token object.
 */
public final class AuthToken
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthToken.class);
    
    
    //Constants
    
    /**
     * The time before a token expires when it ought to be renewed.
     */
    public static final long TIME_BEFORE_EXPIRATION_FOR_RENEWAL = Server.KEY_UPDATE_FREQUENCY / 100;
    
    
    //Static Fields
    
    /**
     * A map of the auth tokens distributed by the server to the id of the users they authorize.
     */
    private static final Map<AuthToken, Integer> authTokens = new HashMap<>();
    
    /**
     * A map of the tokens to the auth tokens they represent.
     */
    private static final Map<String, AuthToken> authTokenTokens = new HashMap<>();
    
    
    //Fields
    
    /**
     * The auth token.
     */
    private String token = "";
    
    /**
     * The expiration time of the auth token.
     */
    private long expiration;
    
    
    //Constructor
    
    /**
     * The constructor for an AuthToken.
     *
     * @param token The token of the auth token.
     */
    public AuthToken(String token)
    {
        this.token = token;
        expiration = System.currentTimeMillis() + Server.KEY_UPDATE_FREQUENCY;
    }
    
    
    //Methods
    
    /**
     * Determines if the auth token is expired or not.
     *
     * @return Whether the auth token is expired or not.
     */
    public boolean isExpired()
    {
        return System.currentTimeMillis() >= expiration;
    }
    
    /**
     * Determines if the auth token ought to be renewed or not.
     *
     * @return Whether the auth token ought to be renewed or not.
     */
    public boolean shouldRenew()
    {
        return (expiration - System.currentTimeMillis()) <= TIME_BEFORE_EXPIRATION_FOR_RENEWAL;
    }
    
    
    //Functions
    
    /**
     * Generates an auth token.
     *
     * @param userId The hex id of the user requesting the auth token.
     * @return The auth token.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static synchronized String generateAuthToken(int userId)
    {
        String token;
        while (authTokens.containsKey(token = UUID.randomUUID().toString())) {
        }
        AuthToken authToken = new AuthToken(token);
        authTokens.put(authToken, userId);
        authTokenTokens.put(token, authToken);
        return token;
    }
    
    /**
     * Determines if an auth token is expired.
     *
     * @param token The auth token.
     * @return Whether the auth token is expired or not.
     */
    public static synchronized boolean authTokenExpired(String token)
    {
        AuthToken authToken = authTokenTokens.get(token);
        if (authToken == null) {
            return true;
        }
        if (authToken.isExpired()) {
            purgeAuthToken(token);
            return true;
        }
        return false;
    }
    
    /**
     * Verifies an auth token is still valid and should not be renewed.
     *
     * @param token The auth token.
     * @return Whether the auth token is still valid and should not be renewed.
     */
    public static synchronized boolean verifyAuthToken(String token)
    {
        AuthToken authToken = authTokenTokens.get(token);
        return (authToken != null) && !authToken.isExpired() && !authToken.shouldRenew();
    }
    
    /**
     * Verifies an auth token is still valid and should not be renewed.
     *
     * @param token  The auth token.
     * @param userId The id of the user that owns the auth token.
     * @return Whether the auth token is still valid and should not be renewed.
     */
    public static synchronized boolean verifyAuthTokenOwner(String token, int userId)
    {
        if (!authTokenExpired(token)) {
            AuthToken authToken = authTokenTokens.get(token);
            return authTokens.get(authToken).equals(userId) || authTokens.get(authToken).equals(userId);
        }
        return false;
    }
    
    /**
     * Determines the id of the user that owns an auth token.
     *
     * @param token The auth token.
     * @return The id of the owner of the auth token.
     */
    public static synchronized int getAuthTokenOwner(String token)
    {
        AuthToken authToken = authTokenTokens.get(token);
        if (authToken == null) {
            return -1;
        }
        return authTokens.get(authToken);
    }
    
    /**
     * Determines the id of the user that owns an auth token.
     *
     * @param authToken The auth token.
     * @param commId        The id of the communication channel used to encrypt the auth token.
     * @return The id of the owner of the auth token.
     */
    public static int getAuthTokenOwnerFromToken(String authToken, long commId)
    {
        //validate communication channel
        if (!CommunicationHandler.hasCommunicationId(commId)) {
            logger.warn("POST failed: Communication channel at: {} was never opened", commId);
            return -1;
        }
        
        //decrypt auth token
        String decryptedAuthToken = CommunicationHandler.decryptCommunication(commId, authToken);
        if (decryptedAuthToken.isEmpty()) {
            logger.warn("POST failed: Communication channel at: {} could not decrypt the auth token", commId);
            return -1;
        }
        
        //verify the auth token
        if (authTokenExpired(decryptedAuthToken)) {
            logger.warn("POST failed: Auth token: {} is expired", decryptedAuthToken);
            return -1;
        }
        
        return getAuthTokenOwner(decryptedAuthToken);
    }
    
    /**
     * Purges an expired auth token.
     *
     * @param token The auth token to purge.
     */
    public static synchronized void purgeAuthToken(String token)
    {
        AuthToken authToken = authTokenTokens.remove(token);
        authTokens.remove(authToken);
    }
    
    /**
     * Validates an auth token for a POST request.
     *
     * @param authToken     The auth token.
     * @param commId        The id of the communication channel used to encrypt the auth token.
     * @return A failure response if there was an error validating the auth token or null if the validation was successful.
     */
    public static Response validateAuthTokenForPost(String authToken, long commId)
    {
        //validate communication channel
//        if (!CommunicationHandler.hasCommunicationId(commId)) {
//            logger.warn("POST failed: Communication channel at: {} was never opened", commId);
//            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel was never opened").build();
//        }
        
        //decrypt auth token
        String decryptedAuthToken = CommunicationHandler.decryptCommunication(commId, authToken);
        if (decryptedAuthToken.isEmpty()) {
            logger.warn("POST failed: Communication channel at: {} could not decrypt the auth token", commId);
            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel could not decrypt the auth token").build();
        }
        
        //verify the auth token
        if (authTokenExpired(decryptedAuthToken)) {
            logger.warn("POST failed: Auth token: {} is expired", decryptedAuthToken);
            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The auth token used is expired").build();
        }
        
        //verify caller
        int owner = getAuthTokenOwner(decryptedAuthToken);
        if (owner > 100000) {
            logger.warn("POST failed: Auth token: {} does not belong to a producer", decryptedAuthToken);
            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The auth token used does not belong to a producer").build();
        }
        
        return null;
    }
    
    
    /**
     * Validates an auth token.
     *
     * @param authToken     The auth token.
     * @param commId        The id of the communication channel used to encrypt the auth token.
     * @return A failure response if there was an error validating the auth token or null if the validation was successful.
     */
    public static Response validateAuthToken(String authToken, long commId)
    {
        //validate communication channel
//        if (!CommunicationHandler.hasCommunicationId(commId)) {
//            logger.warn("POST failed: Communication channel at: {} was never opened", commId);
//            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel was never opened").build();
//        }
        
        //decrypt auth token
        String decryptedAuthToken = CommunicationHandler.decryptCommunication(commId, authToken);
        if (decryptedAuthToken.isEmpty()) {
            logger.warn("POST failed: Communication channel at: {} could not decrypt the auth token", commId);
            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel could not decrypt the auth token").build();
        }
        
        //verify the auth token
        if (authTokenExpired(decryptedAuthToken)) {
            logger.warn("POST failed: Auth token: {} is expired", decryptedAuthToken);
            return Response.status(Status.UNAUTHORIZED).header("message", "Failure: The auth token used is expired").build();
        }
        
        return null;
    }
    
}
