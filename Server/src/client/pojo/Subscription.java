/*
 * File:    Subscription.java
 * Package: client.pojo
 * Author:  Zachary Gill
 */

package client.pojo;

public class Subscription
{
    private int subscriptionId;
    private int userId;
    private int mediaId;
    
    
    public Subscription()
    {
    }
    
    @Override
    public String toString()
    {
        return "subscriptionId: " + getSubscriptionId() + ", userID: " + getUserId() + ", mediaId: " + getMediaId();
    }
    
    public int getSubscriptionId()
    {
        return subscriptionId;
    }
    
    public void setSubscriptionId(int subscriptionId)
    {
        this.subscriptionId = subscriptionId;
    }
    
    public int getUserId()
    {
        return userId;
    }
    
    public void setUserId(int userId)
    {
        this.userId = userId;
    }
    
    public int getMediaId()
    {
        return mediaId;
    }
    
    public void setMediaId(int mediaId)
    {
        this.mediaId = mediaId;
    }
}
