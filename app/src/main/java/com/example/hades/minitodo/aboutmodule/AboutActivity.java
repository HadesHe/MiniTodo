package com.example.hades.minitodo.aboutmodule;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hades.minitodo.R;
import com.example.hades.minitodo.mainmodule.MainActivity;

public class AboutActivity extends AppCompatActivity {

    private String theme;
    private String appVersion;
    private TextView mVersionTextView;
    private Toolbar toolbar;
    private TextView contactMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        theme=getSharedPreferences(MainActivity.THEME_PREFERENCES,MODE_PRIVATE).getString(MainActivity.THEME_SAVED,MainActivity.LIGHTTHEME);

        if(theme.equals(MainActivity.DARKTHEME)){
            setTheme(R.style.CustomStyle_DarkTheme);
        }else{
            setTheme(R.style.CustomStyle_LightTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Intent intent = getIntent();
        final Drawable backArrow=getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        if(backArrow!=null){
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        try {
            PackageInfo info=getPackageManager().getPackageInfo(getPackageName(),0);
            appVersion=info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mVersionTextView=(TextView)findViewById(R.id.aboutVersionTextView);
        mVersionTextView.setText(String.format(getResources().getString(R.string.app_version),appVersion));
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        contactMe=(TextView)findViewById(R.id.aboutContactMe);

        contactMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, "FeedBack", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }
}
