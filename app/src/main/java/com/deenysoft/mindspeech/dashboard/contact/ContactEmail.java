package com.deenysoft.mindspeech.dashboard.contact;

/**
 * Created by shamsadam on 07/09/16.
 */
public class ContactEmail {

    public String address;
    public String type;
    public String email;

    public ContactEmail(String address, String type) {
        this.address = address;
        this.type = type;
    }

    public ContactEmail(String emailAdress){
        this.email = emailAdress;
    }

}
