package com.example.hades.minitodo.beans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Hades on 2017/5/2.
 */
public class TodoItem implements Serializable{

    private String mToDoText;
    private boolean mHasReminder;
    private int mTodoColor;
    private Date mToDoDate;
    private UUID mTodoIdentifier;

    private static final String TODOTEXT="todotext";
    private static final String TODOREMINDER="todoreminder";
    private static final String TODOCOLOR="todocolor";
    private static final String TODODATE="tododate";
    private static final String TODOIDENTIFIER="todoidentifier";

    public TodoItem(String todoBody,boolean hasReminder,Date todoDate){
        mToDoText=todoBody;
        mHasReminder=hasReminder;
        mToDoDate=todoDate;
        mTodoColor=1677725;
        mTodoIdentifier=UUID.randomUUID();
    }

    public TodoItem(JSONObject jsonObject) throws JSONException {
        mToDoText=jsonObject.getString(TODOTEXT);
        mHasReminder=jsonObject.getBoolean(TODOREMINDER);
        mTodoColor=jsonObject.getInt(TODOCOLOR);
        mTodoIdentifier=UUID.fromString(jsonObject.getString(TODOIDENTIFIER));

        if(jsonObject.has(TODODATE)){
            mToDoDate=new Date(jsonObject.getLong(TODODATE));
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TODOTEXT, mToDoText);
        jsonObject.put(TODOREMINDER, mHasReminder);
        if (mToDoDate != null) {
            jsonObject.put(TODODATE, mToDoDate.getTime());
        }
        jsonObject.put(TODOCOLOR, mTodoColor);
        jsonObject.put(TODOIDENTIFIER, mTodoIdentifier.toString());
        return jsonObject;
    }

    public TodoItem(){
        this("Clean my room",true,new Date());
    }

    public String getmToDoText() {
        return mToDoText;
    }

    public void setmToDoText(String mToDoText) {
        this.mToDoText = mToDoText;
    }

    public boolean ismHasReminder() {
        return mHasReminder;
    }

    public void setmHasReminder(boolean mHasReminder) {
        this.mHasReminder = mHasReminder;
    }

    public int getmTodoColor() {
        return mTodoColor;
    }

    public void setmTodoColor(int mTodoColor) {
        this.mTodoColor = mTodoColor;
    }

    public Date getmToDoDate() {
        return mToDoDate;
    }

    public void setmToDoDate(Date mToDoDate) {
        this.mToDoDate = mToDoDate;
    }

    public UUID getmTodoIdentifier() {
        return mTodoIdentifier;
    }

    public void setmTodoIdentifier(UUID mTodoIdentifier) {
        this.mTodoIdentifier = mTodoIdentifier;
    }
}
