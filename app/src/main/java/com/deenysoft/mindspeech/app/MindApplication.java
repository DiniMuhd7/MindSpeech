package com.deenysoft.mindspeech.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.deenysoft.mindspeech.database.MindSpeechDBManager;
import com.deenysoft.mindspeech.util.MindPreferenceManager;

/**
 * Created by shamsadam on 30/08/16.
 */
public class MindApplication extends Application {

    private static Context mContext;
    private SharedPreferences sharedPreferences;

    // AppController variables declaration
    private static MindApplication mInstance;

    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        mInstance = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MindPreferenceManager.initializePreferenceManager(sharedPreferences);

    }

    public static Context getContext() {
        return mContext;
    }

    public static synchronized MindApplication getInstance() {
        return mInstance;
    }


}
