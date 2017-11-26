/*
 * File:    Media.java
 * Package: client.pojo
 * Author:  Zachary Gill
 */

package client.pojo;

import java.io.File;

public class Media
{
    private int mediaId = 0;
    private String title = "";
    private String type = "";
    private int producerId = 0;
    private String description = "";
    private String genre = "";
    private String actors = "";
    private File image = null;
    private String showtimes = "";
    private String rating = "";
    private int year = 0;
    
    
    public Media()
    {
    }
    
    @Override
    public String toString()
    {
        return "mediaId: " + getMediaId() + ", title: " + getTitle() + ", type: " + getType() + ", producerId: " + getProducerId() + ", description: " + getDescription() + ", genre: " + getGenre() + ", actors: " + getActors() + ", showtimes: " + getShowtimes() + ", rating: " + getRating() + ", year: " + getYear();
    }
    
    
    public int getMediaId()
    {
        return mediaId;
    }
    
    public void setMediaId(int mediaId)
    {
        this.mediaId = mediaId;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public int getProducerId()
    {
        return producerId;
    }
    
    public void setProducerId(int producerId)
    {
        this.producerId = producerId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getGenre()
    {
        return genre;
    }
    
    public void setGenre(String genre)
    {
        this.genre = genre;
    }
    
    public String getActors()
    {
        return actors;
    }
    
    public void setActors(String actors)
    {
        this.actors = actors;
    }
    
    public File getImage()
    {
        return image;
    }
    
    public void setImage(File image)
    {
        this.image = image;
    }
    
    public String getShowtimes()
    {
        return showtimes;
    }
    
    public void setShowtimes(String showtimes)
    {
        this.showtimes = showtimes;
    }
    
    public String getRating()
    {
        return rating;
    }
    
    public void setRating(String rating)
    {
        this.rating = rating;
    }
    
    public int getYear()
    {
        return year;
    }
    
    public void setYear(int year)
    {
        this.year = year;
    }
}
