package com.noama.GenreIdentificationServer.controllers;

import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.services.MongoService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

/**
 *  HisrotyView. מחלקה המגדירה את דף היסטוריית החיזויים
 *  By noamabutbul | 18/05/2023 12:42
 */

@PageTitle(Titles.HISTORY_PAGE_TITLE)
@Route(Routes.HISTORY_PAGE)
public class HisrotyView extends VerticalLayout
{
    private MongoService mongoService; // שירותי המונגו
    
    private AppLayoutNavbar navbar; // סרגל הניווט
    private H1 title; // כותרת הדף
    private Grid<Song> grid; // גריד לשירים
        
    private boolean isManager; // משתנה השומר האם המשתמש הוא מנהל
    private String username; // שם המשתמש הנוכחי

    
    
    public HisrotyView(MongoService mongoService)
    {
        this.isManager = false;
        
        // allow accses to page
        if (!checkAccess())
            return;
        
        
        this.mongoService = mongoService;
        
        this.username = Cookies.getCookie(Cookies.USER_OK);
        
        navbar = new AppLayoutNavbar(isManager);
                
        title = new H1(Titles.HISTORY_H1_TITLE);
        title.getStyle().set("color", "green");
        title.getStyle().set("padding", "0");
        title.getStyle().set("margin", "0");
        
        
        HorizontalLayout content = new HorizontalLayout();

        grid = new Grid<>(Song.class, false);
       
        
        grid.addColumn(Song::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Song::getGenre).setHeader("Genre").setSortable(true);
        
        grid.setAllRowsVisible(true);
        
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        
        List<Song> songs = this.mongoService.getSongsHistory(username);
        grid.setItems(songs);
        
    
        content.setWidthFull();
        content.expand(grid);
        content.add(grid);
        content.getStyle().set("margin-right", "50px");
        
        add(navbar, title, content);
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
}
