package com.plur.kanditag;

/**
 * Created by Jim on 2/27/16.
 */
public class Person {

    private String firstName, lastName;
    private String _ktid, _uid;
    private boolean hasConnected;

    public Person() {}

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setKtId(String _id) {
        this._ktid = _id;
    }

    public void setUId(String _id) {
        this._uid = _id;
    }

    public void setHasConnected(boolean connected) {
        this.hasConnected = connected;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getKtId() {
        return this._ktid;
    }

    public String getUId() {
        return this._uid;
    }

    public boolean getHasConnected() {
        return this.hasConnected;
    }
}
