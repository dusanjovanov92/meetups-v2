package com.dusanjovanov.meetups3.models;

import java.io.Serializable;

/**
 * Created by duca on 5/1/2017.
 */

public class Contact implements Serializable{
    private User user;
    private String firebaseNode;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirebaseNode() {
        return firebaseNode;
    }

    public void setFirebaseNode(String firebaseNode) {
        this.firebaseNode = firebaseNode;
    }
}
