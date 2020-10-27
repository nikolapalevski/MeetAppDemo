package com.example.bogstrocieventdemo;


public class Note {
    private String location;
    private String time;
    private String date;
    private String sender;
    private int priority;

    public Note(){
        //empty constractor
    }


    public Note(String location, String time, String date, String sender, int priority) {
        this.location = location;
        this.time = time;
        this.date = date;
        this.sender = sender;
        this.priority = priority;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
    public String getSender() {
        return sender;
    }

    public int getPriority() {
        return priority;
    }
}
