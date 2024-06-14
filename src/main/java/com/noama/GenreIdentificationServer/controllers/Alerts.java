package com.noama.GenreIdentificationServer.controllers;

/**
 *  Alerts. מחלקה המגדירה את ההודעות למשתמש
 *  By noamabutbul | 16/05/2023 17:19
 */
public abstract class Alerts
{
    // general alerts:
    public static final String NOT_FOUND = "Not Found!";
    public static final String ACCESS_DENIED = "You do not have permission to view this information";
    public static final String ERROR = "There is an error, please try again ";

    
    // alerts for login page:
    public static final String PASSWORD_NOT_MATCH = "Passwords not match ";
    public static final String USERNAME_ALREADY_USED = "The username is already used, please choose another ";
    public static final String REGISTERED_SUCCESSFULLY = "You have successfully registered ";
    public static final String LOGIN_SUCCESS = "login success";
    public static final String LOGIN_NOT_SUCCESS = "username or password incorrect";
    
    // alerts for main page:
    public static final String UPLOAD_NOT_SUCCEEDED = "upload didn't succeeded ";
    public static final String HISTORY_DELETED_SUCCESSFULLY = "History was Deleted Successfully!";
    public static final String HISTORY_NOT_DELETED = "History NOT DELETED";



    // alerts for songs page
    public static final String SONG_ADDED_SUCCESSFULLY = "Song was Added Successfully!";
    public static final String SONG_NOT_ADDED = "Song NOT Added!";

    public static final String SONG_UPDATED_SUCCESSFULLY = "Song was Updated Successfully!";
    public static final String SONG_NOT_UPDATED = "Song NOT Updated!";
    
    public static final String SONG_DELETED_SUCCESSFULLY = "Song was Deleted Successfully!";
    public static final String SONG_NOT_DELETED = "Song NOT Deleted!";

            
    // alerts for users page
    public static final String USER_ADDED_SUCCESSFULLY = "User was Added Successfully!";
    public static final String USER_NOT_ADDED = "User NOT Added!";
    
    public static final String USER_UPDATED_SUCCESSFULLY = "User was Updated Successfully!";
    public static final String USER_NOT_UPDATED = "User NOT Updated!";
    
    public static final String USER_DELETED_SUCCESSFULLY = "User was Deleted Successfully!";
    public static final String USER_NOT_DELETED = "User NOT Deleted!";

}
