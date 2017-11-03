/*
 * File:    GetSubscriptions.java
 * Package: rest.subscription
 * Author:  Zachary Gill
 */

package rest.subscription;

import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import java.util.List;

/**
 * Gets the subscription for a user from the server.
 */
@Path("getSubscriptions")
public class GetSubscriptions
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(GetSubscriptions.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Gets the subscription for a user from the server.
     *
     * @param authToken The auth token of the user making the call.
     * @param commId    The id of the communication channel being used to encrypt the auth token.
     * @return A response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSubscriptions(
            @FormDataParam("authToken") String authToken,
            @FormDataParam("commId") long commId)
    {
        try {
            logger.info("POST request to get subscriptions");
            
            
            //validate auth token
            
            Response validateAuthToken = AuthToken.validateAuthToken(authToken, commId);
            if (validateAuthToken != null) {
                return validateAuthToken;
            }
            int userId = AuthToken.getAuthTokenOwnerFromToken(authToken, commId);
            
            
            //get Subscriptions from the database
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT mediaId FROM subscription WHERE userId = ?");
            if (s == null) {
                return null;
            }
            s.setInt(1, userId);
    
    
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            
            List<Object> result = r.getColumn("mediaId");
    
    
            //generate JSON
    
            JSONObject json = new JSONObject();
            JSONArray results = new JSONArray();
            for (Object o : result) {
                results.add(Integer.valueOf(o.toString()));
            }
            json.put("subscriptions", results);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("POST successful: Subscriptions retrieved");
            return Response.ok()
                    .header("subscriptions", json.toString())
                    .header("message", "Success: Subscriptions retrieved").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
