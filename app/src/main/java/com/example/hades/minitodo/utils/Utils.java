package com.example.hades.minitodo.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.example.hades.minitodo.R;

/**
 * Created by Hades on 2017/5/4.
 */
public class Utils {
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes=context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int toolbarHeight= (int) styledAttributes.getDimension(0,0);
        styledAttributes.recycle();
        return toolbarHeight;
    }
}
