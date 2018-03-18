package com.evento.akay18.evento;

/**
 * Created by akshaykumar on 08/01/18.
 */

public class EventDetails {
    private String title, organiser, description, date, time, location, phoneNum, uid;

    public EventDetails() {
    }

    public EventDetails(String uid, String title, String organiser, String description, String date, String time, String location, String phoneNum) {
        this.title = title;
        this.organiser = organiser;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
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


    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
