package com.deenysoft.mindspeech.dashboard.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.dashboard.DashboardActivity;
import com.deenysoft.mindspeech.dashboard.activity.KeyNotePlayer;
import com.deenysoft.mindspeech.dashboard.adapter.KeyNoteAdapter;
import com.deenysoft.mindspeech.dashboard.model.KeyNoteItem;
import com.deenysoft.mindspeech.database.MindSpeechDBManager;
import com.deenysoft.mindspeech.database.MindSpeechDBTable;
import com.deenysoft.mindspeech.database.MindSpeechDatabase;
import com.deenysoft.mindspeech.widget.OffsetDecoration;
import com.deenysoft.mindspeech.widget.TransitionHelper;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by shamsadam on 29/08/16.
 */
public class KeyNoteFragment extends Fragment {

    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mVoceFActionBUtton;
    private TextInputEditText mEditTextTag;
    private TextInputEditText mEditTextScript;

    private KeyNoteItem mKeyNoteItem;
    private MindSpeechDBManager mMindSpeechDBManager;
    private MindSpeechDatabase mMindSpeechDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    private static final int RC_CONTACT_PERMS = 100;
    private static final int SPEECH_REQUEST_CODE = 0;

    // Adapter
    private KeyNoteAdapter mKeyNoteAdapter;
    private static final int REQUEST_CATEGORY = 0x2300;
    private static final int RC_AUDIO_PERMS = 50;


