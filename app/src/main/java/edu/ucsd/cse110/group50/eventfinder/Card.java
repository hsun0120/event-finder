package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;

/**
 * Created by Haoran Sun on 2016/10/30.
 */

public class Card {

    // private Drawable cardImg = getResources().getDrawable(R.drawable.activity_default);
    public  void setCardName(String cardName) {
        this.cardName = cardName;
    }public void setDate(String date) {
        this.date = date;
    }public void setDescription(String description) {
        this.description = description;
    }private String cardName = "Event";
    private String date = "2016/10/30";
    private String description = "Halloween party.";

    private static Card ourInstance = new Card("Sample.", "2016/10/30", "party");

    public static Card getInstance() {
        return ourInstance;
    }

    public Card() {};

    public Card(String name, String date, String description) {
        //cardImg = img;
        cardName = name;
        this.date = date;
        this.description = description;
    }
    //public Drawable getCardImage() {
    //    return cardImg;
    //}

    public String getCardName(){ return cardName;}

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
