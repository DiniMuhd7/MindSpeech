package com.deenysoft.mindspeech.dashboard.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.account.GoogleAuthLoginActivity;
import com.deenysoft.mindspeech.base.BaseActivity;
import com.deenysoft.mindspeech.dashboard.DashboardActivity;
import com.deenysoft.mindspeech.dashboard.adapter.KeyNoteAdapter;
import com.deenysoft.mindspeech.dashboard.model.KeyNoteItem;
import com.deenysoft.mindspeech.database.MindSpeechDBManager;
import com.deenysoft.mindspeech.database.MindSpeechDBTable;
import com.deenysoft.mindspeech.database.MindSpeechDatabase;
import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

/**
 * Created by shamsadam on 30/08/16.
 */
public class KeyNotePlayer extends BaseActivity {

    private ImageView mPreviousButton;
    private ImageView mNextButton;
    private ImageView mPlayButton;
    private ImageView mPauseButton;
    private ImageView mClockIcon;
    private Chronometer mChronometer;
    private SeekBar mSeekBar;

    private TextToSpeech mindSpeech;
    private TextView mTextView;

    private MindSpeechDBManager mMindSpeechDBManager;
    private MindSpeechDatabase mMindSpeechDatabase;
    private SQLiteDatabase mSQLiteDatabase;
    private KeyNoteItem mKeyNoteItem;
    private FirebaseAuth mAuth;

    private ImageView mImageShare;
    private ImageView mImageRate;

    private NativeExpressAdView mNativeAdView;

    // Activity Intent Starter
    public static Intent getStartIntent(Context context, KeyNoteItem mKeyNoteItem) {
        Intent starter = new Intent(context, KeyNotePlayer.class);
        starter.putExtra(KeyNoteItem.TAG, mKeyNoteItem.getKeyNoteTag());
        return starter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keynote_player);
        setupToolbar();

        mAuth = FirebaseAuth.getInstance();

        // Get the unique KeyNoteCategoryID from Intent
        final String keyNoteCategoryID = getIntent().getStringExtra(KeyNoteItem.TAG);

        mPreviousButton = (ImageView) findViewById(R.id.img_previous);
        mNextButton = (ImageView) findViewById(R.id.img_next);
        mPlayButton = (ImageView) findViewById(R.id.img_play);
        mPauseButton = (ImageView) findViewById(R.id.img_pause);
        mClockIcon = (ImageView) findViewById(R.id.img_clock);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mTextView = (TextView) findViewById(R.id.txtScript);
        mSeekBar = (SeekBar) findViewById(R.id.speech_seek);

        mKeyNoteItem = new KeyNoteItem();
        mMindSpeechDBManager = new MindSpeechDBManager(getBaseContext());
        // MindSpeechDatabase is a SQLiteOpenHelper class connecting to SQLite
        mMindSpeechDatabase = new MindSpeechDatabase(this);
        // Get access to the underlying writable database
        mSQLiteDatabase = mMindSpeechDatabase.getWritableDatabase();
        /* Dumped
        String tableName = "keynote_table";
        String[] columnName = new String[] {"_id", "keynote_tag", "keynote_body", "keynote_date"};
        String whereClause = "keynote_tag=?"+mMindSpeechDBManager.getKeyNoteTag();
        Cursor mCursor = mSQLiteDatabase.query(tableName, columnName, whereClause, null,null,null,null);
        */

        mImageShare = (ImageView) findViewById(R.id.share);
        mImageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Add data to the intent, the receiving app will decide what to do with it.
                intent.putExtra(Intent.EXTRA_SUBJECT, "Get MindSpeech App for Android");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, Try MindSpeech App for Android. It provides an advanced form of telepathic communication that enables you to communicate from mind to mind. Get it on Google Play - https://play.google.com/store/apps/developer?id=Deenysoft+Inc");

