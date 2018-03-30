package com.evento.akay18.evento;

/**
 * Created by akshaykumar on 08/01/18.
 */

public class EventDetails {
    private String title;
    private String organiser;
    private String description;
    private String date;
    private String time;
    private String location;
    private String city;
    private String phoneNum;
    private String uid;

    public EventDetails() {
    }

    public EventDetails(String uid, String title, String organiser, String description, String date, String time, String location, String city, String phoneNum) {
        this.title = title;
        this.organiser = organiser;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.city = city;
        this.phoneNum = phoneNum;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getCity() { return city; }

    public String getPhoneNum() {
        return phoneNum;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCity(String city) { this.city = city; }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
