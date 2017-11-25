/*
 * File:    SecurityHandler.java
 * Package: client.server
 * Author:  Zachary Gill
 */

package client.server;

import java.security.KeyPair;
import java.security.PublicKey;

import client.utility.CryptoUtility;

public class SecurityHandler
{
    
    private static String password;
    
    /**
     * The DSA keys of the server.
     */
    private static KeyPair dsaKeys = null;
    
    /**
     * The RSA keys of the server.
     */
    private static KeyPair rsaKeys = null;
    
    public static void setPassword(String passwordA)
    {
        password = passwordA;
    }
    
    public static void setDsaKeys(KeyPair keyPair)
    {
        dsaKeys = keyPair;
    }
    
    public static void setRsaKeys(KeyPair keyPair)
    {
        rsaKeys = keyPair;
    }
    
    public static String getPassword()
    {
        return password;
    }
    
    
    
    
    
    /**
     * Encrypts a message using the an RSA public key.
     *
     * @param message   The message to encrypt.
     * @param publicKey The public key to encrypt the message with.
     * @return The encrypted message.
     */
    public static String encryptMessage(String message, PublicKey publicKey)
    {
        return CryptoUtility.encryptRSA(message, publicKey);
    }
    
    /**
     * Encrypts a message using a password and salt.
     *
     * @param message The message to encrypt.
     * @param salt    The salt to encrypt the message with.
     * @return The encrypted message.
     */
    public static String encryptMessageWithPassword(String message, String salt)
    {
        return CryptoUtility.encryptWithPassword(message, password + salt);
    }
    
    /**
     * Decrypts a message using the system RSA private key.
     *
     * @param message The message to decrypt.
     * @return The decrypted message.
     */
    public static String decryptMessage(String message)
    {
        return CryptoUtility.decryptRSA(message, rsaKeys.getPrivate());
    }
    
    /**
     * Decrypts a message using a password and salt.
     *
     * @param message The message to decrypt.
     * @param salt    The salt to decrypt the message with.
     * @return The decrypted message.
     */
    public static String decryptMessageWithPassword(String message, String salt)
    {
        return CryptoUtility.decryptWithPassword(message, password + salt);
    }
    
    /**
     * Signs a message using the system DSA private key.
     *
     * @param message The message to sign.
     * @return The generated signature for the message.
     */
    public static String signMessage(String message)
    {
        return CryptoUtility.signDSA(message, dsaKeys.getPrivate());
    }
    
    /**
     * Verifies a signature of a message with a DSA public key.
     *
     * @param message   The message that was signed.
     * @param signature The signature to verify.
     * @param publicKey The public key to verify the message with.
     * @return Whether the signature was verified or not.
     */
    public static boolean verifyMessage(String message, String signature, PublicKey publicKey)
    {
        return CryptoUtility.verifyDSA(message, signature, publicKey);
    }
}
