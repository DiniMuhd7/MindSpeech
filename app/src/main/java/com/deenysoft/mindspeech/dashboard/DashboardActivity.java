package com.deenysoft.mindspeech.dashboard;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.account.GoogleAuthLoginActivity;
import com.deenysoft.mindspeech.base.BaseActivity;
import com.deenysoft.mindspeech.dashboard.activity.About;
import com.deenysoft.mindspeech.dashboard.activity.KeyNotePlayer;
import com.deenysoft.mindspeech.dashboard.activity.SettingsActivity;
import com.deenysoft.mindspeech.dashboard.fragment.KeyNoteFragment;
import com.deenysoft.mindspeech.dashboard.fragment.MindChatFragment;
import com.deenysoft.mindspeech.dashboard.model.KeyNoteItem;
import com.deenysoft.mindspeech.database.MindSpeechDBManager;
import com.deenysoft.mindspeech.database.MindSpeechDatabase;
import com.deenysoft.mindspeech.widget.TransitionHelper;
import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class DashboardActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MindSpeechDashboard";

    // Pager Adapter & ViewPager
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private static final String EXTRA_EDIT = "EDIT";
    private static final int RC_CONTACT_PERMS = 104;
    private static final int SPEECH_REQUEST_CODE = 0;
    private FirebaseAuth mAuth;
    private ImageView mImageShare;
    private ImageView mImageRate;

    private MindSpeechDatabase mMindSpeechDatabase;
    private SQLiteDatabase mSQLiteDatabase;
    private KeyNoteItem mKeyNoteItem;
    private static final int REQUEST_CATEGORY = 0x2300;

    private AdView mAdView;
    private NativeExpressAdView mNativeAdView;

    private static String mGuide = "MindSpeech is an advanced form of telepathic communicational application that enables you to communicate from mind to mind to speech.\n\nMindSpeech collects your keynote text and converts it into speech, it allows you to adjust the speech speed while following the system speech." +
            "This enables you to communicate without any glitch while keeping up head to head with the (converted speech) system speech.\n\nMindSpeech provides flexible options for you to pause, forward and reverse speech when interrupted by someone while giving a mind speech.\n\n" +
            "MindSpeech incorporates all techniques of your communication skills by allowing you to communicate with anyone, anytime, anywhere.\n\nIt enables you to send messages to your friends in any languages of your choice by providing an end to end translations.\n\n" +
            "Set up translation method for outgoing messages, write your message and let MindSpeech process it, translate it and deliver it to your recipient instantly with full supports for sending via other social apps. Translation has no limit, MindSpeech can send translated messages as much as you want extensively.\n\n" +
            "MindSpeech consistently allows you to work offline in all gadgets, permitting you to extremely use the application when disconnected from the internet.\n\n" +
            "MindSpeech. Communication Redefined.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setupToolbar();
        mAuth = FirebaseAuth.getInstance();

        // MindSpeechDatabase is a SQLiteOpenHelper class connecting to SQLite
        mMindSpeechDatabase = new MindSpeechDatabase(this);
        // Get access to the underlying writable database
        mSQLiteDatabase = mMindSpeechDatabase.getWritableDatabase();

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Launch app intro
                    Intent i = new Intent(DashboardActivity.this, GoogleAuthLoginActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();
                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                    //  Apply changes
                    e.apply();

                    // Query for insert record into the database and get a cursor back
                    Cursor mCursorInsert = mSQLiteDatabase.rawQuery("INSERT INTO keynote_table (_id, keynote_tag, keynote_body, keynote_date) VALUES " +
                            " (1, 'Guide', '"+mGuide+"','')", null); // What a crazy combination i got here!!
                    if (mCursorInsert != null ) {
                        if  (mCursorInsert.moveToFirst()) {
                            do {
                                //String KeyNoteBody = mCursorUpdate.getString(mCursorUpdate.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                            } while (mCursorInsert.moveToNext());
                        }
                    }
                    if (mCursorInsert != null) {
                        mCursorInsert.close();
                    }

                }

            }
        });

        // Start the thread
        t.start();


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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DashboardActivity.this);
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


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        // load and display ads
        mAdView.loadAd(adRequest);

        mNativeAdView = (NativeExpressAdView) findViewById(R.id.adViewNative);
        AdsAlertDialog();


        // Voice Search
        Intent intent = getIntent();
        if (SearchIntents.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchMindSpeech(query);
        }

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new KeyNoteFragment(),
                    new MindChatFragment()
            };
            private final String[] mFragmentNames = new String[] {
                    "KEYNOTE",
                    "CONTACT"
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(Color.parseColor("#000000"),Color.parseColor("#D50000"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF193A"));

/*
        // Create the School Fragment and add it as main container for the activity.
        FragmentManager mFragManagerKeynote = getSupportFragmentManager();
        if (mFragManagerKeynote.findFragmentById(android.R.id.content) == null) {
            KeyNoteFragment mKeyNoteFrag = new KeyNoteFragment();
            mKeyNoteFrag.beginTransaction().replace(R.id.container, mKeyNoteFrag).commit();
        }
*/


    }


    public void searchMindSpeech(String query) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_element);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(DashboardActivity.this, GoogleAuthLoginActivity.class));
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (mNativeAdView != null) {
            mNativeAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (mNativeAdView != null) {
            mNativeAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mNativeAdView != null) {
            mNativeAdView.destroy();
        }
        super.onDestroy();
    }


    // Ads Dialog
    public void AdsAlertDialog(){
        // Prepare Dialog View and Grid View
        View mView = getLayoutInflater().inflate(R.layout.ads_dialog_dashboard, null);
        mNativeAdView = (NativeExpressAdView) mView.findViewById(R.id.adViewNative);
        mNativeAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        // Set view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);
        //builder.setTitle("ADD");
        builder.show();

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

    // Request Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DashboardActivity.this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
