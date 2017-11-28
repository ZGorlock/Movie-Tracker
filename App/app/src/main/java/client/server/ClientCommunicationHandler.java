/*
 * File:    ClientCommunicationHandler.java
 * Package: client.server
 * Author:  Zachary Gill
 */

package client.server;

import android.util.Log;

import client.utility.CryptoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handled encrypted communication with clients.
 */
public final class ClientCommunicationHandler
{
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ClientCommunicationHandler.class);
    
    
    //Enums
    
    /**
     * An enumeration of key indices.
     */
    public enum KeyIndex
    {
        AES_KEY,
        RSA_SERVER_PUBLIC_KEY,
        DSA_SERVER_PUBLIC_KEY,
    }
    
    
    //Static Fields
    
    /**
     * A map of communication channel ids to their associated AES secret key, server RSA public key, and server DSA public key.
     */
    private final static Map<Long, List<Key>> communicationKeys = new HashMap<>();
    
    
    //Functions
    
    /**
     * Opens a communication channel with the server.
     *
     * @param id           The id of the communication channel.
     * @param aesKey       The AES secret key of the communication channel.
     * @param rsaPublicKey The RSA public key of the server.
     * @param dsaPublicKey The DSA public key of the server.
     * @return Whether the communication channel was successfully opened or not.
     */
    public static boolean openCommunicationChannel(long id, SecretKey aesKey, PublicKey rsaPublicKey, PublicKey dsaPublicKey)
    {
        if ((aesKey == null) || (rsaPublicKey == null) || (dsaPublicKey == null)) {
            return false;
        }
        
        if (communicationKeys.containsKey(id)) {
            return false;
        }
        
        List<Key> keys = new ArrayList<>();
        keys.add(aesKey);
        keys.add(rsaPublicKey);
        keys.add(dsaPublicKey);
        
        communicationKeys.put(id, keys);
        return true;
    }
    
    /**
     * Returns the AES key of an open communication channel encrypted with the RSA public key of the server.
     *
     * @param id The id of the communication channel.
     * @return The AES key of the communication channel encrypted with the RSA public key of the server.
     */
    public static String getCommunicationChannelKey(long id)
    {
        if (!communicationKeys.containsKey(id)) {
            return "";
        }
        SecretKey aesKey = (SecretKey) communicationKeys.get(id).get(KeyIndex.AES_KEY.ordinal());
        PublicKey rsaServerPublicKey = (PublicKey) communicationKeys.get(id).get(KeyIndex.RSA_SERVER_PUBLIC_KEY.ordinal());
        if ((aesKey == null) || (rsaServerPublicKey == null)) {
            return "";
        }
        
        String aesKeyStore = CryptoUtility.storeAESSecret(aesKey);
        return SecurityHandler.encryptMessage(aesKeyStore, rsaServerPublicKey);
    }
    
    /**
     * Encrypts a message for a communication channel.
     *
     * @param id      The id of the communication channel.
     * @param message The message to encrypt.
     * @return The message encrypted with the AES key of the communication channel.
     */
    public static String encryptCommunication(long id, String message)
    {
//        SecretKey aesKey = (SecretKey) communicationKeys.get(id).get(KeyIndex.AES_KEY.ordinal());
//        if (aesKey == null) {
//
//            return "";
//        }

        return CryptoUtility.encryptAES(message, null);
    }
    
    /**
     * Decrypts a message for a communication channel.
     *
     * @param id      The id of the communication channel.
     * @param message The message to decrypt.
     * @return The message decrypted with the AES key of the communication channel.
     */
    public static String decryptCommunication(long id, String message)
    {
//        SecretKey aesKey = (SecretKey) communicationKeys.get(id).get(KeyIndex.AES_KEY.ordinal());
//        if (aesKey == null) {
//            return "";
//        }
        
        return CryptoUtility.decryptAES(message, null);
    }
    
    /**
     * Signs a message for a communication channel.
     *
     * @param id      The id of the communication channel.
     * @param message The message to sign.
     * @return The signature of the message.
     */
    public static String signCommunication(long id, String message)
    {
        return SecurityHandler.signMessage(message);
    }
    
    /**
     * Verifies a signature of a message for a communication channel.
     *
     * @param id        The id of the communication channel.
     * @param message   The message to verify the signature of.
     * @param signature The signature to verify.
     * @return Whether the signature was verified or not.
     */
    public static boolean verifyCommunication(long id, String message, String signature)
    {
        PublicKey dsaServerPublicKey = (PublicKey) communicationKeys.get(id).get(KeyIndex.DSA_SERVER_PUBLIC_KEY.ordinal());
        return (dsaServerPublicKey != null) && SecurityHandler.verifyMessage(message, signature, dsaServerPublicKey);
    }
    
    /**
     * Closes a communication channel.
     *
     * @param id The id of the communication channel.
     * @return Whether the communication channel was closed or not.
     */
    public static boolean closeCommunicationChannel(long id)
    {
        if (communicationKeys.containsKey(id)) {
            communicationKeys.remove(id);
            return true;
        }
        return false;
    }
    
}