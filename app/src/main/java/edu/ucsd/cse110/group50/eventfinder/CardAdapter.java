package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Haoran Sun on 2016/10/30.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    Context c;
    private static ArrayList<Card> cards;

    public class ViewHolder extends RecyclerView.ViewHolder {


        // each data item is just a string in this case
        TextView eventName;
        TextView eventDate;
        TextView eventDes;


        public ViewHolder(View v) {
            super(v);
            eventName = (TextView) v.findViewById(R.id.info_title);
            eventDes = (TextView) v.findViewById(R.id.info_des);
            eventDate = (TextView) v.findViewById(R.id.info_date);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                    int position = getAdapterPosition();
                    intent.putExtra("event_card", cards.get(position));
                    v.getContext().startActivity(intent);
                }
            });
        }


    }

//    public CardAdapter(Query query, Class<MyItem> itemClass, @Nullable ArrayList<MyItem> items,
//                     @Nullable ArrayList<String> keys) {
//        super(query, itemClass, items, keys);
//    }

    public CardAdapter(Context c, ArrayList<Card> cards){
        this.c = c;
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
        holder.eventName.setText(cards.get(position).getEventName());
        holder.eventDes.setText(cards.get(position).getEventDescription());
        //holder.imageView.setImageResource(/*cards.get(position).getPicID()*/R.drawable.activity_default);
        holder.eventDate.setText(cards.get(position).getEventDate());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    /**
     * Remove items from database if swiped
     * @param position Position in the dataset
     */
    public void remove(int position) {
        cards.remove(position);
        notifyItemRemoved(position);
    }
}
