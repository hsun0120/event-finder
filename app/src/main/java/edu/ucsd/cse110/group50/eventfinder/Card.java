package edu.ucsd.cse110.group50.eventfinder;

import android.net.Uri;
import java.io.Serializable;

/**
 * Created by Haoran Sun on 2016/10/30.
 */

public class Card implements Serializable {

    // private Drawable cardImg = getResources().getDrawable(R.drawable.activity_default);
    private String cardName = "Event";
    private String date = "2016/10/30";
    private String description = "Halloween party.";
    private transient Uri location; //Not serializable

    private static Card ourInstance = new Card("Sample.", "2016/10/30", "party",
            Uri.parse("geo:37.7749,-122.4194"));

    public static Card getInstance() {
        return ourInstance;
    }

    public Card() {
        this.cardName = "Event Untitled";
        this.date = "Date Not Set";
        this.description = "Description: Nothing more to say!";
        this.location = Uri.parse("geo:37.7749,-122.4194");
    };

    public Card(String name, String date, String description, Uri location) {
        //cardImg = img;
        this.cardName = name;
        this.date = date;
        this.description = description;
        this.location = location;
    }
    //public Drawable getCardImage() {
    //    return cardImg;
    //}

    public  void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCardName(){ return cardName;}

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Uri getLocation() { return  this.location; }
}
