/*
 * File:    GetDSAPublicKey.java
 * Package: rest.communication
 * Author:  Zachary Gill
 */

package rest.communication;

import org.glassfish.grizzly.http.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;
import utility.CryptoUtility;
import utility.StringUtility;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Returns the DSA public key of the server.
 */
@Path("getDSAPublicKey")
public class GetDSAPublicKey
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(GetDSAPublicKey.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Returns the DSA public key of the server.
     *
     * @return A response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDSAPublicKey()
    {
        try {
           
            logger.info("GET request for server DSA public key");
    
    
            //get DSA public key
            
            String dsaPublicKey = CryptoUtility.storeDSAPublicKey(Server.getDSAKeys().getPublic());
    
    
            //response
            
            logger.info("GET successful");
            return Response.ok()
                    .header("dsaPublicKey", dsaPublicKey)
                    .header("message", "Success: Retrieved DSA public key").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
