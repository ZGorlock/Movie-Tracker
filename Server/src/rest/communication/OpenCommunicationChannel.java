/*
 * File:    OpenCommunicationChannel.java
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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.security.PublicKey;
import java.util.List;

/**
 * Opens a new communication channel with a client.
 */
@Path("openCommunicationChannel")
public class OpenCommunicationChannel
{
    
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(OpenCommunicationChannel.class);
    
    
    //Injections
    
    /**
     * Grizzly request provider.
     */
    @Inject
    private Provider<Request> request;
    
    
    //Methods
    
    /**
     * Opens a new communication channel with a client.
     *
     * @param rsaPublicKeyStore The RSA public key of the client.
     * @param dsaPublicKeyStore The DSA public key of the client.
     * @return A response with the id of the communication channel that was opened.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response openCommunicationChannel(
            @FormDataParam("rsaPublicKeyStore") String rsaPublicKeyStore,
            @FormDataParam("dsaPublicKeyStore") String dsaPublicKeyStore)
    {
        try {
            logger.info("POST request to open a communication channel");
    
    
            //open communication channel
    
            PublicKey rsaPublicKey = CryptoUtility.readRSAPublicKey(rsaPublicKeyStore);
            PublicKey dsaPublicKey = CryptoUtility.readDSAPublicKey(dsaPublicKeyStore);
            if ((rsaPublicKey == null) || (dsaPublicKey == null)) {
                logger.warn("POST failed, unable to open communication channel");
                return Response.status(Status.UNAUTHORIZED).header("message", "Failure: Unable to open communication channel").build();
            }
            
            long commId = CommunicationHandler.openCommunicationChannel(rsaPublicKey, dsaPublicKey, Server.getRSAKeys(), Server.getDSAKeys());
            List<String> keyStores = CommunicationHandler.getCommunicationChannelKeys(commId);
            if ((commId < 0) || (keyStores == null)) {
                logger.warn("POST failed, unable to open communication channel");
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("message", "Failure: Unable to open communication channel").build();
            }
            
            String aesKeyStore = keyStores.get(0);
            String rsaServerPublicKeyStore = keyStores.get(1);
            String dsaServerPublicKeyStore = keyStores.get(2);
    
    
            //response
    
            logger.info("POST successful: opened communication channel at: {}", commId);
            return Response.ok()
                    .header("commId", commId)
                    .header("aesKeyStore", aesKeyStore)
                    .header("rsaServerPublicKeyStore", rsaServerPublicKeyStore)
                    .header("dsaServerPublicKeyStore", dsaServerPublicKeyStore)
                    .header("message", "Success: Opened communication channel").build();
            
        } catch (Exception e) {
            logger.error(Server.stackTrace(e));
            return null;
        }
    }
    
}
