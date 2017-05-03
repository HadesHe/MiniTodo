package com.example.hades.minitodo.data;

import android.content.Context;

import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.mainmodule.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

    public ArrayList<TodoItem> loadFromFile() throws IOException, JSONException {
        ArrayList<TodoItem> items=new ArrayList<>();
        BufferedReader bufferedReader=null;
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=mContext.openFileInput(mFileName);
            StringBuilder builder=new StringBuilder();
            String line;
            bufferedReader=new BufferedReader(new InputStreamReader(fileInputStream));
            while ((line=bufferedReader.readLine())!=null){
                builder.append(line);
            }

            JSONArray jsonArray=(JSONArray)new JSONTokener(builder.toString()).nextValue();
            for(int i=0;i<jsonArray.length();i++){
                TodoItem item=new TodoItem(jsonArray.getJSONObject(i));
                items.add(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(fileInputStream!=null){
                fileInputStream.close();
            }
        }
        return items;
    }
}
