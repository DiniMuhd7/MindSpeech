package com.deenysoft.mindspeech.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shamsadam on 30/08/16.
 */
public class MindSpeechDatabase extends SQLiteOpenHelper {

    private final Resources mResources;

    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "mindspeech.db";

    public MindSpeechDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mResources = context.getResources();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating Tables
        db.execSQL("Create table if not exists " + MindSpeechDBTable.KEYNOTE_TABLE +
                "(" + MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG + " TEXT," +
                MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY + " TEXT," +
                MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_DATE + " DATE," +
                "UNIQUE ("+MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_ID+
                ")"+");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MindSpeechDBTable.KEYNOTE_TABLE);
        onCreate(db);

    }
}
