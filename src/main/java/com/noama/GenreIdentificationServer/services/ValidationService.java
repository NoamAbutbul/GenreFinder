package com.noama.GenreIdentificationServer.services;

import com.noama.GenreIdentificationServer.model.User;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 *  ValidationService. מחלקה המספקת שירות לאימות המשתמש
 *  By noamabutbul | 16/02/2023 11:14
 */

@Service
public class ValidationService
{
    private MongoService mongoService; // שירותי המונגו
    
    public ValidationService(MongoService mongoService)
    {
        this.mongoService = mongoService;
    }
    
    /**
     * פעולה הבודקת את שם המשתמש והסיסמא של המשתמש
     * @param username שם המשתמש
     * @param password הסיסמא
     * @return אמת במידה והמשתמש מאומת אחרת שקר
     */
    public boolean validUserLogin(String username, String password)
    {
        // user not exist
        if (mongoService.getUser(username) == null)
            return false;
        
        // password correct for username
        else if(mongoService.getUser(username).getPassword().equals(password))
            return true;
        
        else
            return false;
    }
    
    /**
     * פעולה המוודאת אם רישום המשתמש תקין
     * @param username שם המשתמש החדש
     * @param password הסיסמא החדשה
     * @return אמת במידה ותקין אחרת שקר
     */
    public boolean validUserSignup(String username, String password)
    {
        // user already exist
        if (mongoService.getUser(username) != null)
            return false;
        
        return true;
    }
    
    /**
     * פעולה הרושמת משתתמש חדש במסד הנתונים
     * @param username שם המשתמש החדש
     * @param password הסיסמא החדשה
     * @return אמת במידה והרישום צלח אחרת שקר
     */
    public boolean registerUser(String username, String password)
    {
        User user = new User(username, password, User.Type.NORMAL_USER, new ArrayList<>());
        return mongoService.addUser(user);
    }
    
    /**
     * פעולה הבודקת האם משתמש הוא מנהל
     * @param username שם המשתמש לבדיקה
     * @return אמת במידה וכן אחרת שקר
     */
    public boolean isManager(String username)
    {
        User user = mongoService.getUser(username);
        return user.getType() == User.Type.MANGER_USER;
    }
    
    
   
}
