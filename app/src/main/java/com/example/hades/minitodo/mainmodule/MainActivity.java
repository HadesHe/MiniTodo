package com.example.hades.minitodo.mainmodule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.hades.minitodo.R;
import com.example.hades.minitodo.addtodomodule.AddTodoActivity;
import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.data.StoreRetrieveData;
import com.example.hades.minitodo.reminder.ReminderActivity;
import com.example.hades.minitodo.services.TodoNotificationService;
import com.example.hades.minitodo.view.CustomRecyclerScrollViewListener;
import com.example.hades.minitodo.view.RecyclerViewEmptySupport;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {


    public static final String TODOITEM="todoitem";
    private static final int REQUEST_ID_TODO_ITEM=100;
    public static final String DATE_TIME_FORMAT_12_HOUR="MMM d, yyyy h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR="MMM d, yyyy k:mm";

    public static final String SHARED_PREF_DATA_SET_CHANGED="shared_pref_data_set_changed";
    public static final String CHANGE_OCCURED="change_occured";
    public static final String FILENAME ="todoitems.json" ;

    public static final String RECREATE_ACTIVITY="recreate_activity";


    private String theme="name_of_the_theme";
    public static final String THEME_PREFERENCES="theme_preference";
    public static final String THEME_SAVED="theme_saved";
    public static final String DARKTHEME="darktheme";
    public static final String LIGHTTHEME="lighttheme";
    private int mTheme=-1;
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<TodoItem> mToDoItemsArrayList;
    private BasicListAdapter adapter;
    private CoordinatorLayout mCoordLayout;
    private FloatingActionButton mAddTodoItemFAB;
    private RecyclerViewEmptySupport mRecyclerView;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    private ItemTouchHelper itemTouchHelper;


    public static ArrayList<TodoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData){
        ArrayList<TodoItem> items=null;
        try {
            items=storeRetrieveData.loadFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(items==null){
            items=new ArrayList<>();
        }
        return items;
    }

    private void setAlarms(){
        if(mToDoItemsArrayList!=null){
            for (TodoItem item : mToDoItemsArrayList) {
                if(item.ismHasReminder()&&item.getmToDoDate().before(new Date())){
                    item.setmToDoDate(null);
                    continue;
                }
                Intent i=new Intent(this,TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOUUID,item.getmTodoIdentifier());
                i.putExtra(TodoNotificationService.TODOTEXT,item.getmToDoText());
                createAlarm(i,item.getmTodoIdentifier().hashCode(),item.getmToDoDate().getTime());

            }
        }
    }

    private void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am=getAlarmManager();
        PendingIntent pi=PendingIntent.getService(this,requestCode,i,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP,timeInMillis,pi);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getSystemService(ALARM_SERVICE);
    }

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
        mToDoItemsArrayList=getLocallyStoredData(storeRetrieveData);
        adapter=new BasicListAdapter(MainActivity.this,mToDoItemsArrayList);
        setAlarms();

        final Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordLayout=(CoordinatorLayout)findViewById(R.id.myCoordinatorLayout);
        mAddTodoItemFAB=(FloatingActionButton)findViewById(R.id.addToDoItemFab);

        mAddTodoItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTodo=new Intent(MainActivity.this,AddTodoActivity.class);
                TodoItem item = new TodoItem("",false,null);
                int color= ColorGenerator.MATERIAL.getRandomColor();
                item.setmTodoColor(color);
                newTodo.putExtra(TODOITEM,item);
                startActivityForResult(newTodo,REQUEST_ID_TODO_ITEM);
            }
        });

        mRecyclerView=(RecyclerViewEmptySupport)findViewById(R.id.todoRecyclerView);

        if(theme.equals(LIGHTTHEME)){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }

        mRecyclerView.setEmptyView(findViewById(R.id.todoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        customRecyclerScrollViewListener=new CustomRecyclerScrollViewListener(){

            @Override
            public void hide() {

                CoordinatorLayout.LayoutParams lp= (CoordinatorLayout.LayoutParams) mAddTodoItemFAB.getLayoutParams();
                int fabMargin=lp.bottomMargin;
                mAddTodoItemFAB.animate().translationY(mAddTodoItemFAB.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();

            }

            @Override
            public void show() {
                mAddTodoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            }
        };
        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);

        ItemTouchHelper.Callback callback=new ItemTouchHelperClass(adapter);
        itemTouchHelper =new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED,MODE_PRIVATE);
        if(sharedPreferences.getBoolean(ReminderActivity.EXIT,false)){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(ReminderActivity.EXIT,false);
            editor.apply();
            finish();
        }

        if(getSharedPreferences(THEME_PREFERENCES,MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY,false)){
            SharedPreferences.Editor editor=getSharedPreferences(THEME_PREFERENCES,MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY,false);
            editor.apply();
            recreate();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED,MODE_PRIVATE);
        if(sharedPreferences.getBoolean(CHANGE_OCCURED,false)){
            mToDoItemsArrayList=getLocallyStoredData(storeRetrieveData);
            adapter=new BasicListAdapter(MainActivity.this,mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED,false);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.aboutMeMenuItem:
                // TODO: 2017/5/5 intent to AboutActivity
                return true;
            case R.id.preferences:
                // TODO: 2017/5/5 intent to SettingActivity.class
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }
}
