package com.example.hades.minitodo.data;

import android.content.Context;

import com.example.hades.minitodo.mainmodule.MainActivity;

/**
 * Created by Hades on 2017/5/2.
 */
public class StoreRetrieveData {
    private final Context mContext;
    private final String mFileName;

    public StoreRetrieveData(Context context, String filename) {
        mContext=context;
        mFileName=filename;

    }
}
