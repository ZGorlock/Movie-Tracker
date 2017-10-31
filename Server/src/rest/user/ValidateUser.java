/*
 * File:    ValidateUser.java
 * Package: rest.user
 * Author:  Zachary Gill
 */

package rest.user;

import communication.CommunicationHandler;
import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * Validates a user with the server.
 */
@Path("validateUser")
public class ValidateUser
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ValidateUser.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Constants
    
    /**
     * The maximum number of consecutive unsuccessful login attempts.
     */
    public static final int MAXIMUM_LOGIN_ATTEMPTS = 3;
    
    /**
     * The number of minutes before a login block is lifted.
     */
    public static final int MINUTES_TO_LIFT_LOGIN_BLOCK = 5;
    
    
    //Static Fields
    
    /**
     * A map from a username to the number of consecutive unsuccessful login attempts.
     */
    private static final Map<String, Integer> loginAttempts = new HashMap<>();
    
    /**
     * A map from a username to the time of the last instance of the maximum consecutive unsuccessful login attempts.
     */
    private static final Map<String, Long> holds = new HashMap<>();
    
    
    //Methods
    
    /**
     * Validates a user with the server.
     *
     * @param user      The username of the user being validated.
     * @param passHash  The password hash of the user being validated encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @return A response with the unique hex id of the user.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerUser(
            @FormDataParam("user") String user,
            @FormDataParam("passHash") String passHash,
            @FormDataParam("signature") String signature,
            @FormDataParam("commId") long commId)
    {
        try {
            logger.info("POST request to validate user: {}", user);
            
            //check for spam
            
            if (holds.containsKey(user)) {
                long blockTime = holds.get(user);
                if ((System.currentTimeMillis() - blockTime) < (MINUTES_TO_LIFT_LOGIN_BLOCK * 60 * 1000)) {
                    int millisToWait = (int) ((MINUTES_TO_LIFT_LOGIN_BLOCK * 60 * 1000) - (System.currentTimeMillis() - blockTime));
                    int minutesToWait = millisToWait / (60 * 1000);
                    int secondsToWait = (millisToWait - (minutesToWait * 60 * 1000)) / 1000;
                    return Response.status(Response.Status.FORBIDDEN).header("message", "Failure: You attempted " + MAXIMUM_LOGIN_ATTEMPTS + " login attempts unsuccessfully; please wait " + minutesToWait + " minutes and " + secondsToWait + " seconds before attempting to log in again").build();
                } else {
                    holds.remove(user);
                }
            }
            
            
            logger.info("POST request to validate user: {}", user);
            
            
            //ensure the user is registered
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM user WHERE name = ?");
            if (s == null) {
                return null;
            }
            s.setString(1, user);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            Integer count = r.getIntResult("COUNT(id)", 0);
            if ((count == null) || (count == 0)) {
                logger.warn("POST failed: User: {} is not registered on the server", user);
                return Response.status(Response.Status.NOT_FOUND).header("message", "Failure: User: " + user + " is not registered on the server").build();
            }
            
            
            //validate communication channel
            
            if (!CommunicationHandler.hasCommunicationId(commId)) {
                logger.warn("POST failed: Communication channel at: {} was never opened", commId);
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel was never opened").build();
            }
            
            
            //decrypt pass hash
            
            String decryptedPassHash = CommunicationHandler.decryptCommunication(commId, passHash);
            if (decryptedPassHash.isEmpty()) {
                logger.warn("POST failed: Communication channel at: {} could not decrypt the password hash", commId);
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel could not decrypt the password hash").build();
            }
            
            
            //verify signature
            
            if (!CommunicationHandler.verifyCommunication(commId, decryptedPassHash, signature)) {
                logger.warn("POST failed: Communication channel at: {} could not verify the client signature", commId);
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel could not verify the client signature").build();
            }
            
            
            //get user login
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("SELECT * FROM user WHERE name = ?");
            if (s2 == null) {
                return null;
            }
            s2.setString(1, user);
            FormattedResultSet r2 = DatabaseAccess.querySqlFormatResponse(s2);
            DatabaseAccess.closeStatement(s2);
            
            String idString = r2.getStringResult("id", 0);
            String storedUser = r2.getStringResult("name", 0);
            String storedPassHash = r2.getStringResult("pass", 0);
            String email = r2.getStringResult("email", 0);
            String firstName = r2.getStringResult("firstName", 0);
            String lastName = r2.getStringResult("lastName", 0);
            String producer = r2.getStringResult("producer", 0);
            if ((storedUser.isEmpty() || storedPassHash.isEmpty()) || idString.isEmpty()) {
                logger.warn("POST failed: Could not retrieve data about user: {}", user);
                return Response.status(Response.Status.NOT_FOUND).header("message", "Failure: Could not retrieve data about the user: " + user).build();
            }
            
            
            //validate user login
            
            if (!storedUser.equals(user) || !storedPassHash.equals(decryptedPassHash)) {
                logger.warn("POST failed: The user credentials are incorrect for the user: {}", user);
                
                int attempts = 0;
                if (loginAttempts.containsKey(user)) {
                    attempts = loginAttempts.get(user);
                }
                attempts++;
                if (attempts == MAXIMUM_LOGIN_ATTEMPTS) {
                    holds.put(user, System.currentTimeMillis());
                    loginAttempts.remove(user);
                } else {
                    loginAttempts.put(user, attempts);
                }
                
                return Response.status(Response.Status.EXPECTATION_FAILED).header("message", "Failure: The user credentials are incorrect for the user: " + user).build();
            }
            
            
            //response
    
            JSONObject json = new JSONObject();
            json.put("userId", idString);
            json.put("userName", storedUser);
            json.put("email", email);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("producer", producer);
            
            logger.info("POST successful: User validated");
            
            if (loginAttempts.containsKey(user)) {
                loginAttempts.remove(user);
            }
            
            return Response.ok()
                    .header("userInfo", json.toString())
                    .header("message", "Success: User validated").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
}
