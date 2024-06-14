package com.noama.GenreIdentificationServer.model;

/**
 *  Prediction.  מחלקה המגדירה את החיזוי
 *  שומרת את הז׳אנר החזוי ואת מערך עם נתוני האחוזים לכל ז׳אנר
 *  By noamabutbul | 18/05/2023 13:25
 */

public class Prediction
{
    private Genre genre; // סוג הז׳אנר החזוי
    private double[] percentages; // מערך אחוזי הסיווג לכל הז׳אנרים

    
    public Prediction(Genre genre)
    {
        this.genre = genre;
        this.percentages = new double[MusicGenreClassifier.NUM_GENRES];
    }

    public Genre getGenre()
    {
        return genre;
    }

    public void setGenre(Genre genre)
    {
        this.genre = genre;
    }

    public double[] getPercentages()
    {
        return percentages;
    }

    public void setPercentages(double[] percentages)
    {
        this.percentages = percentages;
    }
    
    
    public void setPercentages(double percentages, int index)
    {
        this.percentages[index] = percentages;
    }
    
    
    @Override
    public String toString()
    {        
        String str = "Prediction:\n----------------\n";
        str += "Genre = " + genre + "\n";
        
        str += "Percentages:\n";
        
        for (int i = 0; i < MusicGenreClassifier.NUM_GENRES; i++)
        {
            str += Genre.values()[i] + ": " + percentages[i] + "\n";
        }
        
        return str;
    }
}
