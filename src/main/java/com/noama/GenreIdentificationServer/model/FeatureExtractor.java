package com.noama.GenreIdentificationServer.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *  FeatureExtractor. מחלקה לחילוץ פיצ׳רים לביצוע החיזוי
 *  By noamabutbul | 10/05/2023 20:17
 */

public abstract class FeatureExtractor
{
    private static final String PATH_PY = "src/main/java/com/noama/GenreIdentificationServer/model/extractFeatureScript.py"; // נתיב קובץ הסקריפט
    private static final String PYTHON_INTERPRETER_PATH = "/Users/noamabutbul/opt/anaconda3/bin/python"; // נתיב הפייתון במחשב זה
    private static final String FEATURES_OUTPUT_FILE_PATH = "src/main/java/com/noama/GenreIdentificationServer/Data/outputFromPython.txt";

    /**
     * פעולת חילוץ הפיצ׳רים
     * מפעילה סקריפט בפייתון שכותב לתוך קובץ את הפיצ׳רים
     * @param audioStream סטרים קובץ השיר לחילוץ הפיצרים
     * @return מערך הפיצ׳רים המחולצים מהשיר
     * 
     * יעילות הפעולה
     * O(N^2)
     * N: מסמן את גודל קובץ השיר
     * 
     * הספרייה שעושה את החילוץ משתמשת באלגורתמים
     * ואנו מניחים שבמקרה הגרוע היא לא עוברת את היעילות הזו
     */
    public synchronized static double[] extractFeatures(FileInputStream audioStream) 
    {
        try 
        {
            // Create temporary input and output files
            File inputFile = File.createTempFile("input", ".bin");        
            File outputFile = new File(FEATURES_OUTPUT_FILE_PATH);

            FileOutputStream inputFileStream = new FileOutputStream(inputFile);
            // Write audio data to the input file
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer)) != -1) 
            {
                inputFileStream.write(buffer, 0, bytesRead);
            }

            // Close the input file stream
            inputFileStream.close();

            // Build the command
            ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_INTERPRETER_PATH, PATH_PY, inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

            // Run the command
            Process process = processBuilder.start();

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Python script execution completed with exit code: " + exitCode);

            // Read the output file
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile)));
            String line;
            while ((line = reader.readLine()) != null) 
            {
                System.out.println(line);
            }
            
            // Delete the temporary file
            inputFile.delete();
            
            return readOutputFile();
        } 
        catch (Exception ex) 
        {
            System.out.println("Error during feature extraction " + ex);
        }     
        
        
        return null;
    }
 
    
    /**
     * פעולת קריאת הפיצ׳רים מתוך קובץ
     * הסקרפיט המופעל לחילוץ הפיצ׳רים כותב אותם
     * לתוך קובץ
     * הפעולה קוראת את הקובץ ומחזירה מערך של נתוני הפיצ׳רים
     * @return מערך של הפיצ׳רים שחולצו
     * 
     * יעילות הפעולה
     * O(N)
     * N: מסמן את גודל הקובץ
     */
    public static double[] readOutputFile() 
    {
        try
        {
            List<Double> resultList = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(FEATURES_OUTPUT_FILE_PATH))) 
            {
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    double value = Double.parseDouble(line);
                    resultList.add(value);
                }
            }

            // Convert the list to an array
            double[] resultArray = new double[resultList.size()];
            for (int i = 0; i < resultList.size(); i++) {
                resultArray[i] = resultList.get(i);
            }

            printArray(resultArray, "Result:");

            return resultArray;
        } 
        catch (Exception e)
        {
            System.out.println("Error during read the output file");
        }
        
        return null;
    }

    
    public static void printArray(double[] arr, String title) 
    {
        System.out.println(title);
        System.out.println("------------------------------");
        for (int i = 0; i < arr.length; i++) 
        {
            System.out.print(arr[i] + ", ");
        }
        System.out.println();
    }
    
}
