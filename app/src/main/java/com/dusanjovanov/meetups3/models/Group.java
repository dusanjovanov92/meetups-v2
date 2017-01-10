package com.dusanjovanov.meetups3.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duca on 29/12/2016.
 */

public class Group implements Serializable{

    private int id;
    private String name;
    private User admin;
    private int memberCount;
    private Meeting nextMeeting;
    private int currentMeetingCount;
    private List<User> members = new ArrayList<>();
    private List<Meeting> meetings = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Meeting getNextMeeting() {
        return nextMeeting;
    }

    public void setNextMeeting(Meeting nextMeeting) {
        this.nextMeeting = nextMeeting;
    }

    public int getCurrentMeetingCount() {
        return currentMeetingCount;
    }

    public void setCurrentMeetingCount(int currentMeetingCount) {
        this.currentMeetingCount = currentMeetingCount;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }
}
