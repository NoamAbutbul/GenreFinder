package com.noama.GenreIdentificationServer.repositories;

import com.noama.GenreIdentificationServer.model.Song;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *  SongRepository. ממשק לפעולות על השירים במונגו
 *  By noamabutbul | 20/02/2023 16:14
 */

@Repository
public interface SongRepository extends MongoRepository<Song, String>
{
    /**
     * פעולה המוצאת שיר על ידי תחילת שם השיר
     * @param name מחרוזת השם או חלק ממנו
     * @return רשימת השירים שנמצאו תחת אותו שם
     */
    public List<Song> findByNameStartsWith(String name);
}
