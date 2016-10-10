package com.deenysoft.mindspeech.dashboard.model;

/**
 * Created by shamsadam on 19/09/16.
 */
public class UserItem {

    public String username;
    public String email;

    public UserItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserItem(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
