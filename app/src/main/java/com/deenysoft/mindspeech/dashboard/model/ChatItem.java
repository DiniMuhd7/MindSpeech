package com.deenysoft.mindspeech.dashboard.model;

/**
 * Created by shamsadam on 19/09/16.
 */
public class ChatItem {

    public String uid;
    public String text;

    public ChatItem() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public ChatItem(String uid, String text) {
        this.uid = uid;
        this.text = text;
    }

}
