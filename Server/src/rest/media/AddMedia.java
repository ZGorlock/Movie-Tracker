/*
 * File:    AddMedia.java
 * Package: rest.media
 * Author:  Zachary Gill
 */

package rest.media;

import database.DatabaseAccess;
import database.FormattedResultSet;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
 * Adds a media to the server.
 */
@Path("addMedia")
public class AddMedia
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AddMedia.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Adds a media to the server.
     *
     * @param file        The image file.
     * @param fileDetail  Details about the image file.
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
    public Response addMedia(
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
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
            logger.info("POST request to add media: {}", title);
            
            
            //validate auth token
            
            Response validateAuthToken = AuthToken.validateAuthTokenForPost(authToken, commId);
            if (validateAuthToken != null) {
                return validateAuthToken;
            }
            int producerId = AuthToken.getAuthTokenOwnerFromToken(authToken, commId);
            
            
            //ensure the Media is not already added
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM media WHERE title = ? AND producerId = ?");
            if (s == null) {
                return null;
            }
            s.setString(1, title);
            s.setInt(2, producerId);
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            Integer count = r.getIntResult("COUNT(id)", 0);
            
            
            if ((count == null) || (count > 0)) {
                DatabaseAccess.closeStatement(s);
                logger.warn("POST ignored: Media: {} by: {} already exists on the server", title, producerId);
                return Response.status(Response.Status.CONFLICT).header("message", "Failure:  Media: " + title + " by: " + producerId + " already exists on the server").build();
            }
    
    
            //generate new media id
    
            boolean unique = false;
            int id = -1;
            while (!unique) {
                id = (int) ((new SecureRandom().nextDouble() * 268435455) + 1);
        
                PreparedStatement s2 = DatabaseAccess.getPreparedStatement("SELECT COUNT(id) FROM media WHERE id = ?");
                if (s2 == null) {
                    return null;
                }
                s2.setInt(1, id);
        
                FormattedResultSet r2 = DatabaseAccess.querySqlFormatResponse(s2);
                DatabaseAccess.closeStatement(s2);
        
                count = r2.getIntResult("COUNT(id)", 0);
                unique = (count != null) && (count == 0);
            }
    
    
            //create the image
            
            String image = "";
            if (file != null) {
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
            
            PreparedStatement s2 = DatabaseAccess.getPreparedStatement("INSERT INTO media VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            if (s2 == null) {
                return null;
            }
            s2.setInt(1, id);
            s2.setString(2, title);
            s2.setString(3, type);
            s2.setInt(4, producerId);
            s2.setString(5, description);
            s2.setString(6, genre);
            s2.setString(7, actors);
            s2.setString(8, image);
            s2.setString(9, showtimes);
            s2.setString(10, rating);
            s2.setInt(11, year);
            
            if (!DatabaseAccess.executeSql(s2)) {
                DatabaseAccess.rollbackChanges();
                DatabaseAccess.closeStatement(s2);
                logger.warn("{} | POST failed: Media: {} by: {} could not be added to the database", title, producerId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Media: " + title + " by: " + producerId + " could not be added to the database").build();
            }
            DatabaseAccess.closeStatement(s2);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("{} | POST successful: Media added");
            return Response.ok().header("message", "Success: Media added").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
