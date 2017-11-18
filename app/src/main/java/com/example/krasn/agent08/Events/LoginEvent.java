package com.example.krasn.agent08.Events;

/**
 * Created by krasn on 10/14/2017.
 */

public class LoginEvent {
    boolean isLogedIn = false;
    public LoginEvent(boolean isLogedIn){
        this.isLogedIn = isLogedIn;
    }

    public boolean isLogedIn() {
        return isLogedIn;
    }

    public void setLogedIn(boolean logedIn) {
        isLogedIn = logedIn;
    }
}
