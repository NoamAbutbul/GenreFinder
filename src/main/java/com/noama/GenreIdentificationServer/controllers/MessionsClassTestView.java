package com.noama.GenreIdentificationServer.controllers;

import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.services.MongoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;

/**
 *  MessionsClassTestView.
 *  By noamabutbul | 20/02/2023 13:21
 */

@PageTitle("Mession Test Class")
@Route("/messionTest")
public class MessionsClassTestView extends VerticalLayout
{
    private MongoService mongoService;
    
    private TextField searchFiled;
    private Button btnTask1;
    private Button btnTask2;
    private Grid<Song> results;

    
    public MessionsClassTestView(MongoService mongoService)
    {
        this.mongoService = mongoService;
        
        
        searchFiled = new TextField("Name:");
        
        HorizontalLayout layoutForButtons = new HorizontalLayout();
        
        btnTask1 = new Button("Task1");
        btnTask1.addClickListener(event -> search(searchFiled.getValue()));
        
        btnTask2 = new Button("Task2");
        btnTask2.addClickListener(event -> deleteAllByStartLetter(searchFiled.getValue()));

        
        layoutForButtons.add(btnTask1, btnTask2);
        
        results = new Grid<>(Song.class, false);
        
        results.addColumn(Song::getName).setHeader("Name").setSortable(true);
        results.addColumn(Song::getGenre).setHeader("Genre").setSortable(true);
        
        results.setAllRowsVisible(true);
        
        results.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        
        add(searchFiled, layoutForButtons, results);
        
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private void search(String searchStr)
    {
        cleanGrid();
        
        if (searchStr.equals(""))
        {
            showAllSongs();
            return;
        }
        
        Song song = mongoService.getSong(searchStr);
        
        System.out.println("Song searched: " + song);

        if (song != null)
            results.setItems(song);
        else
            Notification.show("Not Found", 4000, Notification.Position.MIDDLE);

    }

    private void deleteAllByStartLetter(String searchLetter)
    {
        ArrayList<Song> songsDeleted = (ArrayList<Song>) mongoService.deleteSongsByStartLetter(searchLetter);
        
        results.setItems(songsDeleted);
        
        Notification.show("Ammount of deleted songs is: " + songsDeleted.size(), 6000, Notification.Position.MIDDLE);
    }
    
    private void showAllSongs()
    {
        results.setItems(mongoService.getAllSongs());
    }
    
    private void cleanGrid()
    {
        results.setItems();
    }
    
}