package com.example.hades.minitodo.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.hades.minitodo.R;
import com.example.hades.minitodo.reminder.ReminderActivity;

import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class TodoNotificationService extends IntentService {


    public static final String TODOUUID = "todouuid";
    public static final String TODOTEXT = "todotext";
    private String mTodoText;
    private UUID mTodoUUID;

    public TodoNotificationService() {
        super("TodoNotificationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mTodoText=intent.getStringExtra(TODOTEXT);
            mTodoUUID=(UUID)intent.getSerializableExtra(TODOUUID);

            Log.d("OskarSchindler","onHandlerIntent called");

            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent i=new Intent(this, ReminderActivity.class);
            i.putExtra(TodoNotificationService.TODOUUID,mTodoUUID);
            Intent deleteIntent =new Intent(this,DeleteNotificationService.class);
            deleteIntent.putExtra(TODOUUID,mTodoUUID);
            Notification notification=new Notification.Builder(this)
                    .setContentTitle(mTodoText)
                    .setSmallIcon(R.drawable.ic_done_white_24dp)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setDeleteIntent(PendingIntent.getService(this,mTodoUUID.hashCode(),deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentIntent(PendingIntent.getActivity(this,mTodoUUID.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT))
                    .build();
            manager.notify(100,notification);
        }
    }

}
