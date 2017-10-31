/*
 * File:    AuthorizeUser.java
 * Package: rest.user
 * Author:  Zachary Gill
 */

package rest.user;

import communication.CommunicationHandler;
import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;
import utility.AuthToken;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;

/**
 * Authorizes a user with the server.
 */
@Path("authorizeUser")
public class AuthorizeUser
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthorizeUser.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Authorizes a user with the server.
     *
     * @param user      The username of the user being authorized.
     * @param passHash  The password hash of the user being authorized encrypted with the AES key of a communication channel.
     * @param signature The client signature of the unencrypted password hash.
     * @param commId    The id of the communication channel being used to encrypt the password hash.
     * @return A response with the unique auth token for the user.
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
            logger.info("POST request to authorize user: {}", user);
            
            
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
            if ((idString == null) || (storedUser == null) || (storedPassHash == null) ||
                    storedUser.isEmpty() || storedPassHash.isEmpty() || idString.isEmpty()) {
                logger.warn("POST failed: Could not retrieve data about user: {}", user);
                return Response.status(Response.Status.NOT_FOUND).header("message", "Failure: Could not retrieve data about the user: " + user).build();
            }
            
            
            //validate user login
            
            if (!storedUser.equals(user) || !storedPassHash.equals(decryptedPassHash)) {
                logger.warn("POST failed: The user credentials are incorrect for the user: {}", user);
                return Response.status(Response.Status.EXPECTATION_FAILED).header("message", "Failure: The user credentials are incorrect for the user: " + user).build();
            }
            
            
            //generate auth token
            
            String token = AuthToken.generateAuthToken(Integer.valueOf(idString));
            String encryptedToken = CommunicationHandler.encryptCommunication(commId, token);
            
            
            //response
            
            logger.info("POST successful: User authorized with auth token: {}", token);
            return Response.ok()
                    .header("authToken", encryptedToken)
                    .header("message", "Success: User authorized").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
