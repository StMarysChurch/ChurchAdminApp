package ca.stmarysorthodoxchurch.churchadmin.models;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * Created by roneythomas on 2016-10-04.
 */

@Keep
public class Schedule {

    private String title;
    private Long expiryDate;
    private ArrayList<String> events = new ArrayList<>();
    private ArrayList<String> times = new ArrayList<>();

    public Schedule() {
    }

    public Schedule(String title, Long expiryDate, ArrayList<String> events, ArrayList<String> times) {
        this.title = title;
        this.expiryDate = expiryDate;
        this.events = events;
        this.times = times;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }
}