                startActivity(Intent.createChooser(intent, "How do you want to share?"));
                finish();
            }
        });


        mImageRate = (ImageView) findViewById(R.id.rate);
        mImageRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(KeyNotePlayer.this);
                mBuilder.setTitle("Rate MindSpeech");
                mBuilder.setPositiveButton(R.string.okey, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mPlayStore = "https://play.google.com/store/apps/details?id=com.deenysoft.mindspeech";
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mPlayStore));
                        startActivity(intent);
                    }
                });

                AlertDialog mAlert = mBuilder.create();
                mAlert.show();

            }
        });

        mNativeAdView = (NativeExpressAdView) findViewById(R.id.adViewNative);
        AdsAlertDialog();


        if (null == keyNoteCategoryID) {
            // Do Nothing
            finish();
        }
        // Query for items from the database and get a cursor back
        Cursor mCursor = mSQLiteDatabase.rawQuery("SELECT keynote_body FROM keynote_table WHERE keynote_tag ='"+keyNoteCategoryID+"'", null); // What a crazy combination i got here!!
        if (mCursor != null ) {
            if  (mCursor.moveToFirst()) {
                do {
                    String KeyNoteBody = mCursor.getString(mCursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                    mKeyNoteItem.setKeyNoteBody(KeyNoteBody);
                } while (mCursor.moveToNext());
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }

        // Clone the retrieved Script
        final String mScript = mKeyNoteItem.getKeyNoteBody();
        // Write script to text view
        mTextView.setText(mScript);

        // Initialize MindSpeech
        mindSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mindSpeech.setLanguage(Locale.UK);
                }
            }
        });

        // Setting up OnClickListeners
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == keyNoteCategoryID) {
                    // Do Nothing
                    finish();
                }
                // Query for items from the database and get a cursor back
                Cursor mCursor = mSQLiteDatabase.rawQuery("SELECT keynote_body FROM keynote_table WHERE keynote_tag IS NOT NULL", null); // What a crazy combination i got here!!
                if (mCursor != null ) {
                    if  (mCursor.moveToFirst()) {
                        do {
                            String KeyNoteBody = mCursor.getString(mCursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                            mKeyNoteItem.setKeyNoteBody(KeyNoteBody);
                        } while (mCursor.moveToNext());
                    }
                }
                if (mCursor != null) {
                    mCursor.close();
                }

                // Clone the retrieved Script
                final String mScript = mKeyNoteItem.getKeyNoteBody();
                // Write script to text view
                mTextView.setText(mScript);
            }

        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == keyNoteCategoryID) {
                    // Do Nothing
                    finish();
                }
                // Query for items from the database and get a cursor back
                Cursor mCursor = mSQLiteDatabase.rawQuery("SELECT keynote_body FROM keynote_table WHERE keynote_tag IS NOT NULL", null); // What a crazy combination i got here!!
                if (mCursor != null ) {
                    if  (mCursor.moveToFirst()) {
                        do {
                            String KeyNoteBody = mCursor.getString(mCursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                            mKeyNoteItem.setKeyNoteBody(KeyNoteBody);
                        } while (mCursor.moveToNext());
                    }
                }
                if (mCursor != null) {
                    mCursor.close();
                }

                // Clone the retrieved Script
                final String mScript = mKeyNoteItem.getKeyNoteBody();
                // Write script to text view
                mTextView.setText(mScript);
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide & Show
                mPlayButton.setVisibility(View.GONE);
                mPauseButton.setVisibility(View.VISIBLE);
                mChronometer.start();
                Toast.makeText(KeyNotePlayer.this, "Follow Speech Now", Toast.LENGTH_SHORT).show();
                // Launch a Thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Prepare to speak
                        String toSpeak = mScript;
                        mindSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }).start();
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide & Show
                mPauseButton.setVisibility(View.GONE);
                mPlayButton.setVisibility(View.VISIBLE);
                mChronometer.stop();
                // Stop Speech
                mindSpeech.stop();
            }
        });

        mClockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.stop();
                // Stop Speech
                mindSpeech.stop();
                mPauseButton.setVisibility(View.GONE);
                mPlayButton.setVisibility(View.VISIBLE);
                //Toast.makeText(KeyNotePlayer.this, "00:00", Toast.LENGTH_SHORT).show();
            }
        });


        // Setting up SeekBar
        mSeekBar.setMax(9);
        mSeekBar.setProgress(6);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress = progress + 1; // Add the minimum value (1)
                if (progress <= 5) {
                    mindSpeech.setSpeechRate((float) 0.5);
                } else {
                    mindSpeech.setSpeechRate((float) 0.8);
                }
                Toast.makeText(KeyNotePlayer.this, ""+progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    // Ads Dialog
    public void AdsAlertDialog(){
        // Prepare Dialog View and Grid View
        View mView = getLayoutInflater().inflate(R.layout.ads_dialog_keynote_player, null);
        mNativeAdView = (NativeExpressAdView) mView.findViewById(R.id.adViewNative);
        mNativeAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        // Set view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);
        //builder.setTitle("ADD");
        builder.show();

    }


    @Override
    public void onPause() {
        if (mNativeAdView != null) {
            mNativeAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mNativeAdView != null) {
            mNativeAdView.resume();
        }
    }

    @Override
    protected void onStop() {
        if (mindSpeech != null){
            mindSpeech.stop();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        if (mindSpeech != null){
            mindSpeech.stop();
        }
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        if (mindSpeech != null){
            mindSpeech.stop();
            mindSpeech.shutdown();
        }
        if (mNativeAdView != null) {
            mNativeAdView.destroy();
        }
        super.onDestroy();
    }

    private void setupToolbar() {
        // Get the unique KeyNoteCategoryID from Intent
        final String keyNoteCategoryID = getIntent().getStringExtra(KeyNoteItem.TAG);
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_element);
        ab.setTitle(keyNoteCategoryID); // Update ActionBar
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.sign_out:
                //startActivity(new Intent(this, SettingsActivity.class)); Do Something
                mAuth.signOut();
                Intent mSignOut = new Intent(this, GoogleAuthLoginActivity.class);
                startActivity(mSignOut);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean providesActivityToolbar() {
        return false;
    }

}
