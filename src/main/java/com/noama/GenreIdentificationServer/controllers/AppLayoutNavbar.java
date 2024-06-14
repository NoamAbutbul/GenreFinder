package com.noama.GenreIdentificationServer.controllers;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

/**
 *  AppLayoutNavbar. מחלקה המגדירה את סרגל הניווט העליון
 *  By noamabutbul | 09/02/2023 00:22
 */
public class AppLayoutNavbar extends AppLayout
{
    private static final String FIND_GENRE = "Find Genre";
    private static final String SONGS_MANAGMENT = "Songs Management";
    private static final String USERS_MANAGMENT = "Users Management";
    private static final String LOGOUT = "Logout";
    
    private H1 title; // כותרת האפליקציה
    private Button btnLogout; // כפתור התנתקות
    private Tabs tabs; // טאבים לניווט
    private boolean isManager; // משתנה השומר האם המשתמש הוא מנהל
    
    public AppLayoutNavbar(boolean isManager) 
    {
        this.isManager = isManager;
        
        title = new H1(Titles.APP_TITLE);
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");
        
        title.addClickListener(event -> routeToMainPage());

        tabs = getTabs();
        
        btnLogout = new Button(LOGOUT);
        btnLogout.addClickListener(event -> logout());
        btnLogout.getStyle().set("margin", "50");
        btnLogout.getStyle().set("color", "red");
        

        addToNavbar(title, tabs, btnLogout);
    }
    
    private Tabs getTabs() 
    {
        Tabs tabs = new Tabs();
        tabs.getStyle().set("margin", "auto");
        if (isManager)
            tabs.add(createTab(FIND_GENRE), createTab(SONGS_MANAGMENT), createTab(USERS_MANAGMENT));
        else
            tabs.add(createTab(FIND_GENRE));
        return tabs;
    }

    private Tab createTab(String viewName) 
    {
        RouterLink link = new RouterLink();
        link.add(viewName);
        
        // routs and functions:
        switch (viewName)
        {
            case FIND_GENRE -> link.setRoute(MainView.class);
                
            case SONGS_MANAGMENT -> link.setRoute(SongsView.class);
                
            case USERS_MANAGMENT -> link.setRoute(UsersView.class);
                
            case LOGOUT -> logout();
                    
            default ->
            {
            }
        }
        
        link.setTabIndex(-1);

        return new Tab(link);
    }

    /**
     * פעולת ההתנתקות מהמערכת
     */
    private void logout()
    {
        Cookies.destoryCookies();
    }
   
    /**
     * פעולה המנווטת לדף הראשי
     */
    private void routeToMainPage()
    {
        UI.getCurrent().navigate(Routes.MAIN_PAGE);
    }
}
