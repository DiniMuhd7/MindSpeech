package com.deenysoft.mindspeech.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import static com.deenysoft.mindspeech.util.LogUtil.makeLogTag;

import com.deenysoft.mindspeech.R;
import com.google.android.gms.common.ConnectionResult;

/**
 * Created by shamsadam on 29/08/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

        private static final String TAG = makeLogTag(BaseActivity.class);
        private Toolbar actionBarToolbar;

        @Override
        protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);

        }

        private ProgressDialog mProgressDialog;

        public void showProgressDialog() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Loading...");
            }

            mProgressDialog.show();
        }


        public void hideProgressDialog() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }


        /*
        public String getUid() {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        */


        /**
         * Provides the action bar instance.
         * @return the action bar.
         */
        protected ActionBar getActionBarToolbar() {
            if (actionBarToolbar == null) {
                actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
                if (actionBarToolbar != null) {
                    setSupportActionBar(actionBarToolbar);
                }
            }
            return getSupportActionBar();
        }


    public abstract boolean providesActivityToolbar();


        public void setToolbar(Toolbar toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


}
