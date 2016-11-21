package edu.ucsd.cse110.group50.eventfinder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private static ArrayList<Event> eventList;
    private ArrayList<EventViewHolder> cards;

    // Constructor
    public EventAdapter( ArrayList<Event> contactList) {

        eventList = contactList;
        this.cards = new ArrayList<>();

    }

    @Override
    public int getItemCount() {

        return eventList.size();

    }

    @Override
    public void onBindViewHolder( final EventViewHolder contactViewHolder, int i ) {

        Event ci = eventList.get( i );
        contactViewHolder.setEvent( ci );
        cards.add( contactViewHolder );

    }

    @Override
    public EventViewHolder onCreateViewHolder( ViewGroup viewGroup, int i ) {
        View itemView = LayoutInflater.
                from( viewGroup.getContext() ).
                inflate( R.layout.card_layout, viewGroup, false );

        return new EventViewHolder( itemView );
    }

    void destroyCards() {

        for ( EventViewHolder card : cards ) {

            card.removeEvent();

        }

    }

    // Inner class ViewHolder
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        // Event information. Expected to be enriched in the future
        private TextView vName;
        private TextView vAddress;
        private TextView vDescription;

        private Event data;
        private LoadListener listener;

        EventViewHolder( View v ) {

            super(v);
            vName =  (TextView) v.findViewById( R.id.newUserTitle );
            vAddress = (TextView) v.findViewById( R.id.txtAddress );
            vDescription = (TextView) v.findViewById( R.id.txtDescrip );

            v.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                    int position = getAdapterPosition();
                    intent.putExtra( "event_card", eventList.get(position) );
                    v.getContext().startActivity(intent);
                }

            });

        }

        void setEvent( Event e ) {

            this.data = e;
            listener = new LoadListener() {

                @Override
                public void onLoadComplete( Object data ) {

                    EventViewHolder.this.update();

                }

            };
            e.addListener( listener );
            update();

        }

        void removeEvent() {

            data.removeListener( listener );
            listener = null;

        }

        void update() {

            vName.setText( data.getName() );
            if ( !data.getHasPassword() || !MapView.user_on_all_events_flag ) {
                vAddress.setText( data.getAddress() );
                vDescription.setText( data.getDescription() );
            } else {
                vAddress.setText( R.string.locked_event_data );
                vDescription.setText( R.string.locked_event_data );
            }

        }

    }


}