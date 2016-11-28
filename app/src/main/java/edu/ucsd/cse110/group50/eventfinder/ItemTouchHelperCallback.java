package edu.ucsd.cse110.group50.eventfinder;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * @Author Haoran Sun
 * @Date 2016/10/31.
 * Callback class for swipe and dismiss
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback{
    private final EventAdapter eventAdapter;

    /**
     * Constructor
     * @param eventAdapter Adapter to perform callback function
     */
    public ItemTouchHelperCallback(EventAdapter eventAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT); //Allow swipe in left and right direction
        this.eventAdapter = eventAdapter;
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
        //Remove
    }
}