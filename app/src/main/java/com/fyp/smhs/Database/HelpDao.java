package com.fyp.smhs.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HelpDao {
    /**
     * A Data Access Object (Dao) is the bridge between the
     * user attempting to interact with the lower-level
     * database and the raw database. The access object
     * allows you to perform operations, retrieve data etc.
     */
    @Query("SELECT * FROM resources")
    List<Help> getAll();

    @Insert
    void insert(Help help);

    @Insert
    void insertAll(Help... resources);

    @Delete
    void delete(Help resource);
}