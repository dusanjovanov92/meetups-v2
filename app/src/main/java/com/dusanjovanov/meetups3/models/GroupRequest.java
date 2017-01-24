package com.dusanjovanov.meetups3.models;

/**
 * Created by duca on 15/1/2017.
 */

public class GroupRequest {
    private int id;
    private Group group;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}

