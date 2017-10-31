/*
 * File:    Test.java
 * Package: client
 * Author:  Zachary Gill
 */

package client;


import client.pojo.User;
import client.server.ServerHandler;

public class Test
{
    
    public static void main(String[] args)
    {
        ServerHandler.setupServerHandler();
    
    
    
        String testUser = "tmp" + ((int)(Math.random() * 10000) + 1);
        String testProducer = "producer" + ((int)(Math.random() * 10000) + 1);
        String testPass = "password";
    
    
    
        //register the producer on the server
        boolean registered = ServerHandler.registerUser(testUser, testPass, "email@email.com", "Mr", "Producer", true);
        if (registered) {
            System.out.println("Producer: " + testProducer + " registered with id: " + ServerHandler.userId);
        } else {
            System.out.println("Producer: " + testProducer + " could not be registered");
            return;
        }
    
    
        //essentially logging in, making sure the credentials are good and returning a User object
        User user = ServerHandler.validateUser(testUser, testPass);
        if (user == null) {
            return;
        }
        System.out.println("User data: " + user.toString());
    
    
        //authorize user, get an auth token allowing them to make changes to the database, the auth token is required for some endpoints
        String token = ServerHandler.authorizeUser(testUser, testPass);
        System.out.println("User authorized with auth token: " + ServerHandler.authToken);
        
        
        
        
        
        //TODO addMedia
        //TODO editMedia
        //TODO retrieveMedia
        //TODO deleteMedia
        
        
        
        
        
        
        
        //register the user on the server
        registered = ServerHandler.registerUser(testUser, testPass, "email@email.com", "Andersmith", "Sanderstein", false);
        if (registered) {
            System.out.println("User: " + testUser + " registered with id: " + ServerHandler.userId);
        } else {
            System.out.println("User: " + testUser + " could not be registered");
            return;
        }
        
        
        //essentially logging in, making sure the credentials are good and returning a User object
        user = ServerHandler.validateUser(testUser, testPass);
        if (user == null) {
            return;
        }
        System.out.println("User data: " + user.toString());
        
        
        //authorize user, get an auth token allowing them to make changes to the database, the auth toke n is required for some endpoints
        token = ServerHandler.authorizeUser(testUser, testPass);
        System.out.println("User authorized with auth token: " + ServerHandler.authToken);
        
        
        
        //TODO addSubscription
        //TODO getSubscriptions
        //  get subscriptions for a user
        //  returns list of Subscription classes
        //TODO removeSubscription
        //TODO queryMedia
        //  queries the media
        //TODO retrieveMedia
        //  Returns the data about a media
        //TODO lookupProducer
    
    
        
        ServerHandler.shutdownServerHandler();
    }
    
}
