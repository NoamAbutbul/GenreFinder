package com.noama.GenreIdentificationServer.controllers;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

/**
 *  AppLayoutNavbarPlacementSide. מחלקה המגדירה את סרגל ניווט הצד
 *  By noamabutbul | 16/04/2023 18:01
 */

public class AppLayoutNavbarPlacementSide extends AppLayout
{
    public static final String FIND_GENRE = "Find Genre";
    public static final String SONGS_MANAGMENT = "Songs Management";
    public static final String USERS_MANAGMENT = "Users Management";
    public static final String LOGOUT = "Logout";
    
    
    private DrawerToggle toggle;
    private H1 title;
    private Tabs tabs;

    
     public AppLayoutNavbarPlacementSide() 
     {
        toggle = new DrawerToggle();

        title = new H1("Genre Finder");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        tabs = getTabs();

        addToDrawer(tabs);
        addToNavbar(toggle, title);

        setPrimarySection(Section.DRAWER);
    }
     
    private Tabs getTabs()
    {
        Tabs tabs = new Tabs();
        tabs.add(createTab(VaadinIcon.GLOBE, FIND_GENRE),
                createTab(VaadinIcon.MUSIC, SONGS_MANAGMENT),
                createTab(VaadinIcon.USERS, USERS_MANAGMENT),
                createTab(VaadinIcon.SIGN_OUT, LOGOUT));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }
    
    private Tab createTab(VaadinIcon viewIcon, String viewName) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        
        
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

    private void logout()
    {
        // TODO -> logout
    }
    
    public void hiddenFromNotManagers()
    {
        
    }
}
