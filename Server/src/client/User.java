/*
 * File:    User.java
 * Package: client
 * Author:  Zachary Gill
 */

package client;

public class User
{
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean producer;
    
    public User()
    {
    }
    
    @Override
    public String toString()
    {
        return "userId: " + userId + ", username: " + username + ", email: " + email + ", firstName: " + firstName + ", lastName: " + lastName + ", producer: " + producer;
    }
    
    
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    public boolean isProducer()
    {
        return producer;
    }
    
    public void setProducer(boolean producer)
    {
        this.producer = producer;
    }
}
