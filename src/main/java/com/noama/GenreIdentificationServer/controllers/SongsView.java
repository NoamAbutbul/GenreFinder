package com.noama.GenreIdentificationServer.controllers;

import com.noama.GenreIdentificationServer.model.Genre;
import com.noama.GenreIdentificationServer.services.MongoService;
import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.services.GenreService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;

/**
 *  SongsView. מחלקה המגדירה את דף ניהול השירים במערכת
 *  By noamabutbul | 08/02/2023 16:44
 */

@PageTitle(Titles.SONG_PAGE_TITLE)
@Route(Routes.SONGS_PAGE)
public class SongsView extends VerticalLayout
{    
    private MongoService mongoService; // שירותי המונגו
    
    private AppLayoutNavbar navbar; // סרגל הניווט
    private H1 title; // כותרת הדף
    private Grid<Song> grid; // גריד לשירים
    
    private TextField filter; // שדה לחיפוש שיר
    private Button btnAdd; // כפתור להוספת שיר
    private Button btnUpdate; // כפתור לעדכון שיר
    private Button btnDelete; // כפתור למחיקת שיר
    private Button btnClearSongForm; // כפתור לניקוי טופס השיר
    
    // טופס השיר
    private TextField name; //שם השיר 
    private ComboBox<Genre> genre; // ז׳אנר השיר
    
    private boolean isManager; // משתנה השומר האם המשתמש הוא מנהל


    public SongsView(MongoService mongoService)
    {
        this.isManager = false;
        
        // allow accses to page
        if (!checkAccess())
            return;
        
        this.mongoService = mongoService;
        
        navbar = new AppLayoutNavbar(isManager);
                
        title = new H1(Titles.SONG_H1_TITLE);
        title.getStyle().set("color", "green");
        title.getStyle().set("padding", "0");
        title.getStyle().set("margin", "0");
        
        HorizontalLayout operationBar = new HorizontalLayout();
        
        filter = new TextField();
        filter.setPlaceholder("Filter by name");
        filter.addKeyPressListener(event -> search(filter.getValue(), event.getKey()));

        btnAdd = new Button("Add Song");
        btnAdd.addClickListener(event -> addSongToDB());
        
        btnUpdate = new Button("Update Song");
        btnUpdate.addClickListener(event -> updateSongInDB());
        btnUpdate.setEnabled(false);

        btnDelete = new Button("Delete Song");
        btnDelete.addClickListener(event -> deleteSongFromDB());
        btnDelete.setEnabled(false);
        
        operationBar.add(filter, btnAdd, btnUpdate, btnDelete);

        
        HorizontalLayout content = new HorizontalLayout();

        grid = new Grid<>(Song.class, false);
       
        
        grid.addColumn(Song::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Song::getGenre).setHeader("Genre").setSortable(true);
        
        grid.setAllRowsVisible(true);
        
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        
        List<Song> songs = mongoService.getAllSongs();
        grid.setItems(songs);
        
        grid.addItemClickListener(event ->
        {
            Song song = event.getItem();
            fillSongForm(song);
        });
        
        content.setWidthFull();
        content.expand(grid);
        content.add(grid, createSongForm());
        content.getStyle().set("margin-right", "50px");
        
        add(navbar, title, operationBar, content);
        
        //setSizeFull();
    }
    
    
    /**
     * פעולה היוצרת את טופס השיר
     * @return פאנל טופס השיר
     */
    private VerticalLayout createSongForm()
    {
        VerticalLayout songForm = new VerticalLayout();
        
        H3 title = new H3(Titles.SONG_FORM_H3_TITLE);
        title.getStyle().set("color", "green");
        title.getStyle().set("padding", "0");
        title.getStyle().set("margin", "0");
        songForm.setWidth("300px");

        name = new TextField("Name");
        genre = new ComboBox<>("Genre");
        genre.setItems(GenreService.allGenres());
       
        btnClearSongForm = new Button("Clear Form");
        btnClearSongForm.addClickListener(event -> clearSongForm());

        songForm.add(title, name, genre, btnClearSongForm);
        songForm.expand(name);

        return songForm;
    }
    
    
   
    /**
     * פעולה הממלאת את טופס השיר
     * @param song השיר שממנו נמלא את המידע
     */
    private void fillSongForm(Song song)
    {
        name.setValue(song.getName());
        genre.setValue(song.getGenre());

        name.setReadOnly(true);
        btnDelete.setEnabled(true);
        btnUpdate.setEnabled(true);
        
        btnAdd.setEnabled(false);
    }
    
    
    /**
     * פעולה המנקה את טופס השיר
     */
    private void clearSongForm()
    {
        name.clear();
        name.setReadOnly(false);
        
        genre.clear();
        
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAdd.setEnabled(true);

    }
    
    /**
     * פעולה המוסיפה את השיר אל מסד הנתונים
     */
    private void addSongToDB()
    {
        Song song = new Song(name.getValue(), genre.getValue());

        if (mongoService.addSong(song))
        {
            Notification.show(Alerts.SONG_ADDED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            refreshGrid();
            clearSongForm();
        }
        else
        {
            Notification.show(Alerts.SONG_NOT_ADDED, 4000, Notification.Position.MIDDLE);
            System.out.println(">>>> " + "Song NOT Added!");
        }
    }

    /**
     * פעולה המעדכנת את השיר במסד הנתונים
     */
    private void updateSongInDB()
    {
        if (mongoService.updateSong(name.getValue(), genre.getValue()))
        {
            Notification.show(Alerts.SONG_UPDATED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            refreshGrid();
        }
        else
        {
            Notification.show(Alerts.SONG_NOT_UPDATED, 4000, Notification.Position.MIDDLE);
            System.out.println(">>>> " + "Song NOT Updated!");
        }
    }

    /**
     * פעולה המוחקת את השיר ממסד הנתונים
     */
    private void deleteSongFromDB()
    {
        if (mongoService.deleteSong(name.getValue()))
        {
            Notification.show(Alerts.SONG_DELETED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            refreshGrid();
            clearSongForm();
        }
        else
        {
            Notification.show(Alerts.SONG_NOT_DELETED, 4000, Notification.Position.MIDDLE);
            System.out.println(">>>> " + "Song NOT Deleted!");
        }
    }
    
    /**
     * פעולה המרעננת את גריד השירים
     */
    private void refreshGrid()
    {
        grid.setItems(mongoService.getAllSongs());
    }

    /**
     * פעולה המחפשת שיר במסד הנתונים
     * @param searchStr שם השיר
     * @param key המקש במקלדת
     */
    private void search(String searchStr, Key key)
    {
        ArrayList<Song> songsFilter = (ArrayList<Song>) mongoService.findByNameStartsWith(searchStr);
         
        if (songsFilter.isEmpty() && key == Key.ENTER)
            Notification.show(Alerts.NOT_FOUND, 4000, Notification.Position.MIDDLE);
        
        grid.setItems(songsFilter);
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
            Notification.show(Alerts.ACCESS_DENIED, 4000, Notification.Position.MIDDLE);
            UI.getCurrent().getPage().setLocation(Routes.MAIN_PAGE);
            this.isManager = false;
            return false;
        }
        else
            this.isManager = true;
        
        return true;
    }
}
