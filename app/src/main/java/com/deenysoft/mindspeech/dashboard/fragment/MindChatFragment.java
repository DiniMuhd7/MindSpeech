package com.deenysoft.mindspeech.dashboard.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.deenysoft.mindspeech.R;
import com.deenysoft.mindspeech.dashboard.DashboardActivity;
import com.deenysoft.mindspeech.dashboard.activity.ChatActivity;
import com.deenysoft.mindspeech.dashboard.contact.Contact;
import com.deenysoft.mindspeech.dashboard.contact.ContactAdapter;
import com.deenysoft.mindspeech.dashboard.contact.ContactFetcher;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by shamsadam on 29/08/16.
 */
public class MindChatFragment extends Fragment {

    private ArrayList<Contact> mContactItemList;
    private ListView mListView;
    private static final int RC_CONTACT_PERMS = 104;

    // Empty Constructor
    public MindChatFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_mindchat, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_contact);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(), ""+mContactItemList.get(i), Toast.LENGTH_SHORT).show();
                Activity mActivity = getActivity();
                startActivity(mActivity, mContactItemList.get(i));
            }
        });

        return rootView;

    }

    @AfterPermissionGranted(RC_CONTACT_PERMS)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadContact();
        super.onViewCreated(view, savedInstanceState);

    }


    // Method to load contacts
    public void loadContact(){
        // Check that we have permission to read and write to external storage.
        String perm = android.Manifest.permission.READ_CONTACTS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !EasyPermissions.hasPermissions(getActivity(), perm)) {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.permission_contacts),
                    RC_CONTACT_PERMS, perm);
            return;
        }
        mContactItemList = new ContactFetcher(getActivity()).fetchAll();
        ContactAdapter adapterContacts = new ContactAdapter(getContext(), mContactItemList);
        mListView.setAdapter(adapterContacts);
    }

    public void startActivity(Activity mActivity, Contact mContact) {
        // Start the activity with the participants, animating from one to the other.
        Intent startIntent = ChatActivity.getStartIntent(mActivity, mContact);
        startActivity(startIntent);
    }

}
