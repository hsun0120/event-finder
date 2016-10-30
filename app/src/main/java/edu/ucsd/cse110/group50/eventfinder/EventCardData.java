package edu.ucsd.cse110.group50.eventfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Object;
/**
 * Store information for a single event card
 */
public class EventCardData {

   // private Drawable cardImg = getResources().getDrawable(R.drawable.activity_default);
    private String cardName = "Sample!";

    private static EventCardData ourInstance = new EventCardData("Sample.");

    public static EventCardData getInstance() {
        return ourInstance;
    }

    private EventCardData(/*Drawable img*/, String name) {
        //cardImg = img;
        cardName = name;
    }
    //public Drawable getCardImage() {
    //    return cardImg;
    //}

    public String getCardName(){ return cardName;}
}
