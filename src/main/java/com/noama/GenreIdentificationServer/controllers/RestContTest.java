package com.noama.GenreIdentificationServer.controllers;


import com.noama.GenreIdentificationServer.model.Genre;
import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.model.User;
import com.noama.GenreIdentificationServer.services.MongoService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  RestContTest.
 *  By noamabutbul | 29/01/2023 21:32
 */

@RestController
public class RestContTest
{
    private MongoService mongoService;

    public RestContTest(MongoService mongoService)
    {
        this.mongoService = mongoService;
    }

    
    @GetMapping("test")
    public String test()
    {
        System.out.println("Test");
        return "Hello from test";
    }
    
    @GetMapping("allSongs")
    public List<Song> allSongs()
    {
        System.out.println("list = " + mongoService.getAllSongs());
        return mongoService.getAllSongs();
    }
    
    
    @GetMapping("addSong")
    public String addSong(String name, int duration, Genre genre)
    {
        System.out.println("addSong()");
        
        System.out.println("name Parameter = " + name);
        System.out.println("duration Parameter = " + duration);
        
        if (mongoService.addSong(new Song(name, genre)))
            return "Succses";
        else
            return "Song not added";
    }
    
    
    @GetMapping("getSong")
    public Song getSong(String name)
    {
        System.out.println("getSong()");
        
        System.out.println("name Parameter = " + name);
        
        Song song = mongoService.getSong(name);
        
        System.out.println("SONG ===========" + song);
        
        if (song == null)
        {
            System.out.println("--------------getSong = NULL");
            return null;
        }

        System.out.println("song = " + song);
        return song;
    }

    @GetMapping("deleteSong")
    public String deleteSong(String name)
    {
        System.out.println("deleteSong()");
        
        System.out.println("name Parameter = " + name);
        
        if (mongoService.deleteSong(name))
            return "Succses";
        
        return "Song not found";
    }
    
    @GetMapping("updateSong")
    public String updateSong(String name, int duration, Genre genre)
    {
        System.out.println("name Parameter = " + name);
        System.out.println("duration Parameter = " + duration);

        mongoService.updateSong(name, genre);
        return  "Succses";
    }
    
    
    @GetMapping("addUser")
    public String addUser(String username, String password)
    {
        System.out.println("addUser()");
        
        System.out.println("namusernamee Parameter = " + username);
        System.out.println("password Parameter = " + password);
        
        mongoService.addUser(new User(username, password, User.Type.NORMAL_USER, new ArrayList<Song>()));
        return "Succses";
    }
    
    @GetMapping("checkGet")
    public String checkGet(String username)
    {
        System.out.println("user  = " +  mongoService.getUser(username));
        return "Succses";
    }
    
    
    @GetMapping("addSongToNoamUser")
    public String addSongToNoamUser()
    {
//        ArrayList<Song> songs = new ArrayList<>();
//        songs.add(new Song("Let It Be", Genre.ROCK));
//        songs.add(new Song("Canon In D", Genre.CLASSICAL));
//        
        
        mongoService.addSongToHistoryUser("Noam", new Song("Let It Be", Genre.ROCK));
        mongoService.addSongToHistoryUser("Noam", new Song("Canon In D", Genre.CLASSICAL));


        
        //mongoService.updateUser("Noam", "1234", User.Type.MANGER_USER, songs);
        return "Succses";
    }
    
}
