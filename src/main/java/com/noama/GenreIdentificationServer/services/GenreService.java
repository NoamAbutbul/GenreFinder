package com.noama.GenreIdentificationServer.services;

import com.noama.GenreIdentificationServer.model.Genre;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *  GenreService. מחלקה המספקת שירות לז׳אנרים
 *  By noamabutbul | 12/02/2023 22:15
 */

@Service
public abstract class GenreService
{
    public static final String ROCK_STRING = "Rock"; // מחרוזת לרוק
    public static final String POP_STRING = "Pop"; // מחרוזת לפופ
    public static final String CLASSICAL_STRING = "Classical"; // מחרוזת לקלאסית
    public static final String DISCO_STRING = "Disco"; // מחרוזת לדיסקו

    
    /**
     * פעולה המחזירה את רשימת כל הז׳אנרים
     * @return רשימת כל הז׳אנרים
     */
    public static ArrayList<Genre> allGenres()
    {
       ArrayList<Genre> genres = new ArrayList<>();
       genres.add(Genre.POP);
       genres.add(Genre.ROCK);
       genres.add(Genre.DISCO);
       genres.add(Genre.CLASSICAL);

       return genres;
    }
    
    /**
     * פעולה המקבלת את הז׳אנר ומחזירה מחרוזת המתאימה לו
     * @param genre ז׳אנר
     * @return מחרוזת של אותו הז׳אנר
     */
    public static String convertGenreToString(Genre genre)
    {
        switch (genre)
        {
            case ROCK ->
            {
                return ROCK_STRING;
            }
            case POP ->
            {
                return POP_STRING;
            }
            case CLASSICAL ->
            {
                return CLASSICAL_STRING;
            }
            case DISCO ->
            {
                return DISCO_STRING;
            }
            default ->
            {
            }
        }
        return null;
    }
    
    /**
     * פעולה המקבלת מחרוזת המתארת את הז׳אנר ומחזירה את הז׳אנר המתאים
     * @param strGenre מחרוזת הז׳אנר
     * @return הז׳אנר המתאים למחרוזת
     */
    private static Genre convertStringToGenre(String strGenre)
    {
        switch (strGenre)
        {
            case ROCK_STRING ->
            {
                return Genre.ROCK;
            }
            case POP_STRING ->
            {
                return Genre.POP;
            }
            case CLASSICAL_STRING ->
            {
                return Genre.CLASSICAL;
            }
            case DISCO_STRING ->
            {
                return Genre.DISCO;
            }
            default ->
            {
            }
        }
        return null;
    }
    
}
