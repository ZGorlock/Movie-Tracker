/*
 * File:    server.Server.java
 * Package: server
 * Author:  Zachary Gill
 */

package server;

import database.DatabaseAccess;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.CryptoUtility;

import java.io.*;
import java.net.URI;
import java.security.KeyPair;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main class for REST server.Server.
 */
public class Server
{
    
    
    //Logger
    
    static { //set logback configuration file
        System.setProperty("logback.configurationFile", "resources/logback.xml");
    }
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    
    
    //Constants
    
    /**
     * The base uri for REST endpoints.
     */
    public static final String BASE_URI = "http://0.0.0.0:4444/";
    
    /**
     * The number of milliseconds in between server key updates.
     */
    public static final long KEY_UPDATE_FREQUENCY = 86400000;
    
    
    //Static Fields
    
    /**
     * The DSA keys of the server.
     */
    private static KeyPair dsaKeys = null;
    
    /**
     * The RSA keys of the server.
     */
    private static KeyPair rsaKeys = null;
    
    /**
     * The timer to dictate when server keys are updated.
     */
    private static Timer keyTimer = null;
    
    
    //Main Method
    
    /**
     * Main method for the DLA server.Server.
     *
     * @param args Command line arguments to the main method.
     */
    public static void main(String[] args)
    {
        final ResourceConfig rc = new ResourceConfig().packages("rest");
        rc.register(MultiPartFeature.class);
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        
        logger.info("Server started at {}", BASE_URI);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down server...");
            server.shutdownNow();
            DatabaseAccess.shutdown();
            logger.info("Server shutdown!");
        }, "shutdownHook"));
        
        if (!DatabaseAccess.setup()) {
            System.exit(0);
        }
        
        keyTimer = new Timer("Server Key Update Timer");
        keyTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                dsaKeys = CryptoUtility.generateDSAKeyPair();
                rsaKeys = CryptoUtility.generateRSAKeyPair();
            }
        }, 0, KEY_UPDATE_FREQUENCY);
        
        logger.info("Server ready!");
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
            logger.error("There was an error shutting down the Server");
        }
        System.exit(0);
    }
    
    
    //Getters
    
    /**
     * Returns the DSA keys of the server.
     *
     * @return The DSA keys of the server.
     */
    public static KeyPair getDSAKeys()
    {
        return dsaKeys;
    }
    
    /**
     * Returns the RSA keys of the server.
     *
     * @return The RSA keys of the server.
     */
    public static KeyPair getRSAKeys()
    {
        return rsaKeys;
    }
    
    
    //Functions
    
    /**
     * Executes a script on the server.
     *
     * @param script The script to execute.
     * @return Whether the script was successfully executed or not.
     */
    public static boolean executeScript(String script)
    {
        try {
            logger.info("Executing script: {}", script);
            
            Process process =
                    new ProcessBuilder("bash", "-c", script)
                            .redirectErrorStream(true)
                            .start();
            
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                logger.debug(line);
            }
            
            return 0 == process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing script: {}", script);
            logger.error(stackTrace(e));
            return false;
        }
    }
    
    /**
     * Creates a stacktrace entry for the log.
     *
     * @param e The Exception to create a stacktrace for.
     * @return The stacktrace entry.
     */
    public static String stackTrace(Exception e)
    {
        StringBuilder stackTrace = new StringBuilder();
        stackTrace.append("Stacktrace:").append(System.lineSeparator());
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        stackTrace.append(sw);
        stackTrace.deleteCharAt(stackTrace.length() - 1);
        
        return stackTrace.toString();
    }
    
}
