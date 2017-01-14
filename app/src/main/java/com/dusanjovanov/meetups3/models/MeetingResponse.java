package com.dusanjovanov.meetups3.models;

/**
 * Created by duca on 14/1/2017.
 */

public class MeetingResponse {

    private int id;
    private int idMeeting;
    private User user;
    private int response;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMeeting() {
        return idMeeting;
    }

    public void setIdMeeting(int idMeeting) {
        this.idMeeting = idMeeting;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

}
