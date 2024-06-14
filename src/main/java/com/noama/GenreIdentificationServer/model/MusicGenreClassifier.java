package com.noama.GenreIdentificationServer.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.sgd.StochasticGradientDescent;
import org.encog.persist.EncogDirectoryPersistence;

/**
 *  MusicGenreClassifier. מחלקת המודל עם כל הפעולות הקשורות אליו
 *  By noamabutbul | 24/04/2023 16:42
 */

public class MusicGenreClassifier
{
    // Consts
    public static final int NUM_FEATURES = 17; // Number of features to use for input
    private static final int NUM_HIDDEN_NEURONS_FIRST = 256; // Number of hidden neurons for the network
    private static final int NUM_HIDDEN_NEURONS_SECOND = 128; // 220 110 60 25 f3
    private static final int NUM_HIDDEN_NEURONS_THIRD = 40; // 300 200 100 50 f4
    private static final int NUM_HIDDEN_NEURONS_FOURTH = 10; // 256 128 64 32 f5
    public static final int NUM_GENRES = 4; // Number of classes for output label
    private static final int NUM_EXAMPLES = 4000; // Number of examples in the dataset

    private static final Path MODEL_PATH = Paths.get("src/main/java/com/noama/GenreIdentificationServer/model/"); // Path to model package
    private static final Path DATA_PATH = Paths.get("src/main/java/com/noama/GenreIdentificationServer/Data/"); // Path to Data package
    
    private final String minFile = "src/main/java/com/noama/GenreIdentificationServer/Data/minValuesForNormalize.txt"; // The minimum features values for normalization
    private final String maxFile = "src/main/java/com/noama/GenreIdentificationServer/Data/maxValuesForNormalize.txt"; // The maximum features values for normalization

    private BasicNetwork network; // עצם המחזיק את רשת הנוירונים
    
    
    private MLDataSet trainingData; // עצם המחזיק את סט האימון
    private MLDataSet testingData; // עצם המחזיק את סט הבדיקה
    
    private double[] featureMins; // מערך ערכי המינימום של הפיצ׳רים
    private double [] featureMaxs; // מערך ערכי המקסימום של הפיצ׳רים
    
    
    /**
     * בנאי המאתחל את מערכי המינימום והמקסימום של הפיצ׳רים
     */
    public MusicGenreClassifier()
    {
        // init the array(s) for the normalize
        featureMins = new double[NUM_FEATURES];
        featureMaxs = new double[NUM_FEATURES];
        
        initFeaturesMaxMinToNormalize();
    }
    
