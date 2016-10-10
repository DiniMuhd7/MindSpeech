package com.deenysoft.mindspeech.dashboard.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deenysoft.mindspeech.R;

import java.util.ArrayList;

/**
 * Created by shamsadam on 07/09/16.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.contact_item, parent, false);
        }
        // Populate the data into the template view using the data object
        TextView tvName = (TextView) view.findViewById(R.id.title);
        TextView tvPhone = (TextView) view.findViewById(R.id.phone);
        //TextView tvEmail = (TextView) view.findViewById(R.id.email);
        tvName.setText(contact.name);
        tvPhone.setText("");
        /*
        tvEmail.setText("");
        if (contact.emails.size() > 0 && contact.emails.get(0) != null) {
            tvEmail.setText(contact.emails.get(0).address);
        }
        */
        if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
            tvPhone.setText(contact.numbers.get(0).number);
        }
        return view;
    }


}
