/*
 * File:    GetRSAPublicKey.java
 * Package: rest.communication
 * Author:  Zachary Gill
 */

package rest.communication;

import org.glassfish.grizzly.http.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;
import utility.CryptoUtility;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Returns the RSA public key of the server.
 */
@Path("getRSAPublicKey")
public class GetRSAPublicKey
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(GetRSAPublicKey.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Returns the RSA public key of the server.
     *
     * @return A response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRSAPublicKey()
    {
        try {
            logger.info("GET request for server RSA public key");
    
    
            //get RSA public key
            
            String rsaPublicKey = CryptoUtility.storeRSAPublicKey(Server.getRSAKeys().getPublic());
    
    
            //response
            
            logger.info("GET successful");
            return Response.ok()
                    .header("rsaPublicKey", rsaPublicKey)
                    .header("message", "Success: Retrieved RSA public key").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
