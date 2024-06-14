package com.noama.GenreIdentificationServer.controllers;

import com.noama.GenreIdentificationServer.model.MusicGenreClassifier;
import com.noama.GenreIdentificationServer.model.Prediction;
import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.services.MongoService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import java.io.InputStream;
import java.util.List;

/**
 *  MainView. מחלקה המגדירה את הדף הראשי במערכת
 *  By noamabutbul | 09/04/2023 03:02
 */

//@Theme(variant = Lumo.DARK)
@PageTitle(Titles.MAIN_PAGE_TITLE)
@Route(Routes.MAIN_PAGE)
public class MainView extends VerticalLayout
{
    private MongoService mongoService; // שירותי המונגו
    
    private String username; // שם המשתמש הנוכחי
    
    private AppLayoutNavbar navbar; // סרגל הניווט
    
    private H1 title; // כותרת הדף
    private Button btnGetPrediction; // כפתור קבלת החיזוי
    private Label labelPopPercentage; // תווית לפופ
    private Label labelRockPercentage; // תווית לרוק
    private Label labelClassicalPercentage; // תווית לקלאסית
    private Label labelDiscoPercentage; // תווית לדיסקו

    private Button btnHistory; // כפתור היסטוריית החיזויים
    private Button clearHistory; // כפתור מחיקת היסטוריית החיזויים
    
    private Paragraph hint; // הסבר על תמיכת הקבצים
    private MemoryBuffer buffer; // באפר להעלאת הקובץ
    private Upload upload; // כלי להעלאת קבצים
    private boolean uploadSucceeded; // משתנה השומר אם העלאה הצליחה
    
    // info file:
    private InputStream fileData;
    private String fileName;
    private long contentLength;
    private String mimeType;
    
    
    private boolean isManager; // משתנה השומר האם המשתמש הוא מנהל
    
