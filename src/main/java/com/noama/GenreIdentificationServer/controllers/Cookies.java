package com.noama.GenreIdentificationServer.controllers;

import com.vaadin.flow.server.VaadinSession;

/**
 *  Cookies. מחקלה לניהול העוגיות במערכת
 *  By noamabutbul | 16/05/2023 17:27
 */
public abstract class Cookies
{
    // cookie types:
    public static final String USER_OK = "userOk";
    public static final String MANAGER = "Manager";
    
    /**
     * פעולה השומרת עוגייה אצל המשתמש
     * @param cookieType סוג העוגייה
     * @param username שם המשתמש
     */
    public static void putCookie(String cookieType, String username)
    {
        VaadinSession.getCurrent().setAttribute(cookieType, username); // put cookie attribute 
        System.out.println("Added to " + username + " " + cookieType + " Cookie");
    }
    
    /**
     * פעולה המוחקת את כל העוגיות של המשתמש
     */
    public static void destoryCookies()
    {
        VaadinSession.getCurrent().getSession().invalidate(); 
        System.out.println("All Cookies were Deleted");
    }
    
    /**
     * פעולה המחזירה את העוגייה הקיימת
     * @param cookieType סוג העוגייה
     * @return מחרוזת העוגייה במידה והמחרוזת ריקה
     * אין את העוגייה הזו למשתמש
     */
    public static String getCookie(String cookieType)
    {
       return (String) VaadinSession.getCurrent().getAttribute(cookieType);
    }
}
