package edu.ucsd.cse110.group50.eventfinder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    // Constructer
    public EventAdapter(List<Event> contactList) {
        this.eventList = contactList;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public void onBindViewHolder(final EventViewHolder contactViewHolder, int i) {
        Event ci = eventList.get(i);
        contactViewHolder.vName.setText(ci.getName());
        contactViewHolder.vAddress.setText(ci.getAddress());
        contactViewHolder.vDescription.setText(ci.getDescription());
        ci.addListener(new LoadListener() {
            EventViewHolder viewHolder = contactViewHolder;
            @Override
            public void onLoadComplete(Object data) {
                Event ci = (Event) data;
                contactViewHolder.vName.setText(ci.getName());
                contactViewHolder.vAddress.setText(ci.getAddress());
                contactViewHolder.vDescription.setText(ci.getDescription());
            }
        });
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new EventViewHolder(itemView);
    }

    // Inner class ViewHolder
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        // Event information. Expected to be enriched in the future
        protected TextView vName;
        protected TextView vAddress;
        protected TextView vDescription;

        public EventViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.name);
            vAddress = (TextView)  v.findViewById(R.id.txtAddress);
            vDescription = (TextView)  v.findViewById(R.id.txtDescrip);
        }
    }


}