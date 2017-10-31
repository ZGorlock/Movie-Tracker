/*
 * File:    DeleteMedia.java
 * Package: rest.media
 * Author:  Zachary Gill
 */

package rest.media;

import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.UUID;

/**
 * Deletes a media from the server.
 */
@Path("deleteMedia")
public class DeleteMedia
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DeleteMedia.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Deletes a media from the server.
     *
     * @param mediaId     The id of the Media to be deleted.
     * @param authToken   The auth token of the user making the call.
     * @param commId      The id of the communication channel being used to encrypt the auth token.
     * @return A response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteMedia(
            @FormDataParam("mediaId") int mediaId,
            @FormDataParam("authToken") String authToken,
            @FormDataParam("commId") long commId)
    {
        try {
            logger.info("POST request to delete media: {}", mediaId);
            
            
            //validate auth token
            
            Response validateAuthToken = AuthToken.validateAuthTokenForPost(authToken, commId);
            if (validateAuthToken != null) {
                return validateAuthToken;
            }
            int producerId = AuthToken.getAuthTokenOwnerFromToken(authToken, commId);
            
            
            //ensure the Media is exists and was originally added by the producer
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT * FROM media WHERE id = ?");
            if (s == null) {
                return null;
            }
            s.setInt(1, mediaId);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            
            if (r.getColumnSize("id") == 0) {
                logger.warn("POST ignored: Media: {} does not exist on the server", mediaId);
                return Response.status(Response.Status.CONFLICT).header("message", "Failure:  Media: " + mediaId + " does not exist on the server").build();
            }
            
            Integer producer = r.getIntResult("producerId", 0);
            if ((producer == null) || (producer != producerId)) {
                logger.warn("POST ignored: Media: {} does not belong to user: {}", mediaId, producerId);
                return Response.status(Response.Status.UNAUTHORIZED).header("message", "Failure:  Media: " + mediaId + " does not belong to user: " + producerId).build();
            }
            
            
            //delete the Media from the database
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("DELETE FROM media WHERE id = ?");
            if (s2 == null) {
                return null;
            }
            s2.setInt(1, mediaId);
            
            if (!DatabaseAccess.executeSql(s2)) {
                DatabaseAccess.rollbackChanges();
                DatabaseAccess.closeStatement(s2);
                logger.warn("{} | POST failed: Media: {} by: {} could not be delete from the database", mediaId, producerId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Media: " + mediaId + " by: " + producerId + " could not be deleted from the database").build();
            }
            DatabaseAccess.closeStatement(s2);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("{} | POST successful: Media deleted");
            return Response.ok().header("message", "Success: Media deleted").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
