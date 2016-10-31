package edu.ucsd.cse110.group50.eventfinder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @Author Haoran Sun
 * @Date 2016/10/31.
 * Callback class for swipe and dismiss
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback{
    private final CardAdapter cardAdapter;

    /**
     * Constructor
     * @param cardAdapter Adapter to perform callback function
     */
    public ItemTouchHelperCallback(CardAdapter cardAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT); //Allow swipe in left and right direction
        this.cardAdapter = cardAdapter;
    }

    /**
     * Not implemented
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return false
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Detect swipe activity and update database
     * @param viewHolder Listening viewHolder
     * @param direction Swipe direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        cardAdapter.remove(viewHolder.getAdapterPosition());
    }
}
