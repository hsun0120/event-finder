package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;

/**
 * Created by Haoran Sun on 2016/10/30.
 */

public class Card {
    int picID;
    String title;
    String text;

    public Card(int picID, String title, String text){
        this.picID = picID;
        this.title = title;
        this.text = text;
    }

    public int getPicID(){
        return this.picID;
    }

    public String getText(){
        return this.text;
    }
}
