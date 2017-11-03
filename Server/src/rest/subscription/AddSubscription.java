/*
 * File:    AddSubscription.java
 * Package: rest.subscription
 * Author:  Zachary Gill
 */

package rest.subscription;

import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.media.AddMedia;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.util.UUID;

/**
 * Adds a subscription for a user to the server.
 */
@Path("addSubscription")
public class AddSubscription
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AddSubscription.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Adds a subscription for a user to the server.
     *
     * @param mediaId   The media to add a subscription for.
     * @param authToken The auth token of the user making the call.
     * @param commId    The id of the communication channel being used to encrypt the auth token.
     * @return A response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addSubscription(
            @FormDataParam("mediaId") int mediaId,
            @FormDataParam("authToken") String authToken,
            @FormDataParam("commId") long commId)
    {
        try {
            logger.info("POST request to add subscription to: {}", mediaId);
            
            
            //validate auth token
            
            Response validateAuthToken = AuthToken.validateAuthToken(authToken, commId);
            if (validateAuthToken != null) {
                return validateAuthToken;
            }
            int userId = AuthToken.getAuthTokenOwnerFromToken(authToken, commId);
            
            
            //ensure the Subscription is not already added
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM subscription WHERE userId = ? AND mediaId = ?");
            if (s == null) {
                return null;
            }
            s.setInt(1, userId);
            s.setInt(2, mediaId);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            Integer count = r.getIntResult("COUNT(id)", 0);
            
            
            if ((count == null) || (count > 0)) {
                DatabaseAccess.closeStatement(s);
                logger.warn("POST ignored: Subscription to: {} for User: {} already exists on the server", mediaId, userId);
                return Response.status(Response.Status.CONFLICT).header("message", "Failure: Subscription to: " + mediaId + " for User: " + userId + " already exists on the server").build();
            }
            
            
            //generate new subscription id
            
            boolean unique = false;
            int id = -1;
            while (!unique) {
                id = (int) ((new SecureRandom().nextDouble() * 2147483646) + 1);
                
                PreparedStatement s2 = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM subscription WHERE id = ?");
                if (s2 == null) {
                    return null;
                }
                s2.setInt(1, id);
                
                FormattedResultSet r2 = DatabaseAccess.querySqlFormatResponse(s2);
                DatabaseAccess.closeStatement(s2);
                
                count = r2.getIntResult("COUNT(id)", 0);
                unique = (count != null) && (count == 0);
            }
            
            
            //insert Subscription into the database
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("INSERT INTO subscription VALUES(?, ?, ?)");
            if (s2 == null) {
                return null;
            }
            s2.setInt(1, id);
            s2.setInt(2, userId);
            s2.setInt(3, mediaId);
            
            if (!DatabaseAccess.executeSql(s2)) {
                DatabaseAccess.rollbackChanges();
                DatabaseAccess.closeStatement(s2);
                logger.warn("POST failed: Subscription to: {} for User: {} could not be added to the database", mediaId, userId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Subscription to: " + mediaId + " for User: " + userId + " could not be added to the database").build();
            }
            DatabaseAccess.closeStatement(s2);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("POST successful: Subscription added");
            return Response.ok().header("message", "Success: Subscription added").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
