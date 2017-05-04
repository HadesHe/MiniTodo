package com.example.hades.minitodo.mainmodule;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Hades on 2017/5/4.
 */
public class ItemTouchHelperClass extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int upFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swipeFlags=ItemTouchHelper.START|ItemTouchHelper.END;
        return makeMovementFlags(upFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemRemoved(viewHolder.getAdapterPosition());

    }

    public interface ItemTouchHelperAdapter{

        void onItemMoved(int fromPosition, int toPosition);

        void onItemRemoved(int position);
    }

    public ItemTouchHelperClass(ItemTouchHelperAdapter adapter) {
        this.adapter=adapter;
    }
}