    private MusicGenreClassifier MGC; // עצם השומר את כלי הסיווג

    
    public MainView(MongoService mongoService)
    {
        //setTheme(true);
        this.isManager = false;
        
        if (!checkAccess())
            return;
        
        this.mongoService = mongoService;
        
        this.username = Cookies.getCookie(Cookies.USER_OK);
        
        this.MGC = new MusicGenreClassifier();
        this.MGC.createNetwork();
        this.MGC.loadData();
        this.MGC.loadModel();
        
        uploadSucceeded = false;
        
        navbar = new AppLayoutNavbar(isManager);
        
        title = new H1(Titles.MAIN_H1_TITLE);
        title.getStyle().set("color", "blue");
        title.getStyle().set("padding", "0");
        title.getStyle().set("margin", "0");
        
        btnGetPrediction = new Button("Get Genre Prediction");
        btnGetPrediction.getStyle().set("color", "green");
        
        //VerticalLayout predictionPercentages = new VerticalLayout();
        labelClassicalPercentage = new Label();
        labelPopPercentage = new Label();
        labelDiscoPercentage = new Label();
        labelRockPercentage = new Label();
        //predictionPercentages.add(labelPopPercentage, labelRockPercentage, labelClassicalPercentage, labelDiscoPercentage);
        
        
        btnGetPrediction.addClickListener(event ->
        {
            try
            {
                getPrediction();
            } catch (Exception ex)
            {
                System.out.println("Error Upload Song");
            } 
        });
        
        
        
        hint = new Paragraph("Maximum file size: 1 GB | Accepted file formats: WAV (.wav)");

        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setDropAllowed(true);
        int maxFileSizeInBytes = 3 * 1024 * 1024 * 1024; // 3GB
        upload.setMaxFileSize(maxFileSizeInBytes);
        upload.setAcceptedFileTypes("application/wav", ".wav");
        
        upload.addSucceededListener(event ->
        {
            // Save information about the uploaded file
            fileData = buffer.getInputStream();
            fileName = event.getFileName();
//            contentLength = event.getContentLength();
//            mimeType = event.getMIMEType();
            
             
            uploadSucceeded = true;
        });
        
        upload.addFileRejectedListener(event ->
        {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        
        
        btnHistory = new Button("Show History");
        btnHistory.addClickListener(event -> showHistory());
        
        clearHistory = new Button("Clear History");
        clearHistory.getStyle().set("color", "red");
        clearHistory.addClickListener(event -> clearHistory());
       
        
        
        
        add(navbar, title, new HorizontalLayout(btnHistory, clearHistory), hint, upload, btnGetPrediction, labelClassicalPercentage, labelPopPercentage, labelDiscoPercentage, labelRockPercentage);
        
        
        //setSizeFull();ס
        setAlignItems(FlexComponent.Alignment.CENTER);
        //setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

   
    /**
     * פעולת החיזוי
     */
    private void getPrediction()
    {
        if (uploadSucceeded)
            processFile(fileData, fileName);
        else
            Notification.show(Alerts.UPLOAD_NOT_SUCCEEDED , 4000, Notification.Position.MIDDLE);
    }

    /**
     * פעולה המנווטת לדף היסטוריית החיזויים
     */
    private void showHistory()
    {
        UI.getCurrent().getPage().setLocation(Routes.HISTORY_PAGE);
    }

    /**
     * פעולת עיבוד הקובץ לחיזוי
     * הפעולה מפעילה את כלי הסיווג ומשנה את תוויות התוצאה
     * @param fileData סטרים הקבוץ לחיוזי
     * @param fileName שם הקובץ לחיזוי
     */
    private void processFile(InputStream fileData, String fileName)
    {                
        
        //Genre genre = MGC.predictGenre(fileData);
        Prediction prediction = MGC.predictGenre(fileData);
        if (prediction != null)
        {
            labelClassicalPercentage.setText("Classical: " + Math.round(prediction.getPercentages()[0]) + "%");
            labelClassicalPercentage.getStyle().set("color", "brown");

            labelPopPercentage.setText("Pop: " + Math.round(prediction.getPercentages()[1]) + "%");
            labelPopPercentage.getStyle().set("color", "Teal");

            labelDiscoPercentage.setText("Disco: " + Math.round(prediction.getPercentages()[2]) + "%");
            labelDiscoPercentage.getStyle().set("color", "Purple");

            labelRockPercentage.setText("Rock: " + Math.round(prediction.getPercentages()[3]) + "%");
            labelRockPercentage.getStyle().set("color", "black");

            Song song = new Song(fileName, prediction.getGenre());


            addSongToHistory(username, song);
        }
        else
            Notification.show(Alerts.ERROR, 4000, Notification.Position.MIDDLE);
        
    }
    
    /**
     * פעולה המוסיפה את השיר אל היסטוריית החיזויים
     * @param username שם המשתמש
     * @param song השיר להוספה
     */
    private void addSongToHistory(String username, Song song)
    {
        List<Song> songs = mongoService.getSongsHistory(username);
        System.out.println("songs from addSongToHistory(String username, Song song) " + songs);
        songs.add(song);
        System.out.println("songs from addSongToHistory(String username, Song song) " + songs);

        mongoService.getUser(username).setSongs(songs);
                
        mongoService.addSongToHistoryUser(username, song);
    }

    /**
     * פעולה הבודקת את הגישה לדף
     * @return אמת במידה ולמשתמש יש גישה אחרת שקר
     */
    private boolean checkAccess()
    {
        String userLogedin = Cookies.getCookie(Cookies.USER_OK);        
        String isManager = Cookies.getCookie(Cookies.MANAGER);
        
        if (userLogedin == null)
        {
            UI.getCurrent().getPage().setLocation(Routes.LOGIN_PAGE);
            return false;
        }
        
        if (isManager == null)
        {
           this.isManager = false;
        }
        else
            this.isManager = true;
            
        
        return true;
    }

    /**
     * פעולה המוחקת את היסטוריית החיזויים
     */
    private void clearHistory()
    {
        try
        {
            mongoService.deleteHistoryUser(username);
            Notification.show(Alerts.HISTORY_DELETED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
        } catch (Exception e)
        {
            Notification.show(Alerts.HISTORY_NOT_DELETED, 4000, Notification.Position.MIDDLE);
        }
        
    }
    
    // TODO -> Work
    private void setTheme(boolean dark) 
    {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }
     
}
