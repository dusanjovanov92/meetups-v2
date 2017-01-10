package com.dusanjovanov.meetups3.models;

import java.io.Serializable;

/**
 * Created by duca on 10/1/2017.
 */

public class Meeting implements Serializable{

    private int id;
    private int idGroup;
    private String label;
    private long startTime;
    private String firebaseNode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getFirebaseNode() {
        return firebaseNode;
    }

    public void setFirebaseNode(String firebaseNode) {
        this.firebaseNode = firebaseNode;
    }
}
