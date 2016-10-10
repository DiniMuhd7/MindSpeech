package com.deenysoft.mindspeech.dashboard.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.account.GoogleAuthLoginActivity;
import com.deenysoft.mindspeech.base.BaseActivity;
import com.deenysoft.mindspeech.dashboard.DashboardActivity;
import com.deenysoft.mindspeech.dashboard.contact.Contact;
import com.deenysoft.mindspeech.dashboard.model.ChatItem;
import com.deenysoft.mindspeech.dashboard.model.UserItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by shamsadam on 07/09/16.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";
    public static final String EXTRA_CHAT_KEY = "chat_key";
    private static String APIKEY = "AIzaSyDdXYGrB_Yq7lQhVihCUQrFUZWwjMrfPHE";

    private SharedPreferences mSharedPreferences;
    private TextInputEditText mTextInputEditText;
    private ImageView mSendImageViewButton;
    private ImageView mTranslateImageView;
    private DatabaseReference mChatReference;
    private ValueEventListener mChatListener;
    private String mChatKey;
    private ChatAdapter mAdapter;
    private RecyclerView mChatRecycler;
    // ListView Unique ID
    private String ListCategoryID;
    private FirebaseAuth mAuth;
    private DashboardActivity mDashboardActivity;
    private LayoutInflater mLayoutInflater;
    private Spinner mTranslateFromSpinner;
    private Spinner mTranslateToSpinner;

    private ImageView mImageShare;
    private ImageView mImageRate;

    private NativeExpressAdView mNativeAdView;


    // Activity Intent Starter
    public static Intent getStartIntent(Context context, Contact mContact) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(Contact.TAG, mContact.name);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        setupToolbar();
        mAuth = FirebaseAuth.getInstance();

        // Get the unique ListCategoryID from Intent
        ListCategoryID = getIntent().getStringExtra(Contact.TAG);
        mDashboardActivity = new DashboardActivity();
        mLayoutInflater = getLayoutInflater();

        // Get chat key from intent
        mChatKey = getIntent().getStringExtra(EXTRA_CHAT_KEY);
        if (mChatKey == null) {
            //throw new IllegalArgumentException("Must pass EXTRA_CHAT_KEY");
        }

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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatActivity.this);
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


        // Get Chat Code Generator
        //Long chatCode = Long.parseLong(mDashboardActivity.getChatCodeGenerate(mCode).toString()) ;
        //String finalCode = "chat"+chatCode+"";
        // Initialize Database
        ListCategoryID.replaceAll(" ", "-");
        String Auth = mAuth.getCurrentUser().getDisplayName();
        mChatReference = FirebaseDatabase.getInstance().getReference()
                .child("MindSpeech").child(""+Auth).child(""+ListCategoryID);

        mChatRecycler = (RecyclerView) findViewById(R.id.recycler_chat);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mTextInputEditText = (TextInputEditText) findViewById(R.id.editText);
        mSendImageViewButton = (ImageView) findViewById(R.id.send);
        mTranslateImageView = (ImageView) findViewById(R.id.translate);

        // Get default preference
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String mPrefTranslateFrom = mSharedPreferences.getString("translateFrom", "en");
        final String mPrefTranslateTo = mSharedPreferences.getString("translateTo", "en");
        //final String mChatCode = mSharedPreferences.getString("code", finalCode);

        mTranslateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a brief preference dialog
                final View mView = mLayoutInflater.inflate(R.layout.brief_settings_dialog, null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatActivity.this);
                mBuilder.setView(mView);

                mTranslateFromSpinner = (Spinner) mView.findViewById(R.id.translateFromSpinner);
                mTranslateToSpinner = (Spinner) mView.findViewById(R.id.translateToSpinner);

                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> mAdapterTranslateFrom = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.languageTranslate, R.layout.spinner_item);
                ArrayAdapter<CharSequence> mAdapterTranslateTo = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.languageTranslate, R.layout.spinner_item);
                // Keep the selected values as TypedArray
                Resources res = getResources();
                final TypedArray languageTranslateValues = res
                        .obtainTypedArray(R.array.languageTranslateValues);
                //Specify the layout to use when the list of choices appears
                mAdapterTranslateFrom.setDropDownViewResource(R.layout.spinner_item);
                mAdapterTranslateTo.setDropDownViewResource(R.layout.spinner_item);
                //Apply the adapter to the spinner
                mTranslateFromSpinner.setAdapter(mAdapterTranslateFrom);
                mTranslateToSpinner.setAdapter(mAdapterTranslateTo);

                // Spinner OnItemSelected Listeners
                mTranslateFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //Get the selected value
                        String languageValue = languageTranslateValues.getString(position);
                        final String mTranslateFrom = mSharedPreferences.getString("translateFrom", languageValue);
                        if (mTranslateFrom != null) {
                            //  Make a new preferences editor
                            SharedPreferences.Editor e = mSharedPreferences.edit();
                            //  Edit preference to make it default
                            e.putString("translateFrom", languageValue);
                            //  Apply changes
                            e.apply();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                mTranslateToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //Get the selected value
                        String languageValue = languageTranslateValues.getString(position);
                        final String mTranslateTo = mSharedPreferences.getString("translateTo", languageValue);
                        if (mTranslateTo != null) {
                            //  Make a new preferences editor
                            SharedPreferences.Editor e = mSharedPreferences.edit();
                            //  Edit preference to make it default
                            e.putString("translateTo", languageValue);
                            //  Apply changes
                            e.apply();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // Dialog positive button
                mBuilder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ChatActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        Toast.makeText(ChatActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog mAlert = mBuilder.create();
                mAlert.show();
            }
        });

        // Send message onClickListener & Translate API Workflow
        mSendImageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mChatText = mTextInputEditText.getText().toString().trim();
                if (!mChatText.isEmpty() && mPrefTranslateFrom.equals(mPrefTranslateTo)) {
                    // No Translation Required. Proceed
                    postChat(mChatText);
                    Toast.makeText(ChatActivity.this, "Translation is not enable", Toast.LENGTH_SHORT).show();

                } else if (!mChatText.isEmpty() && !mPrefTranslateFrom.equals(mPrefTranslateTo)) {
                    // Proceed
                    String mGenerateURL = "https://www.googleapis.com/language/translate/v2?key=" + APIKEY + "&q=" + mChatText + "&source=" + mPrefTranslateFrom + "&target=" + mPrefTranslateTo + "";
                    mGenerateURL = mGenerateURL.replaceAll(" ", "%20");
                    //Toast.makeText(ChatActivity.this, "" + mGenerateURL, Toast.LENGTH_LONG).show();

                    // Setting up a HTTP Get Request
                    JSONParser mJSONParser = new JSONParser();
                    mJSONParser.execute(mGenerateURL);

                    Toast.makeText(ChatActivity.this, "Translated From: "+mPrefTranslateFrom +" Translated To: "+mPrefTranslateTo, Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(ChatActivity.this, "Text box should not be left blank", Toast.LENGTH_SHORT).show();
                }
            }

        });


        //Toast.makeText(ChatActivity.this, ""+mPrefTranslateFrom +mPrefTranslateTo, Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onStart() {
        super.onStart();

        // Listen for chats
        mAdapter = new ChatAdapter(this, mChatReference);
        mChatRecycler.setAdapter(mAdapter);
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
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mChatListener != null) {
            mChatReference.removeEventListener(mChatListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }


    public void postChat(final String mText){
        final String uid = mAuth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Create new comment object
                        // Get user information
                        UserItem user = dataSnapshot.getValue(UserItem.class);
                        //String authorName = user.username;

                        ChatItem chat = new ChatItem(uid, mText);
                        //Push the comment, it will appear in the list
                        mChatReference.push().setValue(chat);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupToolbar() {
        // Get the unique ListCategoryID from Intent
        ListCategoryID = getIntent().getStringExtra(Contact.TAG);
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_element);
        ab.setTitle(ListCategoryID); // Update ActionBar
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


    // Ads Dialog
    public void AdsAlertDialog(){
        // Prepare Dialog View and Grid View
        View mView = getLayoutInflater().inflate(R.layout.ads_dialog_chat_activity, null);
        mNativeAdView = (NativeExpressAdView) mView.findViewById(R.id.adViewNative);
        mNativeAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        // Set view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);
        //builder.setTitle("ADD");
        builder.show();

    }


    @Override
    public boolean providesActivityToolbar() {
        return false;
    }


    @Override
    public void onClick(View view) {

    }


    // Anonymous class containing asynchronous task
    public class JSONParser extends AsyncTask <String, Void, String> {

        private JSONObject mJSONObject = null;
        private JSONArray mJSONArray = null;
        private String mJSON = "";
        BufferedReader mBuffReader;
        InputStreamReader mInputStreamReader;

        // Empty constructor
        public JSONParser(){}


        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... mGenerateURL) {

            URL mURL = null;
            try {
                mURL = new URL(mGenerateURL[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) mURL.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            try {
                urlConnection.setRequestMethod("GET"); //Request method set to GET
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            int responseCode = 0;
            try {
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                try {
                    //Read in the data from input stream, this can be done a variety of ways
                    InputStream mInputStream = null;
                    mInputStream = new BufferedInputStream(urlConnection.getInputStream());
                    mInputStreamReader = new InputStreamReader(mInputStream);
                    mBuffReader = new BufferedReader(mInputStreamReader);
                    StringBuilder mStringBuilder = new StringBuilder();
                    String mChunck;
                    while ((mChunck = mBuffReader.readLine()) != null) {
                        mChunck.trim();
                        mStringBuilder.append(mChunck +"\n");
                    }

                    // Retrieve the Generated JSONObject & JSONArray
                    mJSON = mStringBuilder.toString();
                    mJSONObject = new JSONObject(mJSON);
                    JSONObject mData = mJSONObject.getJSONObject("data");
                    mJSONArray = mData.getJSONArray("translations");
                    for (int i = 0; i < mJSONArray.length(); i++){
                        final JSONObject mTrans = mJSONArray.getJSONObject(i);
                        String mTranslatedText = mTrans.getString("translatedText");

                        postChat(mTranslatedText); // Called to Post User Chat

                        return mTranslatedText;
                    }

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    //Disconnect the connection
                    urlConnection.disconnect();
                }
                if (mBuffReader != null){
                    try {
                        mBuffReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                // 200 is returned by getResponseCode() if the response code could not be retrieved.
                // Signal to the caller that something was wrong with the connection.
                urlConnection.disconnect();

            }

            return Integer.toString(R.string.errorMessage);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Toast.makeText(ChatActivity.this, "" +result, Toast.LENGTH_LONG).show();
            Intent intent=new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Add data to the intent, the receiving app will decide what to do with it.
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, ""+result);

            startActivity(Intent.createChooser(intent, "How do you want to send"));
            finish();
        }


        public void postChat(final String mText){
            final String uid = mAuth.getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Create new comment object
                                // Get user information
                                UserItem user = dataSnapshot.getValue(UserItem.class);
                                //String authorName = user.username;

                                ChatItem chat = new ChatItem(uid, mText);
                                //Push the comment, it will appear in the list
                                mChatReference.push().setValue(chat);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    }


    public static class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mChatIds = new ArrayList<>();
        private List<ChatItem> mChatItem = new ArrayList<>();

        public ChatAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    ChatItem mChat = dataSnapshot.getValue(ChatItem.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mChatIds.add(dataSnapshot.getKey());
                    mChatItem.add(mChat);
                    notifyItemInserted(mChatItem.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    ChatItem newChat = dataSnapshot.getValue(ChatItem.class);
                    String chatKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mChatIds.indexOf(chatKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mChatItem.set(commentIndex, newChat);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + chatKey);
                    }
                    // [END_EXCLUDE]
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String chatKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mChatIds.indexOf(chatKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mChatIds.remove(commentIndex);
                        mChatItem.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + chatKey);
                    }
                    // [END_EXCLUDE]
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    ChatItem movedChat = dataSnapshot.getValue(ChatItem.class);
                    String chatKey = dataSnapshot.getKey();

                    // ...
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load chats.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;

        }

        @Override
        public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.chat_item, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatViewHolder holder, int position) {

            ChatItem mChat = mChatItem.get(position);
            holder.chatView.setText(mChat.text);

        }

        @Override
        public int getItemCount() {
            return mChatItem.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }


    private static class ChatViewHolder extends RecyclerView.ViewHolder {

        public TextView chatView;
        //public ImageView mImageView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatView = (TextView) itemView.findViewById(R.id.chat_body);
            //mImageView = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }

}
