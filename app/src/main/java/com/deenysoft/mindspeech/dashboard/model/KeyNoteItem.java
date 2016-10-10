package com.deenysoft.mindspeech.dashboard.model;

/**
 * Created by shamsadam on 30/08/16.
 */
public class KeyNoteItem {

    public static final String TAG = "KeyNoteItem";

    String KeyNoteTag;
    String KeyNoteBody;


    public void setKeyNoteTag(String KeyNoteTag) {
        this.KeyNoteTag = KeyNoteTag;
    }

    public String getKeyNoteTag(){
        return KeyNoteTag;
    }

    public void setKeyNoteBody(String KeyNoteBody) {
        this.KeyNoteBody = KeyNoteBody;
    }

    public String getKeyNoteBody(){
        return KeyNoteBody;
    }



    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KeyNote Tag: "+KeyNoteTag);
        builder.append("\n");
        builder.append("KeyNote Body: "+KeyNoteBody);
        builder.append("\n\n");
        return builder.toString();
    }

}
