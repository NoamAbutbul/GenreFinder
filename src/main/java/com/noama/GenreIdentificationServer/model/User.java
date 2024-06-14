package com.noama.GenreIdentificationServer.model;

import com.noama.GenreIdentificationServer.services.UserTypeService;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *  User. מחלקה המגדירה משתמש
 *  By noamabutbul | 08/02/2023 02:54
 */

@Document(collection = "users")
public class User
{
    public enum Type // מגדיר את סוגי המשתמשים האפשריים במערכת
    {
        NORMAL_USER,
        MANGER_USER
    }
    
    @Id
    private String username; // שם המשתמש
    private String password; // סיסממא
    private Type type; // סוג המשתמש
    private List<Song> songs; // רשימת היסטוריית החיזויים של המשתמש
    

    public User(String username, String password, Type type, List<Song> songs)
    {
        this.username = username;
        this.password = password;
        this.type = type;
        this.songs = songs;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public String getTypeInString()
    {
        return UserTypeService.convertTypeToString(type);
    }
    
    public void setType(Type type)
    {
        this.type = type;
    }

    public List<Song> getSongs()
    {
        return songs;
    }
    
    public void setSongs(List<Song> songs)
    {
        this.songs = songs;
    }
    
    /**
     * פעולה המוסיפה שיר להיסטוריית החיזויים של המשתמש
     * @param song השיר להוספה
     */
    public void addSong(Song song)
    {
        songs.add(song);        
        System.out.println("The History Song: " + songs);
    }

    @Override
    public String toString()
    {
        return "User{" + "username=" + username + ", password=" + password + ", type=" + type + ", songs=" + songs + '}';
    }
}


