/*
 * File:    RemoveSubscription.java
 * Package: rest.subscription
 * Author:  Zachary Gill
 */

package rest.subscription;

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
import java.security.SecureRandom;
import java.sql.PreparedStatement;

/**
 * Removes a subscription for a user from the server.
 */
@Path("removeSubscription")
public class RemoveSubscription
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RemoveSubscription.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Removes a subscription for a user from the server.
     *
     * @param mediaId   The media to remove the subscription for.
     * @param authToken The auth token of the user making the call.
     * @param commId    The id of the communication channel being used to encrypt the auth token.
     * @return A response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeSubscription(
            @FormDataParam("mediaId") int mediaId,
            @FormDataParam("authToken") String authToken,
            @FormDataParam("commId") long commId)
    {
        try {
            logger.info("POST request to remove subscription to: {}", mediaId);
            
            
            //validate auth token
            
            Response validateAuthToken = AuthToken.validateAuthToken(authToken, commId);
            if (validateAuthToken != null) {
                return validateAuthToken;
            }
            int userId = AuthToken.getAuthTokenOwnerFromToken(authToken, commId);
            
            
            //ensure the Subscription exists
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM subscription WHERE userId = ? AND mediaId = ?");
            if (s == null) {
                return null;
            }
            s.setInt(1, userId);
            s.setInt(2, mediaId);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            Integer count = r.getIntResult("COUNT(id)", 0);
            
            
            if ((count == null) || (count != 1)) {
                DatabaseAccess.closeStatement(s);
                logger.warn("POST ignored: Subscription to: {} for User: {} does not exists on the server", mediaId, userId);
                return Response.status(Response.Status.CONFLICT).header("message", "Failure: Subscription to: " + mediaId + " for User: " + userId + " does not exist on the server").build();
            }
            
            
            //remove Subscription from the database
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("DELETE FROM subscription WHERE userId = ? AND mediaId = ?");
            if (s2 == null) {
                return null;
            }
            s2.setInt(1, userId);
            s2.setInt(2, mediaId);
            
            if (!DatabaseAccess.executeSql(s2)) {
                DatabaseAccess.rollbackChanges();
                DatabaseAccess.closeStatement(s2);
                logger.warn("POST failed: Subscription to: {} for User: {} could not be removed from the database", mediaId, userId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Subscription to: " + mediaId + " for User: " + userId + " could not be removed from the database").build();
            }
            DatabaseAccess.closeStatement(s2);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("POST successful: Subscription removed");
            return Response.ok().header("message", "Success: Subscription removed").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
