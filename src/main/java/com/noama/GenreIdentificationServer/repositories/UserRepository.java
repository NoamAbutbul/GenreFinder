package com.noama.GenreIdentificationServer.repositories;

import com.noama.GenreIdentificationServer.model.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  UserRepository. ממשק לפעולות על המשתמשים במונגו
 *  By noamabutbul | 08/02/2023 02:58
 */

@Repository
public interface UserRepository extends MongoRepository<User, String>
{
    /**
     * פעולה המוצאת משתמש על ידי תחילת שם המשתמש
     * @param name מחזורת השם או חלק ממנו
     * @return רשימת המשתמשים שנמצאו תחת אותו שם
     */
    public List<User> findByUsernameStartsWith(String name);
}
