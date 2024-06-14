package com.noama.GenreIdentificationServer.model;

import static com.noama.GenreIdentificationServer.model.MusicGenreClassifier.NUM_FEATURES;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  CSVFilter. מחלקה המנהלת את קבצי המידע במערכת
 *  By noamabutbul | 1/05/2023 11:42
 */

public abstract class CSVManage
{
    public static final String CSV_FILE_READ = "src/main/java/com/noama/GenreIdentificationServer/Data/features_30_sec.csv"; // קובץ המידע המקורי
    public static final String CSV_FILE_OUTPUT_GENRES = "src/main/java/com/noama/GenreIdentificationServer/Data/output.csv"; // קובץ זמני
    public static final String CSV_FILE_OUTPUT_FEATURES = "src/main/java/com/noama/GenreIdentificationServer/Data/dataset1000.csv"; // קובץ המידע עם אלף דוגמאות
    
    public static final String CSV_DATA_OUTPUT = "src/main/java/com/noama/GenreIdentificationServer/Data/datasetOutput.csv"; // קובץ זמני
    
    
    public static final String TRAIN_FILE = "src/main/java/com/noama/GenreIdentificationServer/Data/traningset.csv"; // סט האימון הסופי
    public static final String TEST_FILE = "src/main/java/com/noama/GenreIdentificationServer/Data/testingset.csv"; // סט הבדיקה הסופי


