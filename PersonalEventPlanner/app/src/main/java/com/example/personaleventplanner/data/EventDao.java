package com.example.personaleventplanner.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events ORDER BY eventDateTime ASC")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM events WHERE eventDateTime >= :now ORDER BY eventDateTime ASC")
    LiveData<List<Event>> getUpcomingEvents(long now);

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    Event getEventById(int id);
}