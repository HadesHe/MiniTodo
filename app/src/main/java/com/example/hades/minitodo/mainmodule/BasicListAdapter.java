package com.example.hades.minitodo.mainmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.hades.minitodo.R;
import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.utils.DateUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static com.example.hades.minitodo.mainmodule.MainActivity.DATE_TIME_FORMAT_12_HOUR;
import static com.example.hades.minitodo.mainmodule.MainActivity.DATE_TIME_FORMAT_24_HOUR;
import static com.example.hades.minitodo.mainmodule.MainActivity.LIGHTTHEME;
import static com.example.hades.minitodo.mainmodule.MainActivity.THEME_PREFERENCES;
import static com.example.hades.minitodo.mainmodule.MainActivity.THEME_SAVED;

/**
 * Created by Hades on 2017/5/3.
 */
public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {
    private  Context mContext;
    private ArrayList<TodoItem> mItems;

    public BasicListAdapter(Context context,ArrayList<TodoItem> mToDoItemsArrayList) {
        this.mItems=mToDoItemsArrayList;
        this.mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoItem item=mItems.get(position);

        SharedPreferences sharedPreferences=mContext.getSharedPreferences(THEME_PREFERENCES,MODE_PRIVATE);
        int bgColor;
        int todoTextColor;
        if(sharedPreferences.getString(THEME_SAVED,LIGHTTHEME).equals(LIGHTTHEME)){
            bgColor= Color.WHITE;
            todoTextColor=mContext.getResources().getColor(R.color.secondary_text);
        }else{
            bgColor=Color.DKGRAY;
            todoTextColor=Color.WHITE;
        }
        holder.linearLayout.setBackgroundColor(bgColor);
        if(item.ismHasReminder()&&item.getmToDoDate()!=null){
            holder.mTodoTextview.setMaxLines(1);
            holder.mTodoTimeTextview.setVisibility(View.VISIBLE);
        }else{
            holder.mTodoTimeTextview.setVisibility(View.GONE);
            holder.mTodoTextview.setMaxLines(2);
        }
        holder.mTodoTextview.setText(item.getmToDoText());
        holder.mTodoTextview.setTextColor(todoTextColor);

        TextDrawable myDrawable=TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(item.getmToDoText().substring(0,1),item.getmTodoColor());

        holder.mColorImageView.setImageDrawable(myDrawable);
        if(item.getmToDoDate()!=null){
            String timeToShow;
            if (android.text.format.DateFormat.is24HourFormat(mContext)) {
                timeToShow=DateUtil.formatDate(DATE_TIME_FORMAT_24_HOUR,item.getmToDoDate());
            }else{
                timeToShow= DateUtil.formatDate(DATE_TIME_FORMAT_12_HOUR,item.getmToDoDate());
            }
            holder.mTodoTimeTextview.setText(timeToShow);
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if(fromPosition<toPosition){
            for(int i=fromPosition;i<toPosition;i++){
                Collections.swap(mItems,i,i+1);
            }
        }else {
            for (int i=fromPosition;i>toPosition;i--){
                Collections.swap(mItems,i,i-1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemRemoved(int position) {
        TodoItem mJustDeletedTodoItem = mItems.remove(position);
        int mIndexOfDeletedTodoItem=position;
        //todo intent to TodoNotificationService.class

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final View mView;
        private final TextView mTodoTextview;
        private final TextView mTodoTimeTextview;
        private final ImageView mColorImageView;
        private final LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/5/3 intent to AddTodoActivity.class
                }
            });

            mTodoTextview=(TextView)itemView.findViewById(R.id.toDoListItemTextview);
            mTodoTimeTextview=(TextView)itemView.findViewById(R.id.toDoListItemTimeTextView);
            mColorImageView=(ImageView)itemView.findViewById(R.id.toDoListItemColorImageView);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.listItemLinearLayout);
        }
    }
}
