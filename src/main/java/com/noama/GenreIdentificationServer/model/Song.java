package com.noama.GenreIdentificationServer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *  Song. מחלקה המגדירה שיר
 *  By noamabutbul | 25/01/2023 11:24
 */
@Document(collection = "songs")
public class Song
{
    @Id
    private String name; // שם השיר
    private Genre genre; // ז׳אנר השיר

    public Song(String name, Genre genre)
    {
        this.name = name;
        this.genre = genre;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Genre getGenre()
    {
        return genre;
    }

    public void setGenre(Genre genre)
    {
        this.genre = genre;
    }

    @Override
    public String toString()
    {
        return "Song{" + "name=" + name + ", genre=" + genre + '}';
    }
}
