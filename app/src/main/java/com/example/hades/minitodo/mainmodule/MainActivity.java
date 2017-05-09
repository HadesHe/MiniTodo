package com.example.hades.minitodo.mainmodule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.provider.AlarmClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.hades.minitodo.aboutmodule.AboutActivity;
import com.example.hades.minitodo.addtodomodule.AddTodoActivity;
import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.data.StoreRetrieveData;
import com.example.hades.minitodo.reminder.ReminderActivity;
import com.example.hades.minitodo.services.TodoNotificationService;
import com.example.hades.minitodo.settingmodule.SettingActivity;
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

    private BasicListAdapter.BasicListAdapterListener deleteListener=new BasicListAdapter.BasicListAdapterListener() {
        @Override
        public void onRemoveItemListener(final int justDeletePosition, final TodoItem todoItem) {
            Snackbar.make(mCoordLayout,"Delete Todo",Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.onRemove(justDeletePosition,todoItem);
                        }
                    }).show();

        }
    };


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

    public void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am=getAlarmManager();
        PendingIntent pi=PendingIntent.getService(this,requestCode,i,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP,timeInMillis,pi);
    }

    public void deleteAlarm(Intent intent, int requestCode){
        if(doesPendingIntentExist(intent, requestCode)) {
            PendingIntent pendingIntent = PendingIntent.getService(this,requestCode,intent, PendingIntent.FLAG_NO_CREATE);
            pendingIntent.cancel();
            getAlarmManager().cancel(pendingIntent);
        }
    }

    private boolean doesPendingIntentExist(Intent intent, int requestCode) {
        PendingIntent pi=PendingIntent.getService(this,requestCode,intent,PendingIntent.FLAG_NO_CREATE);
        return pi!=null;
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
        adapter=new BasicListAdapter(MainActivity.this,mToDoItemsArrayList,deleteListener);
        setAlarms();

        final Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordLayout=(CoordinatorLayout)findViewById(R.id.myCoordinatorLayout);
        mAddTodoItemFAB=(FloatingActionButton)findViewById(R.id.addToDoItemFAB);

        mAddTodoItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoItem item = new TodoItem("",false,null);
                int color= ColorGenerator.MATERIAL.getRandomColor();
                item.setmTodoColor(color);
                startAddTodoActivity(item);

            }
        });

        mRecyclerView=(RecyclerViewEmptySupport)findViewById(R.id.toDoRecyclerView);

        if(theme.equals(LIGHTTHEME)){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }

        mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
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

    public void startAddTodoActivity(TodoItem item) {
        Intent newTodo=new Intent(MainActivity.this,AddTodoActivity.class);
        newTodo.putExtra(TODOITEM,item);
        startActivityForResult(newTodo,REQUEST_ID_TODO_ITEM);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_CANCELED&&requestCode==REQUEST_ID_TODO_ITEM){
            TodoItem item= (TodoItem) data.getSerializableExtra(TODOITEM);
            if(item.getmToDoText().length()<=0){
                return;
            }

            boolean existed=false;

            if(item.ismHasReminder()&&item.getmToDoDate()!=null){
                Intent i=new Intent(this,TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT,item.getmToDoText());
                i.putExtra(TodoNotificationService.TODOUUID,item.getmTodoIdentifier());
                createAlarm(i,item.getmTodoIdentifier().hashCode(),item.getmToDoDate().getTime());
            }

            for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
                if(item.getmTodoIdentifier().equals(mToDoItemsArrayList.get(i).getmTodoIdentifier())){
                    mToDoItemsArrayList.set(i,item);
                    existed=true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }

            if(!existed){
                addToDataStore(item);
            }
        }
    }

    private void addToDataStore(TodoItem item) {
        mToDoItemsArrayList.add(item);
        adapter.notifyItemInserted(mToDoItemsArrayList.size()-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED,MODE_PRIVATE);
        if(sharedPreferences.getBoolean(CHANGE_OCCURED,false)){
            mToDoItemsArrayList=getLocallyStoredData(storeRetrieveData);
            adapter=new BasicListAdapter(MainActivity.this,mToDoItemsArrayList,deleteListener);
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
                Intent i=new Intent(this,AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.preferences:

                Intent intent=new Intent(this,SettingActivity.class);
                startActivity(intent);
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }
}
