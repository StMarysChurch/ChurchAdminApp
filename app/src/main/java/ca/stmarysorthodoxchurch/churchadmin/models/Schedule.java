package ca.stmarysorthodoxchurch.churchadmin.models;

import java.util.ArrayList;

/**
 * Created by roneythomas on 2016-10-04.
 */

public class Schedule {

    String title;
    long expiryDate;
    ArrayList<String> events = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();

    public Schedule() {

    }

    public Schedule(String title, long expiryDate, ArrayList<String> events, ArrayList<String> times) {
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

    public String getExpiryDate() {
        return String.valueOf(expiryDate);
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = Long.valueOf(expiryDate);
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
