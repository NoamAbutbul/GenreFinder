package com.noama.GenreIdentificationServer.services;

import com.noama.GenreIdentificationServer.model.User;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *  TypeService. מחלקה המספקת שירות לסוגי המשתמשים
 *  By noamabutbul | 11/05/2023 11:47
 */

@Service
public abstract class UserTypeService
{
    public static final String TYPE_NORMAL_STRING = "Normal"; // מחרוזת למשתמש רגיל
    public static final String TYPE_MANAGER_STRING = "Manager"; // מחרוזת למשתמש מנהל
    
    /**
     * פעולה המחזירה את כל סוגי המשתמשים
     * @return 
     */
    public static ArrayList<User.Type> allTypes()
    {
       ArrayList<User.Type> types = new ArrayList<>();
       types.add(User.Type.NORMAL_USER);
       types.add(User.Type.MANGER_USER);

       return types;
    }
    
    /**
     * פעולה המחזירה את סוגי המשתמשים במחרוזת
     * @return 
     */
    public static ArrayList<String> allTypesInString()
    {
       ArrayList<String> types = new ArrayList<>();
       types.add(TYPE_NORMAL_STRING);
       types.add(TYPE_MANAGER_STRING);

       return types;
    }
    
    /**
     * פעולה הממירה את סוג המשתמש למחרוזת המתאימה לו
     * @param type סוג המשתמש
     * @return מחרוזת המתאימה לסוג המשתמש
     */
    public static String convertTypeToString(User.Type type)
    {
        switch (type)
        {
            case NORMAL_USER ->
            {
                return TYPE_NORMAL_STRING;
            }
                
            case MANGER_USER ->
            {
                return TYPE_MANAGER_STRING;
            }
  
            default ->
            {
            }
        }
        
        return null;
    }
    
    /**
     * פעולה הממירה את המחרוזת של המשתמש לסוג המתאים לו
     * @param typeString מחרוזת המתארת את סוג המשתמש
     * @return סוג המשתמש המתאים למחרוזת
     */
    public static User.Type convertStringToType(String typeString)
    {
        switch (typeString)
        {
            case TYPE_NORMAL_STRING:
                return User.Type.NORMAL_USER;
                
            case TYPE_MANAGER_STRING:
                return User.Type.MANGER_USER;
  
            default:
        }
        
        return null;
    }
}
