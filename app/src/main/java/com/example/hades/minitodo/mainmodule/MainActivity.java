package com.example.hades.minitodo.mainmodule;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hades.minitodo.R;
import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.data.StoreRetrieveData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    public static final String SHARED_PREF_DATA_SET_CHANGED="shared_pref_data_set_changed";
    public static final String CHANGE_OCCURED="change_occured";
    public static final String FILENAME ="todoitems.json" ;


    private String theme="name_of_the_theme";
    public static final String THEME_PREFERENCES="theme_preference";
    public static final String THEME_SAVED="theme_saved";
    public static final String DARKTHEME="darktheme";
    public static final String LIGHTTHEME="lighttheme";
    private int mTheme=-1;
    private StoreRetrieveData storeRetrieveData;


    public static ArrayList<TodoItem>

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        theme=getSharedPreferences(THEME_PREFERENCES,MODE_PRIVATE).getString(THEME_SAVED,LIGHTTHEME);
        if(theme.equals(LIGHTTHEME)){
            mTheme=R.style.CustomStyle_LightTheme;
        }else{
            mTheme=R.style.CustomStyle_DarkTheme;
        }
        this.setTheme(mTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED,false);
        editor.apply();

        storeRetrieveData=new StoreRetrieveData(this,FILENAME);
        mToDoItemsArrayList=getLocalStoredData(storeRetrieveData);
    }
}
