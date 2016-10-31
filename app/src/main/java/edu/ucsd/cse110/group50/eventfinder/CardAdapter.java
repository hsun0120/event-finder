package edu.ucsd.cse110.group50.eventfinder;

import java.lang.Object;
/**
 * CardView Adapter
 */


public class CardAdapter extends Adapter<CardAdapter.ViewHolder> {
    private String[] data;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView cardImage;
        public TextView cardName;
        public ViewHolder(TextView u, ImageView v) {
            super(u,v);
            cardName = u;
            cardImage = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(String[] input) {
        data = input;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View u = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_name, parent, false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_image, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(u,v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardName.setText(data[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.length;
    }

}
