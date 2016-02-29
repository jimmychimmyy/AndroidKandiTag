package com.plur.kanditag;

/**
 * Created by Jim on 2/27/16.
 */
public class Event {

    private String name;
    private String date;

    public Event() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }
}
