package com.deenysoft.mindspeech.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.deenysoft.mindspeech.dashboard.DashboardActivity;

/**
 * Created by shamsadam on 31/08/16.
 */
public class SplashScreen extends AppCompatActivity {

    private static String TAG = SplashScreen.class.getName();
    private static long SLEEP_TIME = 5;    // Sleep for some time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Removes title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash_screen); // Doesn't Required

        // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         * Sleep for some time and than start new activity.
         */
        public void run() {
            try {
                // Sleeping
                Thread.sleep(SLEEP_TIME*1000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            // Start main activity
            Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
            SplashScreen.this.startActivity(intent);
            SplashScreen.this.finish();
        }
    }


}
