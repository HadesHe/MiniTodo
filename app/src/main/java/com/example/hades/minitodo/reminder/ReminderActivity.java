package com.example.hades.minitodo.reminder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hades.minitodo.R;

public class ReminderActivity extends AppCompatActivity {

    public static final String EXIT = "exit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
    }
}
