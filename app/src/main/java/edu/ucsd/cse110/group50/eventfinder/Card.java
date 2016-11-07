package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Haoran Sun on 2016/10/30.
 */

public class Card implements Serializable {



    // private Drawable cardImg = getResources().getDrawable(R.drawable.activity_default);
    private String ownerName = "Event Owner";
    private String eventName = "Event";
    private String eventDate = "2016/10/30";
    private String eventDescription = "Halloween party.";
    private transient Uri location; //Not serializable
    private String address = "123 Kappa Ave, San Diego, 12345";

    private static Card ourInstance = new Card("Owner's Name","Sample.", "2016/10/30", "party", Uri.parse("geo:37.7749,-122.4194"),"123 Kappa Ave, San Diego, 12345");

    public static Card getInstance() {
        return ourInstance;
    }

    public Card() {
        this.ownerName = "Mei";
        this.eventName = "Event Untitled";
        this.eventDate = "eventDate Not Set";
        this.eventDescription = "Description: Nothing more to say!";
        this.address = "1234";

    }

    public Card(String ownerName, String eventName, String eventDate, String description, Uri location, String address) {
        this.ownerName = ownerName;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = description;
        this.location = location;
        this.address = address;
    }
    //public Drawable getCardImage() {
    //    return cardImg;
    //}

    public  void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventDescription(String description) {
        this.eventDescription = description;
    }

    public String getEventName(){ return eventName;}

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public Uri getLocation() { return  this.location; }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
