package com.deenysoft.mindspeech.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deenysoft.mindspeech.app.MindApplication;
import com.deenysoft.mindspeech.dashboard.model.KeyNoteItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shamsadam on 30/08/16.
 */
public class MindSpeechDBManager {

    private static SQLiteDatabase mSQLiteDatabase;
    private static MindSpeechDatabase mMindSpeechDatabase;
    private static MindSpeechDBManager mMindSpeechDBManagerInstance;


    public static MindSpeechDBManager getInstance(Context context) {
        if( mMindSpeechDBManagerInstance == null ) {
            synchronized (MindSpeechDBManager.class) {
                if (mMindSpeechDBManagerInstance == null) {
                    mMindSpeechDBManagerInstance = new MindSpeechDBManager(context);
                }
            }
        }
        return mMindSpeechDBManagerInstance;
    }


    public MindSpeechDBManager(Context context) {
        mMindSpeechDatabase = new MindSpeechDatabase(context);
        open();
    }

    public void open() throws android.database.SQLException {
        mSQLiteDatabase = mMindSpeechDatabase.getWritableDatabase();
    }

    public static void release() {
        close();
    }

    public static void close() {
        if( mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
        if(mMindSpeechDatabase != null) {
            mMindSpeechDatabase.close();
            mMindSpeechDatabase = null;
        }
    }


    // Add KeyNoteItem values into the sqlite database.
    public long addKeyNoteItem(KeyNoteItem mKeyNoteItem) throws android.database.SQLException {
        long insertId = 0;
        ContentValues values = new ContentValues();
        //values.put(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_ID, mKeyNoteItem.getKeyNoteID());
        values.put(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG, mKeyNoteItem.getKeyNoteTag());
        values.put(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY, mKeyNoteItem.getKeyNoteBody());
        try {
            insertId = mSQLiteDatabase.insertOrThrow(MindSpeechDBTable.KEYNOTE_TABLE, null,values);
        } catch (android.database.SQLException ex) {
            throw ex;
        }
        return insertId;
    }


    // Retrieve KeyNoteItem values from the sqlite database.
    public List<KeyNoteItem> getKeyNoteItem() {
        Cursor cursor = mSQLiteDatabase.query(MindSpeechDBTable.KEYNOTE_TABLE,
                null, null, null, null, null, MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG + " DESC");

        List<KeyNoteItem> mKeyNoteItemList = new ArrayList<KeyNoteItem>();

        if (cursor != null ) {
            if  (cursor.moveToFirst()) {
                do {
                    KeyNoteItem mKeyNoteItem = new KeyNoteItem();
                    //int KeyNoteID = cursor.getInt(cursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_ID));
                    String KeyNoteTag = cursor.getString(cursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG));
                    String KeyNoteBody = cursor.getString(cursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));

                    //mKeyNoteItem.setKeyNoteID(KeyNoteID);
                    mKeyNoteItem.setKeyNoteTag(KeyNoteTag);
                    mKeyNoteItem.setKeyNoteBody(KeyNoteBody);
                    mKeyNoteItemList.add(mKeyNoteItem);
                } while (cursor.moveToNext());
            }
        }

        if(cursor != null) {
            cursor.close();
        }

        return mKeyNoteItemList;

    }


    // Get KeyNote Tag
    public String getKeyNoteTag(){
        Cursor cursor = mSQLiteDatabase.query(MindSpeechDBTable.KEYNOTE_TABLE,
                null, null, null, null, null, MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG+" DESC ");

        String KeyNoteTag = "";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    KeyNoteTag = cursor.getString(cursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG));
                }while (cursor.moveToNext());
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return KeyNoteTag;
    }


    // Get KeyNote Body
    public String getKeyNoteBody(){
        Cursor cursor = mSQLiteDatabase.query(MindSpeechDBTable.KEYNOTE_TABLE,
                null, null, null, null, null, MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY+" DESC ");

        String KeyNoteBody = "";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    KeyNoteBody = cursor.getString(cursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                }while (cursor.moveToNext());
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return KeyNoteBody;
    }



}