    @AfterPermissionGranted(RC_CONTACT_PERMS)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_keynote, container, false);

        // Submit Link Button Init
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        rootView.findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View mView = inflater.inflate(R.layout.add_keynote_dialog, null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setView(mView);
                mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing
                    }
                });

                mBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Edit Texts Init
                        mEditTextTag = (TextInputEditText) mView.findViewById(R.id.input_keynote_tag);
                        mEditTextScript = (TextInputEditText) mView.findViewById(R.id.input_keynote_script);
                        // Call method to submit and store data
                        pushToStorage(getActivity());
                        // Refresh Activity
                        Intent intent = new Intent(getActivity(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                AlertDialog mAlert = mBuilder.create();
                mAlert.show();


            }

        });


        mVoceFActionBUtton = (FloatingActionButton) rootView.findViewById(R.id.fab_voice);
        rootView.findViewById(R.id.fab_voice).setOnClickListener(new View.OnClickListener() {
            @AfterPermissionGranted(RC_AUDIO_PERMS)
            @Override
            public void onClick(View view) {
                // Check that we have permission to record voice.
                String perm = Manifest.permission.RECORD_AUDIO;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !EasyPermissions.hasPermissions(getActivity(), perm)) {
                    EasyPermissions.requestPermissions(getActivity(), getString(R.string.permission_voice),
                            RC_AUDIO_PERMS, perm);
                    return;
                }
                // Call speech recognizer to retrieve speech
                displaySpeechRecognizer();
            }
        });

        return rootView;

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpDashboardGrid((RecyclerView) view.findViewById(R.id.keynote_recycler)); // & merge recycler id to view object .. Called
        super.onViewCreated(view, savedInstanceState);
    }


    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            // Do something with spokenText
            String spokenText = results.get(0);
            Toast.makeText(getActivity(), ""+spokenText, Toast.LENGTH_SHORT).show();
            //Intent startIntent = KeyNotePlayer.getStartIntent(getActivity(), mKeyNoteItem);
            //startActivity(startIntent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Create an intent that can start the Speech Recognizer activity
    public void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    public void pushToStorage(Context context) {
        // Objects Init
        mKeyNoteItem = new KeyNoteItem();
        mMindSpeechDBManager = new MindSpeechDBManager(context);

        // Get data from user's input
        final String mKeynoteTag = mEditTextTag.getText().toString().trim();
        final String mKeynoteScript = mEditTextScript.getText().toString().trim();

        // Title is required
        if (TextUtils.isEmpty(mKeynoteTag)) {
            mEditTextTag.setError("REQUIRED");
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(mKeynoteScript)) {
            mEditTextScript.setError("REQUIRED");
            return;
        }

        if (!mKeynoteTag.isEmpty() && !mKeynoteScript.isEmpty()) {
            // Pass to KeyNoteItem Class
            mKeyNoteItem.setKeyNoteTag(mKeynoteTag);
            mKeyNoteItem.setKeyNoteBody(mKeynoteScript);

            // Push to SchoolBoxDBManager SQLite Database
            mMindSpeechDBManager.addKeyNoteItem(mKeyNoteItem); // Stored
            Toast.makeText(getActivity(), "Added", Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "" + mMindSpeechDBManager.getKeyNoteItem(), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getActivity(), "Text box should not be left blank", Toast.LENGTH_SHORT).show();
        }
    }


    // Setting up recycler view with Mindspeech Item Adapter
    private void setUpDashboardGrid(final RecyclerView mRecyclerGridView) {
        final int spacing = getActivity().getResources()
                .getDimensionPixelSize(R.dimen.spacing_nano);
        mRecyclerGridView.addItemDecoration(new OffsetDecoration(spacing));
        // MindSpeech Item Adapter
        mKeyNoteAdapter = new KeyNoteAdapter(getActivity());
        mKeyNoteAdapter.setOnItemClickListener(new KeyNoteAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Activity activity = getActivity();
                startQuizActivityWithTransition(activity,
                        view.findViewById(R.id.keynoteTitle),
                        mKeyNoteAdapter.getItem(position));

            }
        });
        mKeyNoteAdapter.setOnItemLongClickListener(new KeyNoteAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position) {
                //Toast.makeText(getActivity(), "It works", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Launch second alert dialog
                        TagAlertDialog();
                    }
                });

                mBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Launch second alert dialog
                        TagAlertDialog();
                    }
                });

                AlertDialog mAlert = mBuilder.create();
                mAlert.show();
                return true;
            }
        });
        mRecyclerGridView.setAdapter(mKeyNoteAdapter); // Bind the adapter

    }


    public void TagAlertDialog(){
        // MindSpeechDatabase is a SQLiteOpenHelper class connecting to SQLite
        mMindSpeechDatabase = new MindSpeechDatabase(getActivity());
        // Get access to the underlying writable database
        mSQLiteDatabase = mMindSpeechDatabase.getWritableDatabase();
        final LayoutInflater mInflater = getActivity().getLayoutInflater();
        final View mViewTag = mInflater.inflate(R.layout.tag_alert_dialog, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setView(mViewTag);
        mBuilder.setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Update Keynote
                TextInputEditText mEditText = (TextInputEditText) mViewTag.findViewById(R.id.input_keynote_tag);
                final String mKeynoteTag = mEditText.getText().toString().trim();
                if (!mKeynoteTag.isEmpty()) {
                    // Query for items from the database and get a cursor back
                    Cursor mCursor = mSQLiteDatabase.rawQuery("SELECT keynote_tag FROM keynote_table WHERE keynote_tag ='" + mKeynoteTag + "'", null); // What a crazy combination i got here!!
                    if (mCursor != null) {
                        if (mCursor.moveToFirst()) {
                            do {
                                String KeyNoteTag = mCursor.getString(mCursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG));
                                if (!mKeynoteTag.equalsIgnoreCase(KeyNoteTag)) {
                                    Toast.makeText(getActivity(), "Invalid Keynote Tag", Toast.LENGTH_SHORT).show();
                                } else {
                                    final View mViewUpdate = mInflater.inflate(R.layout.update_alert_dialog, null);
                                    final TextInputEditText mTagEditText = (TextInputEditText) mViewUpdate.findViewById(R.id.input_keynote_tag);
                                    final TextInputEditText mScriptEditText = (TextInputEditText) mViewUpdate.findViewById(R.id.input_keynote_script);
                                    // Query for items from the database and get a cursor back
                                    mCursor = mSQLiteDatabase.rawQuery("SELECT keynote_body FROM keynote_table WHERE keynote_tag ='" + mKeynoteTag + "'", null); // What a crazy combination i got here!!
                                    if (mCursor != null ) {
                                        if  (mCursor.moveToFirst()) {
                                            do {
                                                String KeyNoteBody = mCursor.getString(mCursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                                                mTagEditText.setText(KeyNoteTag);
                                                mScriptEditText.setText(KeyNoteBody);
                                            } while (mCursor.moveToNext());
                                        }
                                    }
                                    if (mCursor != null) {
                                        mCursor.close();
                                    }

                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                                    mBuilder.setView(mViewUpdate);
                                    mBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String TagEditText = mTagEditText.getText().toString().trim();
                                            String ScriptEditText = mScriptEditText.getText().toString().trim();
                                            if (!TagEditText.isEmpty() && !ScriptEditText.isEmpty()){
                                                // Query for update from the database and get a cursor back
                                                Cursor mCursorUpdate = mSQLiteDatabase.rawQuery("UPDATE keynote_table SET keynote_tag ='"+TagEditText+"', keynote_body ='"+ScriptEditText+"' " +
                                                        "WHERE keynote_tag ='" + mKeynoteTag + "'", null); // What a crazy combination i got here!!
                                                if (mCursorUpdate != null ) {
                                                    if  (mCursorUpdate.moveToFirst()) {
                                                        do {
                                                            //String KeyNoteBody = mCursorUpdate.getString(mCursorUpdate.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_BODY));
                                                        } while (mCursorUpdate.moveToNext());
                                                    }
                                                }
                                                if (mCursorUpdate != null) {
                                                    mCursorUpdate.close();
                                                }
                                                Intent mRestart = new Intent(getActivity(), DashboardActivity.class);
                                                startActivity(mRestart);
                                                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Text box should not be left blank", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    AlertDialog mAlert = mBuilder.create();
                                    mAlert.show();
                                }
                            } while (mCursor.moveToNext());
                        }
                    }
                    if (mCursor != null) {
                        mCursor.close();
                    }
                } else {
                    // Null Detected
                    Toast.makeText(getActivity(), "Text box should not be left blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete KeyNote
                TextInputEditText mEditText = (TextInputEditText) mViewTag.findViewById(R.id.input_keynote_tag);
                String mKeynoteTag = mEditText.getText().toString().trim();
                if (!mKeynoteTag.isEmpty()){
                    // Query for items from the database and get a cursor back
                    Cursor mCursor = mSQLiteDatabase.rawQuery("SELECT keynote_tag FROM keynote_table WHERE keynote_tag ='" + mKeynoteTag + "'", null);
                    if (mCursor != null ) {
                        if (mCursor.moveToFirst()) {
                            do {
                                String KeyNoteTag = mCursor.getString(mCursor.getColumnIndex(MindSpeechDBTable.KEYNOTE_FIELD.KEYNOTE_TAG));
                                if (!mKeynoteTag.equalsIgnoreCase(KeyNoteTag)){
                                    Toast.makeText(getActivity(), "Invalid Keynote Tag", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Query for delete from the database and get a cursor back
                                    Cursor mCursorDel = mSQLiteDatabase.rawQuery("DELETE FROM keynote_table " +
                                            "WHERE keynote_tag ='" + mKeynoteTag + "'", null); // What a crazy combination i got here!!
                                    if (mCursorDel != null ) {
                                        if  (mCursorDel.moveToFirst()) {
                                            do {
                                                // Do Nothing
                                            } while (mCursorDel.moveToNext());
                                        }
                                    }
                                    if (mCursorDel != null) {
                                        mCursorDel.close();
                                    }
                                }
                            } while (mCursor.moveToNext()) ;
                        }
                        if (mCursor != null) {
                            mCursor.close();
                        }

                        Intent mRestart = new Intent(getActivity(), DashboardActivity.class);
                        startActivity(mRestart);

                    }
                } else {
                    // Null Detected
                    Toast.makeText(getActivity(), "Text box should not be left blank", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog mAlert = mBuilder.create();
        mAlert.show();

    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar,
                                                 KeyNoteItem mKeyNoteItem) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = KeyNotePlayer.getStartIntent(activity, mKeyNoteItem);
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }


}
