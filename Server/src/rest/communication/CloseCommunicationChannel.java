/*
 * File:    CloseCommunicationChannel.java
 * Package: rest.communication
 * Author:  Zachary Gill
 */

package rest.communication;

import communication.CommunicationHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;
import utility.CryptoUtility;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.security.PrivateKey;

/**
 * Closes a communication channel with a client.
 */
@Path("closeCommunicationChannel")
public class CloseCommunicationChannel
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CloseCommunicationChannel.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Closes a communication channel with a client.
     *
     * @param commId      The id of the communication channel to close.
     * @param aesKeyStore The AES key of the client encrypted with the RSA public key of the server.
     * @return A response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response closeCommunicationChannel(
            @FormDataParam("commId") long commId,
            @FormDataParam("aesKeyStore") String aesKeyStore)
    {
        try {
            logger.info("POST request to close a communication channel at: {}", commId);
    
    
            //close communication channel
    
            PrivateKey rsaPrivateKey = Server.getRSAKeys().getPrivate();
            String decryptedAesKeyStore = CryptoUtility.decryptRSA(aesKeyStore, rsaPrivateKey);
            SecretKey aesKey = CryptoUtility.readAESSecret(decryptedAesKeyStore);
            
            if (!CommunicationHandler.closeCommunicationChannel(commId, aesKey)) {
                logger.warn("POST failed, unable to close communication channel at: {}", commId);
                return Response.status(Status.UNAUTHORIZED).header("message", "Failure: Unable to close communication channel").build();
            }
    
    
            //response
    
            logger.info("POST successful: closed communication channel at: {}", commId);
            return Response.ok().header("message", "Success: Closed communication channel").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
