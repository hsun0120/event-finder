package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;

/**
 * Created by Haoran Sun on 2016/10/30.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private LinkedList<Card> cards;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleTextView;
        public TextView desTextView;
        public TextView dateTextView;
        public ViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.info_title);
            desTextView = (TextView) v.findViewById(R.id.info_des);
            //imageView = (ImageView) v.findViewById(R.id.activityImg);
            dateTextView = (TextView) v.findViewById(R.id.info_date);
        }
    }

    public CardAdapter(LinkedList<Card> cards){
        this.cards = cards;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.titleTextView.setText(cards.get(position).getCardName());
        holder.desTextView.setText(cards.get(position).getDescription());
        //holder.imageView.setImageResource(/*cards.get(position).getPicID()*/R.drawable.activity_default);
        holder.dateTextView.setText(cards.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

}
