package com.example.personaleventplanner.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.personaleventplanner.data.Event;
import com.example.personaleventplanner.data.EventDao;
import com.example.personaleventplanner.data.EventDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {

    private final EventDao eventDao;
    private final LiveData<List<Event>> upcomingEvents;
    private final ExecutorService executorService;

    public EventRepository(Application application) {
        EventDatabase database = EventDatabase.getInstance(application);
        eventDao = database.eventDao();
        upcomingEvents = eventDao.getUpcomingEvents(System.currentTimeMillis());
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Event>> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void insert(Event event) {
        executorService.execute(() -> eventDao.insert(event));
    }

    public void update(Event event) {
        executorService.execute(() -> eventDao.update(event));
    }

    public void delete(Event event) {
        executorService.execute(() -> eventDao.delete(event));
    }

    public void getEventById(int id, OnEventLoadedListener listener) {
        executorService.execute(() -> {
            Event event = eventDao.getEventById(id);
            listener.onLoaded(event);
        });
    }

    public interface OnEventLoadedListener {
        void onLoaded(Event event);
    }
}