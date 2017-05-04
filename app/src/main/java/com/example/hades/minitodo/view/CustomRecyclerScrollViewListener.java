package com.example.hades.minitodo.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Hades on 2017/5/4.
 */
public abstract class CustomRecyclerScrollViewListener extends RecyclerView.OnScrollListener{

    int scrollDist=0;
    boolean isVisible=true;
    static final float MINIMUM=20;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(isVisible&&scrollDist>MINIMUM){
            Log.d("OskarSchindler","Hide "+scrollDist);
            hide();
            scrollDist=0;
            isVisible=false;
        }else if(!isVisible&&scrollDist<-MINIMUM){
            Log.d("OskarSchindler","Show "+scrollDist);
            show();
            scrollDist=0;
            isVisible=true;
        }

        if((isVisible&&dy>0)||(!isVisible&&dy<0)){
            Log.d("OskarSchindler","Add up "+scrollDist);
            scrollDist+=dy;
        }
    }

    public abstract void hide();
    public abstract void show();


}
