/*
 * File:    RegisterUser.java
 * Package: rest
 * Author:  Zachary Gill
 */

package rest;

import communication.CommunicationHandler;
import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
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
import java.security.SecureRandom;
import java.sql.PreparedStatement;

/**
 * Registers a user on the server.
 */
@Path("registerUser")
public class RegisterUser
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegisterUser.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Constants
    
    /**
     * The maximum length of a user name.
     */
    public static final int MAX_USERNAME_LENGTH = 32;
    
    /**
     * The expected length of SHA-512 password hash.
     */
    public static final int PASSWORD_HASH_LENGTH = 128;
    
    
    //Methods
    
    /**
     * Registers a user on the server.
     *
     * @param user  The username of the user being registered.
     * @param passHash  The password hash of the user being registered encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @param email     The email of the user.
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param producer  Whether the user is a producer or not.
     * @return A response with the unique id of the newly registered user.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerUser(
            @FormDataParam("user") String user,
            @FormDataParam("passHash") String passHash,
            @FormDataParam("signature") String signature,
            @FormDataParam("commId") long commId,
            @FormDataParam("email") String email,
            @FormDataParam("firstName") String firstName,
            @FormDataParam("lastName") String lastName,
            @FormDataParam("producer") String producer
            )
    {
        
        try {
            logger.info("POST request to register user: {}", user);
            
            //pre-validation of data
            
            boolean alphanumeric = true;
            for (int i = 0; i < user.length(); i++) {
                char c = user.charAt(i);
                if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                    alphanumeric = false;
                }
            }
            if (!alphanumeric) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).header("message", "Failure: Registration of User failed: User name must not contain special characters").build();
            }
            
            
            //ensure the user is not already registered
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM user WHERE name = ?");
            if (s == null) {
                return null;
            }
            s.setString(1, user);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            Integer count = r.getIntResult("COUNT(id)", 0);
            if ((count == null) || (count > 0)) {
                return Response.status(Response.Status.CONFLICT).header("message", "Failure: User: " + user + " is already registered on the server").build();
            }
            
            
            //ensure the username is valid
            
            if (user.length() > MAX_USERNAME_LENGTH) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).header("message", "Failure: The username of a user must be " + MAX_USERNAME_LENGTH + " characters or less").build();
            }
            
            
            //validate communication channel
            
            if (!CommunicationHandler.hasCommunicationId(commId)) {
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel was never opened").build();
            }
            
            
            //decrypt pass hash
            
            String decryptedPassHash = CommunicationHandler.decryptCommunication(commId, passHash);
            if (decryptedPassHash.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel could not decrypt the password hash").build();
            }
            
            
            //ensure the password hash is valid
            
            if (decryptedPassHash.length() != PASSWORD_HASH_LENGTH) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).header("message", "Failure: The password hash of a user must be an SHA-512 hash string of length " + PASSWORD_HASH_LENGTH).build();
            }
            
            
            //verify signature
            
            if (!CommunicationHandler.verifyCommunication(commId, decryptedPassHash, signature)) {
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure: The specified communication channel could not verify the client signature").build();
            }
            
            
            //generate new user id
            
            String idString = "";
            boolean unique = false;
            while (!unique) {
                long id;
                if (producer.equals("y")) {
                    id = (long) ((new SecureRandom().nextDouble() * 100000) + 1);
                } else {
                    id = (long) ((new SecureRandom().nextDouble() * 268435455L) + 100001);
                }
                idString = Long.toString(id);
                
                PreparedStatement s2 = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM user WHERE id = ?");
                if (s2 == null) {
                    return null;
                }
                s2.setString(1, idString);
                
                FormattedResultSet r2 = DatabaseAccess.querySqlFormatResponse(s2);
                DatabaseAccess.closeStatement(s2);
                
                count = r2.getIntResult("COUNT(id)", 0);
                unique = (count != null) && (count == 0);
            }
            
            
            //insert user into the database
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("INSERT INTO user VALUES(?, ?, ?, ?, ?, ?, ?)");
            if (s2 == null) {
                return null;
            }
            s2.setString(1, idString);
            s2.setString(2, user);
            s2.setString(3, decryptedPassHash);
            s2.setString(4, email);
            s2.setString(5, firstName);
            s2.setString(6, lastName);
            s2.setString(7, producer);
            if (!DatabaseAccess.executeSql(s2)) {
                DatabaseAccess.rollbackChanges();
                DatabaseAccess.closeStatement(s2);
                logger.warn("POST failed: User: {} could not be added to the database", user);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: User: " + user + " could not be added to the database").build();
            }
            DatabaseAccess.closeStatement(s2);
            
            
            //response
            DatabaseAccess.commitChanges();
            logger.info("POST successful: User registered");
            return Response.ok()
                    .header("userId", idString)
                    .header("message", "Success: User registered").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
