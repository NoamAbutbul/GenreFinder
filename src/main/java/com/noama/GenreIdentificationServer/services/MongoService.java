package com.noama.GenreIdentificationServer.services;

import com.noama.GenreIdentificationServer.model.Genre;
import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.model.User;
import com.noama.GenreIdentificationServer.model.User.Type;
import com.noama.GenreIdentificationServer.repositories.SongRepository;
import com.noama.GenreIdentificationServer.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * AppService.
 * By noamabutbul | 25/01/2023 15:44
 */

@Service
public class MongoService
{
    private SongRepository songRepo; // מממשק פעולות השירים
    private UserRepository userRepo; // ממשק םעולות המשתמשים
    // Antother Repositories...

    public MongoService(SongRepository songRepo, UserRepository userRepo)
    {
        this.songRepo = songRepo;
        this.userRepo = userRepo;
    }

    // Functions for Song Service:
    
    /**
     * פעולה המחזירה את רשימת כל השירים במסד הנתונים
     * @return רשימת כל השירים במסד הנתונים
     */
    public List<Song> getAllSongs()
    {
        List<Song> songs = songRepo.findAll();

        System.out.println("songs = " + songs);

        return songs;
    }

    /**
     * פעולה המוסיפה שיר למסד הנתונים
     * @param song השיר להוספה
     * @return אמת במידה וההוספה הצליחה אחרת שקר
     */
    public boolean addSong(Song song)
    {
        try
        {
            // valid if song already exist
            if (getSong(song.getName()) != null)
            {
                return false;
            }

            long countBefore, countAfter;
            countBefore = songRepo.count();
            songRepo.insert(song);
            countAfter = songRepo.count();

            return (countBefore + 1 == countAfter);
        } 
        catch (Exception e)
        {
             System.out.println("ERROR from 'boolean addSong(Song song)' " + e);
             return false;
        }
        
    }

    /**
     * פעולה המוצאת שיר על ידי השם שלו
     * @param name שם השיר
     * @return השיר שנמצא
     * במידה ולא נמצא השיר מחזיר ערך ריק
     */
    public Song getSong(String name)
    {
        try
        {
            Optional<Song> optional = songRepo.findById(name);

            if (optional.isEmpty())
                return null;
            
            // song that returned from songRepo.findById(name) if not empty
            return optional.get();
            
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'Song getSong(String name)' " + e);
            return null;
        }
    }

    /**
     * פעולה המוחקת שיר ממסד הנתונים
     * @param name שם השיר למחיקה
     * @return אמת במידה ונמחק אחרת שקר
     */
    public boolean deleteSong(String name)
    {
        try
        {
            Song song = getSong(name);

            if (song != null)
            {
                songRepo.deleteById(name);
                return true;
            }

            return false;
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'boolean deleteSong(String name)' " + e);
            return false;
        }
    }

    /**
     * פעולה המעדכנת שיר במסד הנתונים
     * @param name שם השיר
     * @param gener ז׳אנר השיר
     * @return אמת במידה והשיר עודכן אחרת שקר
     */
    public boolean updateSong(String name, Genre gener)
    {
        try
        {
            Song song = new Song(name, gener);
            songRepo.save(song);
            return true;
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'void updateSong(String name, int duration, Genre gener)' " + e);
            return false;
        }
    }
    
    /**
     * פעולה המוחקת את כל השירים שמתחילים במחרוזת מסויימת
     * @param startletter מחרוזת התחלת השם
     * @return רשימת השירים שנמחקו
     */
    public List<Song> deleteSongsByStartLetter(String startletter)
    {
        List<Song> songs = songRepo.findByNameStartsWith(startletter);
        songRepo.deleteAll(songs);
        return songs;
    }
         
    
    /**
     * פעולה המוצאת שירים על ידי תחילת שמם
     * @param startletter מחזורת תחילת השם
     * @return רשימת השירים שנמצאו
     */
    public List<Song> findByNameStartsWith(String startletter)
    {
        return songRepo.findByNameStartsWith(startletter);
    }
    
    
    // --------------------------------------------------------------------------------------------------------- //
    
    // Functions for User Service:
    
    
     /**
     * פעולה המחזירה את רשימת כל המשתמשים במסד הנתונים
     * @return רשימת כל המשתמשים במסד הנתונים
     */
    public List<User> getAllUsers()
    {
        List<User> users = userRepo.findAll();

        System.out.println("users = " + users);

        return users; 
    }

