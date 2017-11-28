/*
 * File:    CommunicationHandler.java
 * Package: communication
 * Author:  Zachary Gill
 */

package communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.CryptoUtility;

import javax.crypto.SecretKey;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handled encrypted communication with the server.
 */
public final class CommunicationHandler
{
    //Logger
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CommunicationHandler.class);


    //Enums
    
    /**
     * An enumeration of key indices.
     */
    public enum KeyIndex
    {
        AES_KEY,
        RSA_CLIENT_PUBLIC_KEY,
        DSA_CLIENT_PUBLIC_KEY,
        RSA_SERVER_PUBLIC_KEY,
        RSA_SERVER_PRIVATE_KEY,
        DSA_SERVER_PUBLIC_KEY,
        DSA_SERVER_PRIVATE_KEY
    }
    
    
    //Static Fields
    
    /**
     * A map of communication channel ids to their associated AES secret key, client RSA public key, and client DSA public key.
     */
//    private final static Map<Long, List<Key>> communicationKeys = new HashMap<>();
    
    
    //Functions
    
    /**
     * Opens a communication channel for a client.
     *
     * @param rsaPublicKey  The RSA public key of the client.
     * @param dsaPublicKey  The DSA public key of the client.
     * @param rsaServerKeys The RSA key pair of the server.
     * @param dsaServerKeys The DSA key pair of the server.
     * @return The id of the communication channel that was opened.
     */
    public static long openCommunicationChannel(PublicKey rsaPublicKey, PublicKey dsaPublicKey, KeyPair rsaServerKeys, KeyPair dsaServerKeys)
    {
        return -1;
        
//        if ((rsaPublicKey == null) || (dsaPublicKey == null) || (rsaServerKeys == null) || (dsaServerKeys == null)) {
//            return -1;
//        }
//
//        long id = generateCommunicationId();
//        SecretKey aesKey = CryptoUtility.generateAESKey();
//
//        List<Key> keys = new ArrayList<>();
//        keys.add(aesKey);
//        keys.add(rsaPublicKey);
//        keys.add(dsaPublicKey);
//        keys.add(rsaServerKeys.getPublic());
//        keys.add(rsaServerKeys.getPrivate());
//        keys.add(dsaServerKeys.getPublic());
//        keys.add(dsaServerKeys.getPrivate());
//
//        communicationKeys.put(id, keys);
//        return id;
    }
    
    /**
     * Generates a new id for the communication key map.
     *
     * @return The newly generate id.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    private static long generateCommunicationId()
    {
        return -1;
        
//        long id;
//        while (communicationKeys.containsKey((id = (long) ((new SecureRandom().nextDouble() * Long.MAX_VALUE) + 1)))) {
//        }
//        return id;
    }
    
    /**
     * Determines if a communication channel is open with the specified id.
     *
     * @param id The id of the communication channel.
     * @return Whether a communication channel with the specified id is open or not.
     */
    public static boolean hasCommunicationId(long id)
    {
        return true;
//        return communicationKeys.containsKey(id);
    }
    
    /**
     * Returns the AES key of an open communication channel encrypted with the RSA public key of the client.
     *
     * @param id The id of the communication channel.
     * @return A list containing the AES key of the communication channel encrypted with the RSA public key of the client, the RSA public key of the server, and the DSA public key of the server.
     */
    public static List<String> getCommunicationChannelKeys(long id)
    {
        return new ArrayList<String>(4);
        
//        if (!communicationKeys.containsKey(id)) {
//            return null;
//        }
//
//        SecretKey aesKey = (SecretKey) communicationKeys.get(id).get(KeyIndex.AES_KEY.ordinal());
//        PublicKey rsaKey = (PublicKey) communicationKeys.get(id).get(KeyIndex.RSA_CLIENT_PUBLIC_KEY.ordinal());
//        PublicKey rsaServerKey = (PublicKey) communicationKeys.get(id).get(KeyIndex.RSA_SERVER_PUBLIC_KEY.ordinal());
//        PublicKey dsaServerKey = (PublicKey) communicationKeys.get(id).get(KeyIndex.DSA_SERVER_PUBLIC_KEY.ordinal());
//        if ((aesKey == null) || (rsaKey == null) || (rsaServerKey == null) || (dsaServerKey == null)) {
//            return null;
//        }
//
//        List<String> keyStores = new ArrayList<>();
//        keyStores.add(CryptoUtility.encryptRSA(CryptoUtility.storeAESSecret(aesKey), rsaKey));
//        keyStores.add(CryptoUtility.storeRSAPublicKey(rsaServerKey));
//        keyStores.add(CryptoUtility.storeDSAPublicKey(dsaServerKey));
//
//        return keyStores;
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
//        PrivateKey dsaServerPrivateKey = (PrivateKey) communicationKeys.get(id).get(KeyIndex.DSA_SERVER_PRIVATE_KEY.ordinal());
//        if (dsaServerPrivateKey == null) {
//            return "";
//        }
        
        return CryptoUtility.signDSA(message, null);
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
        return true;
        
//        PublicKey dsaClientPublicKey = (PublicKey) communicationKeys.get(id).get(KeyIndex.DSA_CLIENT_PUBLIC_KEY.ordinal());
//        return (dsaClientPublicKey != null) && CryptoUtility.verifyDSA(message, signature, dsaClientPublicKey);
    }
    
    /**
     * Closes a communication channel.
     *
     * @param id     The id of the communication channel.
     * @param aesKey The AES key associated with the communication channel.
     * @return Whether the communication channel was closed or not.
     */
    public static boolean closeCommunicationChannel(long id, SecretKey aesKey)
    {
        return true;
        
//        if (communicationKeys.containsKey(id) && CryptoUtility.storeAESSecret((SecretKey) communicationKeys.get(id).get(KeyIndex.AES_KEY.ordinal())).equals(CryptoUtility.storeAESSecret(aesKey))) {
//            communicationKeys.remove(id);
//            return true;
//        }
//        return false;
    }
    
}
