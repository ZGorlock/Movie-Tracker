/*
 * File:    QueryMedia.java
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
import java.util.List;
import java.util.UUID;

/**
 * Queries media on the server.
 */
@Path("queryMedia")
public class QueryMedia
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(QueryMedia.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Queries media on the server.
     *
     * @param title       The title query.
     * @param type        The type query.
     * @param producerId  The producer id query.
     * @param description The description query.
     * @param genre       The genre query.
     * @param actors      The actors query.
     * @param showtimes   The showtimes query.
     * @param rating      The rating query.
     * @param year        The year query.
     * @return A response with a list of media ids that satisfy the query.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response queryMedia(
            @FormDataParam("title") String title,
            @FormDataParam("type") String type,
            @FormDataParam("producerId") int producerId,
            @FormDataParam("description") String description,
            @FormDataParam("genre") String genre,
            @FormDataParam("actors") String actors,
            @FormDataParam("showtimes") String showtimes,
            @FormDataParam("rating") String rating,
            @FormDataParam("year") int year)
    {
        try {
            logger.info("POST request to query for media");
            
            
            //create query string
            
            StringBuilder query = new StringBuilder();
            query.append("SELECT id FROM media");
            
            StringBuilder params = new StringBuilder();
            if (!title.isEmpty() && !title.contains(";")) {
                params.append(" title LIKE '%").append(title).append("%'");
            }
            if (!type.isEmpty() && !type.contains(";")) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" type LIKE '%").append(type).append("%'");
            }
            if (producerId != 0) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" producerId = ").append(producerId);
            }
            if (!description.isEmpty() && !description.contains(";")) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" description LIKE '%").append(description).append("%'");
            }
            if (!genre.isEmpty() && !genre.contains(";")) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" genre LIKE '%").append(genre).append("%'");
            }
            if (!actors.isEmpty() && !actors.contains(";")) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" actors LIKE '%").append(actors).append("%'");
            }
            if (!showtimes.isEmpty() && !showtimes.contains(";")) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" showtimes LIKE '%").append(showtimes).append("%'");
            }
            if (!rating.isEmpty() && !rating.contains(";")) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" rating LIKE '%").append(rating).append("%'");
            }
            if (year != 0) {
                params.append((params.toString().isEmpty() ? "" : " AND"));
                params.append(" year = ").append(year);
            }
            
            if (!params.toString().isEmpty()) {
                query.append(" WHERE");
                query.append(params.toString());
            }
            
            
            //query for the Media
            
            PreparedStatement s = DatabaseAccess.getPreparedStatement(query.toString());
            if (s == null) {
                return null;
            }
            
            FormattedResultSet r = DatabaseAccess.querySqlFormatResponse(s);
            DatabaseAccess.closeStatement(s);
            
            List<Object> result = r.getColumn("id");
            
            
            //generate JSON
    
            JSONObject json = new JSONObject();
            JSONArray results = new JSONArray();
            for (Object o : result) {
                results.add(Integer.valueOf(o.toString()));
            }
            json.put("results", results);
            
            
            //response
            
            DatabaseAccess.commitChanges();
            logger.info("POST successful: Media queried");
            return Response.ok()
                    .header("results", json.toString())
                    .header("message", "Success: Media queried").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
