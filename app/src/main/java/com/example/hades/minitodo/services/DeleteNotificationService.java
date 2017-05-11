package com.example.hades.minitodo.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.data.StoreRetrieveData;
import com.example.hades.minitodo.mainmodule.MainActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class DeleteNotificationService extends IntentService {

    private StoreRetrieveData storeRetrieveData;
    private ArrayList<TodoItem> mTodoItems;
    private TodoItem mItem;

    public DeleteNotificationService() {
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            storeRetrieveData=new StoreRetrieveData(this, MainActivity.FILENAME);
            UUID todoID= (UUID) intent.getSerializableExtra(TodoNotificationService.TODOUUID);

            mTodoItems=loadData();

            if(mTodoItems!=null){
                for (TodoItem item : mTodoItems) {
                    if(item.getTodoIdentifier().equals(todoID)){
                        mItem=item;
                        break;
                    }
                }

                if(mItem!=null){
                    mTodoItems.remove(mItem);
                    dataChanged();
                    saveData();
                }
            }
        }
    }

    private void dataChanged() {
        SharedPreferences sharedPreferences=getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(MainActivity.CHANGE_OCCURED,true);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private void saveData() {
        try {
            storeRetrieveData.saveToFile(mTodoItems);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TodoItem> loadData(){
        try {
            return storeRetrieveData.loadFromFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
