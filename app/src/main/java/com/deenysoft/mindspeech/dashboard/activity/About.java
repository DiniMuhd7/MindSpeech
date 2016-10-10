package com.deenysoft.mindspeech.dashboard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.account.GoogleAuthLoginActivity;
import com.deenysoft.mindspeech.base.BaseActivity;
import com.deenysoft.mindspeech.dashboard.DashboardActivity;
import com.deenysoft.mindspeech.widget.AvatarView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by shamsadam on 05/10/2016.
 */
public class About extends BaseActivity {

    private String deenURL = "https://twitter.com/deenadem";
    private String deenGithub = "https://github.com/Deen-Adam";
    private Button mButton1, mButton2, mButton3;
    private FirebaseAuth mAuth;
    private ImageView mImageShare;
    private ImageView mImageRate;

    private NativeExpressAdView mNativeAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        setupToolbar();
        mAuth = FirebaseAuth.getInstance();

        mNativeAdView = (NativeExpressAdView) findViewById(R.id.adViewNative);
        AdsAlertDialog();

        mButton1 = (Button) findViewById(R.id.twitterButton);;
        mButton2 = (Button) findViewById(R.id.deenGithubButton);
        mButton3 = (Button) findViewById(R.id.freepikButton);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.twitterButton:
                        // Do Something
                        Intent mTwitter = new Intent(Intent.ACTION_VIEW);
                        mTwitter.setData(Uri.parse(deenURL));
                        startActivity(mTwitter);
                        break;
                    default:
                        throw new UnsupportedOperationException("The onClick method has not be implemented");
                }
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.deenGithubButton:
                        // Do Something
                        Intent mGithub = new Intent(Intent.ACTION_VIEW);
                        mGithub.setData(Uri.parse(deenGithub));
                        startActivity(mGithub);
                        break;
                    default:
                        throw new UnsupportedOperationException("The onClick method has not be implemented");
                }
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.freepikButton:
                        // Do Something
                        String FreepikURL = "http://www.freepik.com/";
                        Intent mFreePikIntent = new Intent(Intent.ACTION_VIEW);
                        mFreePikIntent.setData(Uri.parse(FreepikURL));
                        startActivity(mFreePikIntent);
                        break;
                    default:
                        throw new UnsupportedOperationException("The onClick method has not be implemented");
                }
            }
        });


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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(About.this);
                mBuilder.setTitle("Rate MindSpeech");
                mBuilder.setPositiveButton(R.string.okey, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mPlayStore = "https://play.google.com/store/apps/details?id=com.deenysoft.mindspeech";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mPlayStore));
                        startActivity(intent);
                    }
                });

                AlertDialog mAlert = mBuilder.create();
                mAlert.show();

            }
        });


        final AvatarView avatarView = (AvatarView) findViewById(R.id.avatar);
        final AvatarView avatarView2 = (AvatarView) findViewById(R.id.avatar2);
        avatarView.setAvatar(R.drawable.avatar1);
        avatarView2.setAvatar(R.drawable.mindspeech);

    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_element);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    // Ads Dialog
    public void AdsAlertDialog(){
        // Prepare Dialog View and Grid View
        View mView = getLayoutInflater().inflate(R.layout.ads_dialog_about, null);
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

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
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
}
