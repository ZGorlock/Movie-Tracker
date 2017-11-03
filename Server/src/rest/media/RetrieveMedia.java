/*
 * File:    RetrieveMedia.java
 * Package: rest.media
 * Author:  Zachary Gill
 */

package rest.media;

import communication.CommunicationHandler;
import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.user.ValidateUser;
import server.Server;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * Retrieves a Media from the server.
 */
@Path("retrieveMedia")
public class RetrieveMedia
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RetrieveMedia.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Retrieves a Media from the server.
     *
     * @param mediaId The id of the Media to retrieve.
     * @return A response with the Media's image as an attachment.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response registerUser(
            @FormDataParam("mediaId") int mediaId)
    {
        try {
            logger.info("POST request to retrieve Media: {}", mediaId);
            
            
            //ensure the Media exists
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM media WHERE id = ?");
            if (s == null) {
                return null;
            }
            s.setInt(1, mediaId);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            Integer count = r.getIntResult("COUNT(id)", 0);
            if ((count == null) || (count == 0)) {
                logger.warn("POST failed: Media: {} does not exist on the server", mediaId);
                return Response.status(Response.Status.NOT_FOUND).header("message", "Failure: Media: " + mediaId + " does not exist on the server").build();
            }
            
            
            //get Media data
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("SELECT * FROM media WHERE id = ?");
            if (s2 == null) {
                return null;
            }
            s2.setInt(1, mediaId);
            FormattedResultSet r2 = DatabaseAccess.querySqlFormatResponse(s2);
            DatabaseAccess.closeStatement(s2);
            
            String idString = r2.getStringResult("id", 0);
            String title = r2.getStringResult("title", 0);
            String type = r2.getStringResult("type", 0);
            String producerId = r2.getStringResult("producerId", 0);
            String description = r2.getStringResult("description", 0);
            String genre = r2.getStringResult("genre", 0);
            String actors = r2.getStringResult("actors", 0);
            String image = r2.getStringResult("image", 0);
            String showtimes = r2.getStringResult("showtimes", 0);
            String rating = r2.getStringResult("rating", 0);
            String year = r2.getStringResult("year", 0);
    
    
            //open a stream for the file transfer
    
            FileInputStream is = null;
            File imageFile = null;
            if (!image.isEmpty()) {
                imageFile = new File(Server.IMAGES_DIR + image);
                try {
                    is = new FileInputStream(imageFile);
                } catch (FileNotFoundException e) {
                    logger.error("GET failed: {} could not be opened", imageFile.getName());
                    logger.error(Server.stackTrace(e));
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: " + imageFile.getName() + " could not be opened").build();
                }
            }
            
            //response
            
            JSONObject json = new JSONObject();
            json.put("mediaId", idString);
            json.put("title", title);
            json.put("type", type);
            json.put("producerId", producerId);
            json.put("description", description);
            json.put("genre", genre);
            json.put("actors", actors);
            json.put("image", (imageFile == null) ? "" : imageFile.getName());
            json.put("showtimes", showtimes);
            json.put("rating", rating);
            json.put("year", year);
            
            logger.info("POST successful: Media retrieved");
            
            return Response.ok(is)
                    .header("Content-Disposition", "attachment; filename=" + ((imageFile == null) ? "" : imageFile.getName()) + "; size=" + ((imageFile == null) ? 0 : imageFile.length()) + ';')
                    .header("mediaInfo", json.toString())
                    .header("message", "Success: Media retrieved").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
}
