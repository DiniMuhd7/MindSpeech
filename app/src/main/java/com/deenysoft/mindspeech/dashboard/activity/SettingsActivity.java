package com.deenysoft.mindspeech.dashboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.account.GoogleAuthLoginActivity;
import com.deenysoft.mindspeech.base.BaseActivity;
import com.deenysoft.mindspeech.dashboard.DashboardActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by shamsadam on 09/09/16.
 */
public class SettingsActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private ImageView mImageShare;
    private ImageView mImageRate;

    private NativeExpressAdView mNativeAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setupToolbar();
        mAuth = FirebaseAuth.getInstance();

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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
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


    }


    // Ads Dialog
    public void AdsAlertDialog(){
        // Prepare Dialog View and Grid View
        View mView = getLayoutInflater().inflate(R.layout.ads_dialog_settings, null);
        mNativeAdView = (NativeExpressAdView) mView.findViewById(R.id.adViewNative);
        mNativeAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        // Set view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);
        //builder.setTitle("ADD");
        builder.show();

    }


    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_element);
        ab.setDisplayHomeAsUpEnabled(true);
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
    protected void onDestroy() {
        if (mNativeAdView != null) {
            mNativeAdView.destroy();
        }
        super.onDestroy();
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


    // Anonymous Class FragmentSettings Preference Fragment
    public static class FragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_pref);

            final ListPreference mListTransFrom = (ListPreference) getPreferenceManager().findPreference("translateFrom");
            final ListPreference mListTransTo = (ListPreference) getPreferenceManager().findPreference("translateTo");

            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final String mListTranslateFrom = mSharedPreferences.getString("translateFrom", "en");
            final String mListTranslateTo = mSharedPreferences.getString("translateTo", "en");

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences mSharedPreferences, String key) {
            // Do Something
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String mPrefTranslateFrom = mSharedPreferences.getString("translateFrom", "en");
            if (mPrefTranslateFrom != null && mPrefTranslateFrom.equals("en")) {
                //  Make a new preferences editor
                SharedPreferences.Editor e = mSharedPreferences.edit();
                //  Edit preference to make it default
                e.putString("translateFrom", "en");
                //  Apply changes
                e.apply();
            }

            String mPrefTranslateTo = mSharedPreferences.getString("translateTo", "en");
            if (mPrefTranslateTo != null && mPrefTranslateTo.equals("en")) {
                //  Make a new preferences editor
                SharedPreferences.Editor e = mSharedPreferences.edit();
                //  Edit preference to make it default
                e.putString("translateTo", "en");
                //  Apply changes
                e.apply();
            }

            // Update List Summary UI
            Preference preference = findPreference(key);
            if (key.equals("translateFrom")) {
                preference.setSummary(((ListPreference) preference).getEntry());
                Toast.makeText(getActivity(), "Enabled "+mPrefTranslateFrom, Toast.LENGTH_SHORT).show();
            }

            if (key.equals("translateTo")) {
                preference.setSummary(((ListPreference) preference).getEntry());
                Toast.makeText(getActivity(), "Enabled "+mPrefTranslateTo, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStart() {
            super.onStart();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onStop() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onStop();
        }
    }

}
