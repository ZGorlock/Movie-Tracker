/*
 * File:    Test.java
 * Package: client
 * Author:  Zachary Gill
 */

package client;


import client.pojo.Media;
import client.pojo.User;
import client.server.ServerHandler;
import server.Server;

import java.io.File;
import java.util.List;

public class Test
{
    
    public static void main(String[] args)
    {
        ServerHandler.setupServerHandler();
    
    
        
        
        String testUser = "tmp" + ((int)(Math.random() * 10000) + 1);
        String testProducer = "producer" + ((int)(Math.random() * 10000) + 1);
        String testPass = "password";
    
    
    
        //register the producer on the server
        
        //usernames, first names, and lastnames <= 32 character
        //emails <= 64 characters
        
        boolean registered = ServerHandler.registerUser(testProducer, testPass, "email@email.com", "Mr", "Producer", true);
        if (registered) {
            System.out.println("Producer: " + testProducer + " registered with id: " + ServerHandler.userId);
        } else {
            System.out.println("Producer: " + testProducer + " could not be registered");
            return;
        }
    
    
        //essentially logging in, making sure the credentials are good and returning a User object
        
        User user = ServerHandler.validateUser(testProducer, testPass);
        if (user == null) {
            return;
        }
        System.out.println("Producer data: " + user.toString());
    
    
        //authorize producer, get an auth token allowing them to make changes to the database, the auth token is required for some endpoints
        
        String token = ServerHandler.authorizeUser(testProducer, testPass);
        System.out.println("Producer authorized with auth token: " + token);
        
        
        
        
        //add media
        
        //title, type, and genre <= 64 characters
        //rating <= 16 characters
        //actors, showtimes <= 1024 characters
        //description <= 2048 characters
        
        Media media1 = new Media();
        media1.setTitle("The Show");
        media1.setType("Show"); //you can make the types whatever you want as long as they are consistent
        //media1.setProducerId(); no need to set this, the server takes care of it
        media1.setDescription("This is the TV Show.");
        media1.setGenre("Drama");
        media1.setActors("Lionard Deprapio; Jecca Simons; Kristofar Wakin;"); //you can deliminate these entries any way you would like, just handle it in the client application. It will be stored and retrieved from the database exactly as you save it.
        media1.setImage(new File("resources/unnamed.png"));
        media1.setShowtimes("Nov 17, 2017 11:00 PM; Nov 19, 217 08:00 PM;"); //same with the format and delimination of these
        media1.setRating("R");
        media1.setYear(2017);

        ServerHandler.addMedia(media1);

        media1.setTitle("The other Show");
        media1.setDescription("This is the other TV Show.");
        media1.setShowtimes("Dec 1, 2017 01:00 PM;");

        ServerHandler.addMedia(media1);

        media1.setTitle("The Movie");
        media1.setType("Movie");
        media1.setDescription("This is the movie.");
        media1.setShowtimes("Dec 7, 2017 01:00 PM;");

        ServerHandler.addMedia(media1);

        media1.setTitle("The other Movie");
        media1.setDescription("This is the other Movie.");
        media1.setShowtimes("Dec 3, 2017 01:00 PM;");

        ServerHandler.addMedia(media1);
        
        
        //query the current media by this producer
        
        Media queryMedia = new Media();
        queryMedia.setProducerId(ServerHandler.userId); //anything set in this query Media will be used as a parameter in the search. producerId and year require exact matches, everything else will perform a "string contains" operation. Any parameter with a ';' will be discarded as safety against sql injection. This means you cannot search multiple actors or showtimes at once.
        List<Integer> currentMedia = ServerHandler.queryMedia(queryMedia);
        for (int i : currentMedia) {
            System.out.println("Query returned Media: " + i);
        }
        
        
        //retrieve a media
        
        Media retrievedMedia = ServerHandler.retrieveMedia(currentMedia.get(0)); //retrieve the first media in the list, this will return a usable Media entity. This will also download the media's image to the images/ folder, but only the first time. You can get the image from the image field in the entity.
        System.out.println(retrievedMedia.toString());
        
        
        //edit media
        
        retrievedMedia.setTitle(retrievedMedia.getTitle() + " edited");
        ServerHandler.editMedia(retrievedMedia.getMediaId(), retrievedMedia);
    
        
        //retrieve the edited media
    
        Media newRetrievedMedia = ServerHandler.retrieveMedia(currentMedia.get(0)); //retrieve the media again
        System.out.println(newRetrievedMedia.toString());
        
        
        //delete media
        
        ServerHandler.deleteMedia(newRetrievedMedia.getMediaId());
    
    
        //query the current media by this producer after deleting one
    
        queryMedia.setProducerId(ServerHandler.userId);
        currentMedia = ServerHandler.queryMedia(queryMedia);
        for (int i : currentMedia) {
            System.out.println("Query returned Media: " + i);
        }
        
        
        
        
        //register a user on the server
        registered = ServerHandler.registerUser(testUser, testPass, "email@email.com", "Sandersmith", "Anderstein", false);
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
    
    
        //authorize user, get an auth token allowing them to add, remove, and get their subscriptions, the auth token is required for some endpoints
    
        token = ServerHandler.authorizeUser(testUser, testPass);
        System.out.println("User authorized with auth token: " + token);
        
        
        
        
        //add subscriptions
    
        ServerHandler.addSubscription(currentMedia.get(0));
        ServerHandler.addSubscription(currentMedia.get(1));
        
        
        //get User's subscriptions
        
        List<Integer> subscriptions = ServerHandler.getSubscriptions();
        for (int i : subscriptions) {
            System.out.println("Subscribed to Media: " + i);
        }
        
        
        //remove subscription
    
        ServerHandler.removeSubscription(currentMedia.get(0)); //in the event that a Media is deleted while a User is subscribed to it, during the process of deleting the Media the server will also remove any subscriptions for that Media for all Users
    
    
        //get User's subscriptions after removing one
    
        subscriptions = ServerHandler.getSubscriptions();
        for (int i : subscriptions) {
            System.out.println("Subscribed to Media: " + i);
        }
    
    
        
        
        ServerHandler.shutdownServerHandler();
    }
    
}