    public static final String CSV_SPLIT_SIGN = ","; // סימן הפרדת הנתונים בקובץ
    public static final String[] LABELS = {"classical", "pop", "disco", "rock"}; // שמות הז׳אנרים מקובץ הנתונים
    
    
    /**
     * פונקצייה האחראית לניקוי המידע בקובץ המידע
     * מוחקת את הז׳נארים הלא רלוונטים
     * ומוחקת את הפיצ׳רים שלא נעשה בהם שימוש
     * 
     * יעילות הפעולה
     * O(N) 
     * N: מסמן את גודל קובץ המידע
     */
    public static void filter()
    {
        // filter the genres:
        
        String line = "";

        try ( BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_READ));  FileWriter fw = new FileWriter(CSV_FILE_OUTPUT_GENRES))
        {
            // iterate over rows and filter out unwanted labels
            while ((line = br.readLine()) != null)
            {
                String[] row = line.split(CSV_SPLIT_SIGN);
                String label = row[row.length - 1]; // הז׳נאר נמצא בעמודה האחרונה
                boolean isWantedLabel = false;
                for (String wantedLabel : LABELS)
                {
                    if (label.equals(wantedLabel))
                    {
                        isWantedLabel = true;
                        break;
                    }
                }
                if (isWantedLabel)
                {
                    fw.write(line + "\n");
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
        // filter the features:
        
        // Define which columns to keep
        final int[] COLUMNS_TO_KEEP = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 59};

        // Open input and output streams
        try ( BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_OUTPUT_GENRES));  BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE_OUTPUT_FEATURES)))
        {
            // clean the unnecessary headers
            bw.write("chroma_stft_mean,chroma_stft_var,rms_mean,rms_var,spectral_centroid_mean,spectral_centroid_var,spectral_bandwidth_mean,spectral_bandwidth_var,rolloff_mean,rolloff_var,zcr_mean,zcr_var,harmony_mean,harmony_var,perceptr_mean,perceptr_var,tempo, label");
            bw.newLine();

            // Process each line of the dataset
            line = "";
            while ((line = br.readLine()) != null)
            {
                // Split the line into columns
                String[] columns = line.split(CSV_SPLIT_SIGN);

                // Keep only the desired columns
                StringBuilder filteredLine = new StringBuilder();
                for (int colIndex : COLUMNS_TO_KEEP)
                {
                    filteredLine.append(columns[colIndex]).append(",");
                }
                filteredLine.setLength(filteredLine.length() - 1); // remove last comma

                // Write the filtered line to output file
                bw.write(filteredLine.toString());
                bw.newLine();
            }

            System.out.println("Filtered dataset saved to: " + CSV_FILE_OUTPUT_FEATURES);

        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
        // delete the unnecessary files
        File file = new File(CSV_FILE_OUTPUT_GENRES);
        file.delete();
        
    }
    
    /**
     *פונקציה הכותבת לתוך קובץ אצ ערכי 
     * המינימום והמקסימום של הפיצ׳רים
     * קובץ למינימום וקובץ למקסימום
     * 
     * יעילות הפעולה
     * O(N)
     * N: מסמן את גודל קובץ המידע
     */
    public static void writeMimMaxValueFeaturesIntoFile()
    {
        final int NUM_FEATURES = 17;
        double[] featureMins = new double[NUM_FEATURES];
        double[] featureMaxs = new double[NUM_FEATURES];
        final String minFile = "src/main/java/com/noama/GenreIdentificationServer/Data/minValuesForNormalize.txt";
        final String maxFile = "src/main/java/com/noama/GenreIdentificationServer/Data/maxValuesForNormalize.txt";
        
        
        BufferedReader br;
        BufferedWriter writerForMin;
        BufferedWriter writerForMax;


        String line = "";


        // init the array
        try 
        {
            br = new BufferedReader(new FileReader(TRAIN_FILE));
            
            // Read header line
            String header = br.readLine();

            // Initialize feature mins/maxs
            for (int i = 0; i < NUM_FEATURES; i++) 
            {
                featureMins[i] = Double.MAX_VALUE;
                featureMaxs[i] = Double.MIN_VALUE;
            }

            // Process each data row
            while ((line = br.readLine()) != null) 
            {
                String[] values = line.split(CSV_SPLIT_SIGN);

                // Update min/max values for each feature
                for (int i = 0; i < NUM_FEATURES; i++) 
                {
                    double value = Double.parseDouble(values[i]);
                    if (value < featureMins[i]) 
                    {
                        featureMins[i] = value;
                    }
                    if (value > featureMaxs[i]) 
                    {
                        featureMaxs[i] = value;
                    }
                }
            }
            
            System.out.println("MIN-------------------------");
            printArray(featureMins);
            System.out.println("MAX-------------------------");
            printArray(featureMaxs);


            br.close();

        } 
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        
        
        try 
        {
            writerForMin = new BufferedWriter(new FileWriter(minFile));
            writerForMax  = new BufferedWriter(new FileWriter(maxFile));

            
            for (double element : featureMins) 
            {
                writerForMin.write(String.valueOf(element));
                writerForMin.newLine();
            }
            
            for (double element : featureMaxs) 
            {
                writerForMax.write(String.valueOf(element));
                writerForMax.newLine();
            }
            
            writerForMin.flush();
            writerForMax.flush();
            
            System.out.println("The files written successfully");
            
        } catch (Exception e)
        {
            
        }
            
    }
    
    
    /**
     * פונקצייה לנרמול הנתונים בקובץ הנתונים
     * הפונקציה עוברת על כל שורה ומאתחלת שני מערכים
     * מערך מינימום לפיצ׳רים ומערך מקסימום
     * לאחר מכן עוברת שוב על הקובץ ומנרמלת כל פיצ׳ר בכל שורה
     * על ידי חישוב פשוט עם הערך המקסימלי והמינימלי שנוכל לקבל
     * בכל פיצ׳ר
     * 
     * יעילות הפעולה
     * O(N)
     * N: מסמן את גודל קובץ הנתונים
     */
    public static void normalizeData()
    {        
        double[] featureMins = new double[NUM_FEATURES];
        double[] featureMaxs = new double[NUM_FEATURES];
        
        
        BufferedReader br;
        BufferedWriter bw;
        StringBuilder sb;
        
        String line = "";
        
        try 
        {
            br = new BufferedReader(new FileReader(TRAIN_FILE));
            
            // Read header line
            br.readLine();

            // Initialize feature mins/maxs
            for (int i = 0; i < NUM_FEATURES; i++) 
            {
                featureMins[i] = Double.MAX_VALUE;
                featureMaxs[i] = Double.MIN_VALUE;
            }

            // Process each data row
            while ((line = br.readLine()) != null) 
            {
                String[] values = line.split(CSV_SPLIT_SIGN);

                // Update min/max values for each feature
                for (int i = 0; i < NUM_FEATURES; i++) 
                {
                    double value = Double.parseDouble(values[i]);
                    if (value < featureMins[i]) 
                    {
                        featureMins[i] = value;
                    }
                    if (value > featureMaxs[i]) 
                    {
                        featureMaxs[i] = value;
                    }
                }
            }
            
            System.out.println("MIN-------------------------");
            printArray(featureMins);
            System.out.println("MAX-------------------------");
            printArray(featureMaxs);


        br.close();
            
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        
               
        // Normalize data and write to output file
        try 
        {
            br = new BufferedReader(new FileReader(TRAIN_FILE));
            bw = new BufferedWriter(new FileWriter(CSV_DATA_OUTPUT));
            sb = new StringBuilder();
            
            // Write header line
            bw.write(br.readLine());
            bw.newLine();

            
            // Process each data row
            while ((line = br.readLine()) != null) 
            {
                String[] values = line.split(CSV_SPLIT_SIGN);

                // Normalize each feature value
                for (int i = 0; i < NUM_FEATURES; i++) 
                {
                    double value = Double.parseDouble(values[i]);
                    double scaledValue;
                    if (featureMins[i] == featureMaxs[i]) 
                    {
                        System.out.println("Skipping normalization for feature index: " + i);
                        scaledValue = value; // Keep the original value if min == max
                    } 
                    else 
                        scaledValue = (value - featureMins[i]) / (featureMaxs[i] - featureMins[i]);
                    
                    if (Double.isNaN(scaledValue)) 
                    {
                        System.out.println("NaN value encountered for feature index: " + i);
                    }
                    values[i] = String.format("%.6f", scaledValue); // Round to 6 decimal places
                }
                
                // Concatenate processed data row to output string
                sb.append(String.join(",", values));
                sb.append("\n");
                
            }
            
            bw.write(sb.toString());
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return;
        }
       
        
        // Delete original file
        File oldFile = new File(TRAIN_FILE);
        oldFile.delete();

        // Rename output file
        File newFile = new File(CSV_DATA_OUTPUT);
        newFile.renameTo(oldFile);
    }
     
     
    private static void printArray(double[] arr) 
    {
        for (int i = 0; i < arr.length; i++) 
        {
            String val = String.format("%.6f", arr[i]);
            System.out.println("Line " + (i+1) + ": " + val + " ");
        }
    }
    
   
    /**
     * פונקצייה המחלקת את קובץ המידע
     * לשני קבצים
     * אחד לאימון ואחד לבדיקה
     * 
     * יעילות הפעולה
     * O(N)
     * N: מסמן את גודל קובץ המידע
     */
    public static void splitData() 
    {
        final String INPUT_FILE = "src/main/java/com/noama/GenreIdentificationServer/Data/dataset1000.csv";
       
        final double SPLIT_RATIO = 0.7;
        
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));
            BufferedWriter trainWriter = new BufferedWriter(new FileWriter(TRAIN_FILE));
            BufferedWriter testWriter = new BufferedWriter(new FileWriter(TEST_FILE));

            String headerLine = reader.readLine(); // Read the header line

            List<String> lines = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            reader.close();

            // Shuffle the lines
            Collections.shuffle(lines);

            int totalLines = lines.size();
            int trainLines = (int) (totalLines * SPLIT_RATIO);
            int testLines = totalLines - trainLines;

            // Write the header line to both training and testing files
            trainWriter.write(headerLine);
            trainWriter.newLine();
            testWriter.write(headerLine);
            testWriter.newLine();

            // Write the lines to training and testing files
            for (int i = 0; i < totalLines; i++) 
            {
                line = lines.get(i);
                if (i < trainLines) 
                {
                    trainWriter.write(line);
                    trainWriter.newLine();
                } 
                else 
                {
                    testWriter.write(line);
                    testWriter.newLine();
                }
            }

            trainWriter.close();
            testWriter.close();
        }
        catch (Exception e)
        {
            System.out.println("Error during spilliting data: " + e);
        }
    }

}