     /**
     * פעולה המוסיפה משתמש למסד הנתונים
     * @param username המשתמש להוספה
     * @return אמת במידה וההוספה הצליחה אחרת שקר
     */
    public boolean addUser(User username)
    {
        try
        {
            // valid if user already exist
            if (getUser(username.getUsername()) != null)
            {
                return false;
            }

            long countBefore, countAfter;
            countBefore = userRepo.count();
            userRepo.insert(username);
            countAfter = userRepo.count();

            return (countBefore + 1 == countAfter);
        } 
        catch (Exception e)
        {
             System.out.println("ERROR from 'boolean addUser(User user)' " + e);
             return false;
        }
    }

    /**
     * פעולה המוצאת משתמש על ידי השם שלו
     * @param username שם המשתמש
     * @return המשתמש שנמצא
     * במידה ולא נמצא המשתמש מחזיר ערך ריק
     */
    public User getUser(String username)
    {
        try
        {
            Optional<User> optional = userRepo.findById(username);

            if (optional.isEmpty())
                return null;
            
            // user that returned from songRepo.findById(name) if not empty
            return optional.get();
            
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'User getUser(String username)' " + e);
            return null;
        }
    }
  
    /**
     * פעולה המחזירה את רשימת היסטוריית החיזויים של המשתמש
     * @param username שם המשתמש
     * @return רשימת היסטוריית החיזויים שלו
     * במידה ולא נמצא מחזיר ערך ריק
     */
    public List<Song> getSongsHistory(String username)
    {
        try
        {
            Optional<User> optional = userRepo.findById(username);

            if (optional.isEmpty())
                return null;
            
            // user that returned from songRepo.findById(name) if not empty
            return optional.get().getSongs();
            
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'List<Song> getSongsHistory(String username)' " + e);
            return null;
        }
    }

    /**
     * פעולה המוחקת משתמש ממסד הנתונים
     * @param username שם המשתמש למחיקה
     * @return אמת במידה ונמחק אחרת שקר
     */
    public boolean deleteUser(String username)
    {
        try
        {
            User user = getUser(username);

            if (user != null)
            {
                userRepo.deleteById(username);
                return true;
            }

            return false;
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'boolean deleteUser(String username)' " + e);
            return false;
        }
    }

    /**
     * פעולה המעדכנת משתמש במסד הנתונים
     * @param username שם המשתמש
     * @param password סיסמת המשתמש
     * @param type סוג המשתמש
     * @param songs רשימת היסטוריית החיזויים של המשתמש
     * @return אמת במידה והפעולה הצליחה אחרת שקר
     */
    public boolean updateUser(String username, String password, Type type, List<Song> songs)
    {
        try
        {
            User user = new User(username, password, type, songs);
            userRepo.save(user);
            return true;
        } 
        catch (Exception e)
        {
            System.out.println("ERROR from 'void updateUser(String username, String password, List<Song> songs)' " + e);
            return false;
        }
        
       
    }
    
    /**
     * פעולה המוסיפה שיר להיסטוריית החיזויים של המשתמש
     * @param username שם המשתמש
     * @param song השיר להוספה
     */
    public void addSongToHistoryUser(String username, Song song)
    {
        try
        {
            User user = getUser(username);

            if (user != null)
            {
                user.addSong(song);
                userRepo.save(user);
            }
            
        } catch (Exception e)
        {
            System.out.println("ERROR from 'void addSongToHistoryUser(String username, Song song)' " + e);
        }
    }
    
    
    /**
     * פעולה המוחקת את רשימת היסטוריית החיזויים של המשתמ
     * @param username שם המשתמש
     */
    public void deleteHistoryUser(String username)
    {
         try
        {
            User user = getUser(username);

            if (user != null)
            {
                user.setSongs(new ArrayList<>());
                userRepo.save(user);
            }
            
        } catch (Exception e)
        {
            System.out.println("ERROR from 'void addSongToHistoryUser(String username, Song song)' " + e);
        }
    }

    /**
     * פעולה המוצאת משתמשים על ידי תחילת שמם
     * @param startletter מחזורת תחילת השם
     * @return רשימת המשתמשים שנמצאו
     */
    public List<User> findByUsernameStartsWith(String startletter)
    {
        return userRepo.findByUsernameStartsWith(startletter);
    }
}
