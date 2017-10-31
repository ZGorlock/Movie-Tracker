/*
 * File:    EditMedia.java
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
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.util.UUID;

/**
 * Edits a media to the server.
 */
@Path("editMedia")
public class EditMedia
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(EditMedia.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Edits a media to the server.
     *
     * @param file        The image file.
     * @param fileDetail  Details about the image file.
     * @param mediaId     The id of the Media to be edited.
     * @param title       The title of the Media.
     * @param type        The type of the Media.
     * @param description The description of the Media.
     * @param genre       The genre of the Media.
     * @param actors      The actors of the Media.
     * @param showtimes   The showtimes of the Media.
     * @param rating      The rating of the Media.
     * @param year        The year of the Media.
     * @param authToken The auth token of the user making the call.
     * @param commId    The id of the communication channel being used to encrypt the auth token.
     * @return A response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editMedia(
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("mediaId") int mediaId,
            @FormDataParam("title") String title,
            @FormDataParam("type") String type,
            @FormDataParam("description") String description,
            @FormDataParam("genre") String genre,
            @FormDataParam("actors") String actors,
            @FormDataParam("showtimes") String showtimes,
            @FormDataParam("rating") String rating,
            @FormDataParam("year") int year,
            @FormDataParam("authToken") String authToken,
            @FormDataParam("commId") long commId)
    {
        try {
            logger.info("POST request to edit media: {}", mediaId);
            
            
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
            
            String oldImage = r.getStringResult("image", 0);
    
    
            //create the image
            
            String image = "";
            if (file != null && !oldImage.contains(fileDetail.getFileName())) {
                image = UUID.randomUUID().toString() + '.' + fileDetail.getType();
                File tmpDir = new File("/home/movie-tracker/images/" + image);
                Files.createFile(Paths.get(tmpDir.getAbsolutePath()));
    
                //receive the file transfer
    
                try {
                    FileOutputStream fos = new FileOutputStream(tmpDir);
                    byte[] buffer = new byte[Server.BUFFER_SIZE];
        
                    while (true) {
                        int read;
                        if ((read = file.read(buffer, 0, buffer.length)) == -1) {
                            fos.flush();
                            fos.close();
                            break;
                        }
            
                        fos.write(buffer, 0, read);
                    }
                } catch (IOException e) {
                    logger.error("{} | POST failed: Image could not be written");
                    logger.error(Server.stackTrace(e));
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Image could not be written").build();
                }
            }
            
            
            //insert Media into the database
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("UPDATE media SET title = ?, type = ?, description = ?, genre = ?, actors = ?, image = ?, showtimes = ?, rating = ?, year = ? WHERE id = ?");
            if (s2 == null) {
                return null;
            }
            s2.setString(1, title);
            s2.setString(2, type);
            s2.setString(4, description);
            s2.setString(5, genre);
            s2.setString(6, actors);
            s2.setString(7, image);
            s2.setString(8, showtimes);
            s2.setString(9, rating);
            s2.setInt(10, year);
            s2.setInt(11, mediaId);
            
            if (!DatabaseAccess.executeSql(s2)) {
                DatabaseAccess.rollbackChanges();
                DatabaseAccess.closeStatement(s2);
                logger.warn("{} | POST failed: Media: {} by: {} could not be updated in the database", title, producerId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Media: " + title + " by: " + producerId + " could not be updated in the database").build();
            }
            DatabaseAccess.closeStatement(s2);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("{} | POST successful: Media edited");
            return Response.ok().header("message", "Success: Media edited").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
