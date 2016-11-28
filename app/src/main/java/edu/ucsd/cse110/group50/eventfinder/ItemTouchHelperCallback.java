package edu.ucsd.cse110.group50.eventfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.google.android.gms.maps.*;

/**
 * @Author Haoran Sun
 * @Date 2016/10/31.
 * Callback class for swipe and dismiss
 * Source: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf#.rzt0wowzq
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
     * Disable long press and drop
     * @return false
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * Disable swipe and dismiss on all event view fragment
     * @return true if swipe and drop is enabled in current fragment; otherwise false
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return !MapView.user_on_all_events_flag;
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
        MapView.itemSwiped(viewHolder.getAdapterPosition());
    }
}