    /**
     * פונקציה הקוראת מקובץ ערכי המינימום ומהקסימום של ערכי 
     * הפיצ׳רים על מנת לאתחל את מערכי המינימום והמקסימום במחלקה
     */
    private void initFeaturesMaxMinToNormalize() 
    {
        BufferedReader readerMin;
        BufferedReader readerMax;

        int index;
        String line = "";

        try 
        {
            readerMin = new BufferedReader(new FileReader(minFile));
            readerMax = new BufferedReader(new FileReader(maxFile));
            
            
            index = 0;
            while ((line = readerMin.readLine()) != null && index < featureMins.length) 
            {
                double value = Double.parseDouble(line);
                featureMins[index] = value;
                index++;
            }
            
            index = 0;
            while ((line = readerMax.readLine()) != null && index < featureMaxs.length) 
            {
                double value = Double.parseDouble(line);
                featureMaxs[index] = value;
                index++;
            }
            
            readerMin.close();
            readerMax.close();

            printArray(featureMins, "featureMins:");
            printArray(featureMaxs, "featureMaxs:");
        } 
        
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Cannot rade Min and Max values from the files");
        }
    }
    
    
    /**
     * פונקציה האחראית על טעינת המידע מקבצי אוסף הנתונים לתוך עצמי המחלקה
     */
    public void loadData()
    {
        // Load the training dataset
        trainingData = new BasicMLDataSet();
        loadDataset(trainingData, DATA_PATH.resolve("traningset.csv").toFile().getAbsolutePath());

        System.out.println("Training Data:");
        printDataSet(trainingData);

        // Load the testing dataset
        testingData = new BasicMLDataSet();
        loadDataset(testingData, DATA_PATH.resolve("testingset.csv").toFile().getAbsolutePath());
        
        System.out.println("Testing Data:");
        printDataSet(testingData);
        
        System.out.println("Data Loaded Succesfully");
        
    }
    
    /**
     * פונקציה המדפיסה את סט הנתונים
     * @param dataSet סט הנתונים
     */
    private void printDataSet(MLDataSet dataSet) 
    {
        for (MLDataPair pair : dataSet) 
        {
            System.out.println("Input: " + Arrays.toString(pair.getInputArray()));
            System.out.println("Output: " + Arrays.toString(pair.getIdealArray()));
            System.out.println();
        }
    }
    
    
    /**
     * פונקציה האחראית על טעינת סט הנתונים לעצם
     * @param dataSet סט הנתונים לטעינה
     * @param filePath נתיב הקובץ ששם יושבים הנתונים
     */
    private void loadDataset(MLDataSet dataSet, String filePath) 
    {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            int exampleCount = 0;
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null && exampleCount < NUM_EXAMPLES) 
            {
                String[] values = line.split(",");

                // Extract first four features as input
                double[] inputFeatures = new double[NUM_FEATURES];
                for (int i = 0; i < NUM_FEATURES; i++) 
                {
                    inputFeatures[i] = Double.parseDouble(values[i]);
                }

                // Extract output label and encode as one-hot vector
                String label = values[NUM_FEATURES]; 
                double[] oneHot = new double[NUM_GENRES];
                int labelIndex = Arrays.asList(CSVManage.LABELS).indexOf(label);
                if (labelIndex != -1)
                {
                    oneHot[labelIndex] = 1;
                    MLDataPair pair = new BasicMLDataPair(new BasicMLData(inputFeatures), new BasicMLData(oneHot));
                    dataSet.add(pair);
                } 
                else 
                    System.out.println("Label not found in list of possible classes: " + label);

                exampleCount++;
            }
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        
    }
    
    /**
     * פונקציה המגדירה את ארכיטקטורת הרשת
     */
    public void createNetwork()
    {
        // Create network
        ActivationFunction inputLayerActivation = new ActivationSigmoid();
        ActivationFunction hiddenLayerActivation = new ActivationSigmoid();
        ActivationFunction outputLayerActivation = new ActivationSoftMax();
        network = new BasicNetwork();
        
        network.addLayer(new BasicLayer(inputLayerActivation, true, NUM_FEATURES)); // input layer
        
        network.addLayer(new BasicLayer(hiddenLayerActivation, true, NUM_HIDDEN_NEURONS_FIRST)); // hidden layer
        network.addLayer(new BasicLayer(hiddenLayerActivation, true, NUM_HIDDEN_NEURONS_SECOND)); 
        network.addLayer(new BasicLayer(hiddenLayerActivation, true, NUM_HIDDEN_NEURONS_THIRD)); 
        network.addLayer(new BasicLayer(hiddenLayerActivation, true, NUM_HIDDEN_NEURONS_FOURTH));


        network.addLayer(new BasicLayer(outputLayerActivation, false, NUM_GENRES)); // output layer
        network.getStructure().finalizeStructure(); // מסיים את הבנייה של השכבות
        network.reset();
    }
    
    
    /**
     * פונקציית אימון המודל
     * לוקחת את המידע מסט האימון ומתחילה לאמן את המודל על ידי אלגוריתם גרדיאנט דסנט
     * מגידרה בנוסף את הפרמטרים של האימון
     * 
     * בסיום הפעולה שומרת את נתוני המודל בקובץ
     * 
     * יעילות הפעולה
     * O(k * N)
     * k: מסמן את מספר האיטרציות של אלגוריתם הגרדיאנט
     * N: מסמן את גודל סט נתוני האימון
     * 
     * היעילות תלויה בעוד פרמטרים אבל אלו העיקריים
     */
    public void train()
    {          
        // Train network
        System.out.println("I am Starting training now,  Data = \n" + trainingData.toString());
        
        final StochasticGradientDescent trainer = new StochasticGradientDescent(network, trainingData);
        
        final int batchSize = 32;
        final double learningRate = 0.001;
        final double momentum = 0.8;
        
        trainer.setBatchSize(batchSize);
        trainer.setLearningRate(learningRate);
        trainer.setMomentum(momentum);

        
        ErrorCalculation error = new ErrorCalculation();
        
        int epoch = 1; 
        final int MAX_EPOCHS = 70000;
        do
        {
            trainer.iteration();
            error.reset();
            for (MLDataPair pair : trainingData) 
            {
                MLData output = network.compute(pair.getInput());
                error.updateError(output.getData(), pair.getIdeal().getData(), 1.0);
            }
            System.out.println("Epoch #" + epoch + " -> Training error: " + error.calculate());
            epoch++;
            
            if (epoch >= MAX_EPOCHS)
                break; 
            
        } while (error.calculate() > learningRate);
        trainer.finishTraining();

        // Save trained network
        saveModel();
     
    }
    

    
    /**
     * פעולה השומרת את קובץ המודל לאחר האימון
     */
    private void saveModel()
    {
        EncogDirectoryPersistence.saveObject(MODEL_PATH.resolve("my_model.eg").toFile(), network);
        System.out.println("Network saved to " + MODEL_PATH.toAbsolutePath());
    }
    
    /**
     * פונקצייה הטוענת את קובץ המודל הנשמר לאחר האימון
     */
    public void loadModel()
    {
        // Load trained network from file
        network = (BasicNetwork) EncogDirectoryPersistence.loadObject(MODEL_PATH.resolve("my_model.eg").toFile());
        System.out.println("Network loaded from " + MODEL_PATH.resolve("my_model.eg").toFile());
    }
    
   

    
    /**
     * פונקציית חיזוי הז׳אנר
     * 
     * @param inputStream הסטרים של הקובץ לחיזוי
     * @return עצם מסוג חיזוי השומר בתוכו את חיזוי המודל לשיר ואת האחוזים המתאימים לחיזוי
     * מחזירה ערך ריק אם הייתה שגיאה במהלך החיזוי
     * 
     * יעילות הפעולה
     * O(N^2) 
     * N: מסמן את גודל הקובץ
     * 
     * הפעולה קוראת לפעולת חילוץ הפיצ׳רים שאנו
     * מניחים שיעילותה במקרה הגרוע היא
     * O(N^2)
     * 
     */
    public Prediction predictGenre(InputStream inputStream)
    {
        try
        {
            double[] features = extractFeatures(inputStream);
        
            // Normalize features using same parameters used during training
            double[] normalizedFeatures = normalizeFeatures(features);

            MLData input = new BasicMLData(normalizedFeatures);
            MLData output = network.compute(input);

            // Get predicted genre
            int predictedGenreIndex = getMaxIndex(output.getData());
            Genre predictedGenre = Genre.values()[predictedGenreIndex];

            Prediction prediction = new Prediction(predictedGenre);
            double[] percentages = output.getData();
            for (int i = 0; i < percentages.length; i++)
            {
                double percentage = percentages[i] * 100;
                prediction.setPercentages(percentage, i);
            }

            System.out.println(prediction);
            return prediction;
            
        } catch (Exception e)
        {
            System.out.println("Error during prediction");
        }
        
        return null;
    }
    
    
    
    public Genre predictGenre(double [] data) throws IOException //קלאסי  דיסקו פופ
    {        
//        double[] data = 
//        {
//
//            0.40670788,	0.07709428,	0.122531205,	0.00155297,	2333.775152	,84535.54864	,2043.06606,	37656.09873,	4556.674241,	441886.4048,	0.149251701,	0.126975631,	-0.000860726	,0.008819128,	-0.002568836,	0.003162799,	80.74951172
//        
//        };
//        
        // TODO -> get a mp3 / wav file and prediction genre
        
        // Extract audio features from MP3 file
        
        // Normalize features using same parameters used during training
        data = normalizeFeatures(data);
        
        // Pass normalized features through trained neural network
        MLData input = new BasicMLData(data);
        MLData output = network.compute(input);

        // Get predicted genre
        int predictedGenreIndex = getMaxIndex(output.getData());
        Genre predictedGenre = Genre.values()[predictedGenreIndex];
        
        double[] percentages = output.getData();
        for (int i = 0; i < percentages.length; i++)
        {
            Genre genre = Genre.values()[i];
            double percentage = percentages[i] * 100;
            System.out.println(genre + ": " + percentage + "%");
        }

        return predictedGenre;
    }
    
    
    /**
     * פעולת חילוץ הפיצ׳רים של השיר על מנת לקבל חיוזי מהמודל
     * @param inputStream הסטרים של הקובץ לחיזוי
     * @return מערך הפיצ׳רים המתאים לקובץ החיזוי
     * 
     * יעילות הפעולה
     * O(N^2)
     * N: מסמן את גודל הקובץ
     * 
     * מכיוון ואנו משתמשים בספרייה של פייתון על מנת
     * לחלץ את הפיצ׳רים אנו לא בדיוק יודעים מה היעילות של האלגוריתמים שלהם
     * לכן נוכל להניח שהיעילות במקרה הגרוע לא עוברת את
     * O(N^2)
     */
    private double[] extractFeatures(InputStream inputStream) throws Exception 
    {
        // Create a temporary file
        File tempFile = File.createTempFile("temp", null);

        try (FileOutputStream outputStream = new FileOutputStream(tempFile))
        {
            // Copy the data from the input stream to the temporary file
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) 
            {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Create a FileInputStream from the temporary file
        FileInputStream fileInputStream = new FileInputStream(tempFile);

        // Use the fileInputStream as needed
        
        double[] features = FeatureExtractor.extractFeatures(fileInputStream);
        
        // Clean up: delete the temporary file
        tempFile.delete();
        
        return features;
    }

    
    /**
     * פעולת נרמול הפיצ׳רים
     * עוברת על מערך הפיצ׳רים ומחשבת את הערך המנורמל עבור כל פיצ׳ר
     * בעזרת חישוב על ערכי המינימום והמקסימום של סט הנתונים
     * @param features מערך הפיצ׳רים לנרמול
     * @return מערך הפיצ׳רים המנורמל
     * 
     * 
     * יעילות הפעולה
     * O(N)
     * N: מסמן את גודל המערך
     */
    private double[] normalizeFeatures(double[] features)
    {
        for (int i = 0; i < features.length; i++)
        {
            // במידה וניתקל בפיצ׳ר שחורג מגבולות ערכי האימון נטפל בו בדרך שונה
            if (features[i] < featureMins[i])
            {
                features[i] = featureMins[i];
                System.out.println("Im under the Minimum range");
            }
            
            else if (features[i] > featureMaxs[i])
            {
                features[i] = featureMaxs[i];
                System.out.println("Im over the Maxmimum range");
            }
            
            else
                features[i] = normalizeValue(features[i], featureMins[i], featureMaxs[i]);
        }
        
        printArray(features, "Features");
        
        return features;
      
    }
   
    
    /**
     * פונקצייה המנרמלת ערך על ידי חישוב פשוט
     * עם ערך המקסימום שהערך יכול לקבל
     * וערך המינימום שיכול לקבל
     * @param value הערך לנרמול
     * @param min ערך המינימלי שיכול להתקבל לערך
     * @param max ערך המקסימלי שיכול להתקבל לערך
     * @return הערך המנורמל
     * 
     * יעילות הפעולה
     * O(1)
     */
    public double normalizeValue(double value, double min, double max)
    {
        return (value - min) / (max - min);
    }
    
    
    /**
     * פעולה המוצאת את האינדקס שבתא שלו יושב הערך
     * המקסימלי של המערך
     * 
     * יעילות הפעולה
     * O(N)
     * N: מסמן את גודל המערך
     * 
     * @param arr המערך שבו מחפשים את האינדקס של ערך המקסימום
     * @return האינדקס עם הערך המקסימלי במערך
     */
    private int getMaxIndex(double[] arr) 
    {
        int maxIndex = 0;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) 
        {
            if (arr[i] > maxVal) 
            {
                maxVal = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    
    public static void printArray(double[][] arr, String title) 
    {
        System.out.println(title);
        System.out.println("------------------------------");
        for (int i = 0; i < arr.length; i++) 
        {
            for (int j = 0; j < arr[i].length; j++) 
            {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
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

    
    /**
     * פעולה המעריכה את ביצועי המודל
     * על ידי מעבר על סט הבדיקה וניתוח ביצועי המודל
     * בפרמטרים הסטטיסטיים של ביצועי המודל
     * 
     * יעילות הפעולה
     * O(N^2)
     * N: מסמן את גודל המידע בסט הבדיקה
     * 
     * היעילות הזו נובעת מיעילות פונקציית החיזוי
     * 
     */
    public void evaluateModel() 
    {
//        // Load the trained model
//        loadModel();
//
//        // Load the test dataset
//        MLDataSet testData = new BasicMLDataSet();
//        loadDataset(testData, "src/main/java/com/noama/GenreIdentificationServer/Data/testingset.csv");

        int correctPredictions = 0;
        int totalPredictions = testingData.size();

        int[] yTrue = new int[totalPredictions];
        int[] yPred = new int[totalPredictions];

        for (int i = 0; i < totalPredictions; i++) 
        {
            MLDataPair pair = testingData.get(i);

            // Get the input features and true label
            MLData input = pair.getInput();
            MLData target = pair.getIdeal();

            Genre predictedGenre = null;
            try
            {
                // Use the model to predict the genre
                predictedGenre = predictGenre(input.getData());

            } catch (Exception e)
            {
                System.out.println("Error during evaluate model" + e);
            }
            
            if (predictedGenre != null)
            {
                 // Compare the predicted genre with the true label
                if (predictedGenre == Genre.values()[getMaxIndex(target.getData())]) 
                    correctPredictions++;


                // Store true label and predicted label for later evaluation
                yTrue[i] = getMaxIndex(target.getData());
                yPred[i] = predictedGenre.ordinal();
            }
           
        }

        // Calculate accuracy
        double accuracy = (double) correctPredictions / totalPredictions;


        // Calculate precision, recall, and F1 score
        int tp = 0; // True positives
        int fp = 0; // False positives
        int fn = 0; // False negatives

        for (int i = 0; i < totalPredictions; i++) 
        {
            if (yTrue[i] == 1 && yPred[i] == 1)
            {
                tp++;
            } 
            else if (yTrue[i] == 0 && yPred[i] == 1) 
            {
                fp++;
            } 
            else if (yTrue[i] == 1 && yPred[i] == 0) 
            {
                fn++;
            }
        }

        double precision = tp / (double) (tp + fp);
        double recall = tp / (double) (tp + fn);
        double f1Score = 2 * (precision * recall) / (precision + recall);

        System.out.println("The Summary Of The Model Evaluation:\n---------------------------");
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1 Score: " + f1Score);
    }

    
    

}
