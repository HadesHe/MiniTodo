package com.example.hades.minitodo.addtodomodule;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hades.minitodo.R;
import com.example.hades.minitodo.beans.TodoItem;
import com.example.hades.minitodo.mainmodule.MainActivity;
import com.example.hades.minitodo.utils.DateUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class AddTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private static final String TAG = AddTodoActivity.class.getSimpleName();
    private String theme;
    private Toolbar mToolbar;
    private TodoItem mUserTodoItem;
    private String mUserEnteredText;
    private boolean mUserHasReminder;
    private Date mUserReminderDate;
    private int mUserColor;
    private ImageView imgAddTodoReminderIcon;
    private TextView tvAddTodoReminderText;
    private LinearLayout mContainerLayout;
    private LinearLayout mUserDateSpinnerContainingLinearLayout;
    private EditText mTodoTextBodyEditText;
    private SwitchCompat mTodoDateSwitch;
    private FloatingActionButton mTodoSendFloatingActionButton;
    private TextView mReminderTextView;
    private Button mDateButton;
    private Button mTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        initView();
        processExtra();
        initData();

    }

    private void initData() {
        if (theme.equals(MainActivity.DARKTHEME)) {
            imgAddTodoReminderIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_add_white_24dp));
            tvAddTodoReminderText.setTextColor(Color.WHITE);
        }

        if (mUserHasReminder && (mUserReminderDate != null)) {
            setReminderTextView();
            setEnterDateLayoutVisibleWithAnimations(true);
        }

        if (mUserReminderDate == null) {
            mTodoDateSwitch.setChecked(false);
            mReminderTextView.setVisibility(View.INVISIBLE);
        }

        mTodoTextBodyEditText.requestFocus();
        mTodoTextBodyEditText.setText(mUserEnteredText);

        mTodoTextBodyEditText.setSelection(mTodoTextBodyEditText.length());
        mTodoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserEnteredText = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setEnterDateLayoutVisible(mTodoDateSwitch.isChecked());

        mTodoDateSwitch.setChecked(mUserHasReminder && (mUserReminderDate != null));
        mTodoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    mUserReminderDate = null;
                }
                mUserHasReminder = isChecked;
                setDateAndTimeEditText();
                setEnterDateLayoutVisibleWithAnimations(isChecked);
                hideKeyboard(mTodoTextBodyEditText);

            }
        });

        setDateAndTimeEditText();

    }

    private void processExtra() {
        mUserTodoItem = (TodoItem) getIntent().getSerializableExtra(MainActivity.TODOITEM);
        mUserEnteredText = mUserTodoItem.getToDoText();
        mUserHasReminder = mUserTodoItem.getHasReminder();
        mUserReminderDate = mUserTodoItem.getToDoDate();
        mUserColor = mUserTodoItem.getTodoColor();
    }


    private void initView() {
        initToolBar();
        imgAddTodoReminderIcon = (ImageView) findViewById(R.id.imgAddTodoReminderIcon);
        tvAddTodoReminderText = (TextView) findViewById(R.id.tvAddTodoReminderText);

        mContainerLayout = (LinearLayout) findViewById(R.id.llAddTodoDateContainer);
        mUserDateSpinnerContainingLinearLayout = (LinearLayout) findViewById(R.id.llAddTodoEnterDate);
        mTodoTextBodyEditText = (EditText) findViewById(R.id.etAddTodoInput);
        mTodoDateSwitch = (SwitchCompat) findViewById(R.id.switchAddTodoHasDate);
        mTodoSendFloatingActionButton = (FloatingActionButton) findViewById(R.id.fabAddTodo);
        mReminderTextView = (TextView) findViewById(R.id.llAddTodoTimeReminder);
        mDateButton = (Button) findViewById(R.id.btnAddTodoDateChoose);
        mTimeButton = (Button) findViewById(R.id.btnAddTodoTimeChoose);

        mContainerLayout.setOnClickListener(this);
        mTodoSendFloatingActionButton.setOnClickListener(this);
        mDateButton.setOnClickListener(this);
        mTimeButton.setOnClickListener(this);

    }

    private void initToolBar() {
        final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
        if (cross != null) {
            cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(cross);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initTheme() {
        theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME);
        if (theme.equals(MainActivity.LIGHTTHEME)) {
            setTheme(R.style.CustomStyle_LightTheme);
        } else {
            setTheme(R.style.CustomStyle_DarkTheme);
        }
    }


    private void setDateAndTimeEditText() {
        if (mUserTodoItem.getHasReminder() && mUserReminderDate != null) {
            String userDate = DateUtil.formatDate("d MMM, yyyy", mUserReminderDate);
            String formatToUse;
            if (DateFormat.is24HourFormat(this)) {
                formatToUse = "k:mm";
            } else {
                formatToUse = "h:mm a";
            }
            String userTime = DateUtil.formatDate(formatToUse, mUserReminderDate);
            mTimeButton.setText(userTime);
            mDateButton.setText(userDate);
        } else {
            mDateButton.setText(getString(R.string.date_reminder_default));
            boolean time24 = DateFormat.is24HourFormat(this);
            Calendar calendar = Calendar.getInstance();
            if (time24) {
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            } else {
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
            }

            calendar.set(Calendar.MINUTE, 0);
            mUserReminderDate = calendar.getTime();
            Log.d(TAG, "Date" + mUserReminderDate);
            String timeString;
            if (time24) {
                timeString = DateUtil.formatDate("k:mm", mUserReminderDate);
            } else {
                timeString = DateUtil.formatDate("h:mm a", mUserReminderDate);
            }
            mTimeButton.setText(timeString);

        }
    }

    public void makeResult(int result) {
        Intent i = new Intent();
        if (mUserEnteredText.length() > 0) {
            String capitalizedString = Character.toUpperCase(mUserEnteredText.charAt(0)) + mUserEnteredText.substring(1);
            mUserTodoItem.setToDoText(capitalizedString);
        } else {
            mUserTodoItem.setToDoText(mUserEnteredText);
        }

        if (mUserReminderDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mUserReminderDate);
            calendar.set(Calendar.SECOND, 0);
            mUserReminderDate = calendar.getTime();
        }

        mUserTodoItem.setHasReminder(mUserHasReminder);
        mUserTodoItem.setToDoDate(mUserReminderDate);
        mUserTodoItem.setTodoColor(mUserColor);
        i.putExtra(MainActivity.TODOITEM, mUserTodoItem);
        setResult(result, i);
    }


    @Override
    public void onBackPressed() {
        if (mUserReminderDate.before(new Date())) {
            mUserTodoItem.setToDoDate(null);
        }
        makeResult(RESULT_OK);
        super.onBackPressed();
    }

    private void setEnterDateLayoutVisible(boolean checked) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setEnterDateLayoutVisibleWithAnimations(boolean checked) {
        if (checked) {
            setReminderTextView();
            mUserDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        } else {
            mUserDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    private void setReminderTextView() {
        if (mUserReminderDate != null) {
            mReminderTextView.setVisibility(View.VISIBLE);
            if (mUserReminderDate.before(new Date())) {
                Log.d(TAG, "Date is " + mUserReminderDate);
                mReminderTextView.setText(getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }

            Date date = mUserReminderDate;
            String dateString = DateUtil.formatDate("d MMM,yyyy", date);
            String timeString;
            String amPmString = "";

            if (DateFormat.is24HourFormat(this)) {
                timeString = DateUtil.formatDate("k:mm", date);
            } else {
                timeString = DateUtil.formatDate("h:mm", date);
                amPmString = DateUtil.formatDate("a", date);
            }

            String finalString = String.format(getResources().getString(R.string.reminder_date_and_time), dateString, timeString, amPmString);
            mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            mReminderTextView.setText(finalString);
        } else {
            mReminderTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);

    }

    private void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour, minute;
        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);
        if (reminderCalendar.before(calendar)) {
            Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        if (DateFormat.is24HourFormat(this)) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);
        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
        setDateEditText();

    }

    private void setDateEditText() {
        String dateFormat = "d MM, yyyy";
        mDateButton.setText(DateUtil.formatDate(dateFormat, mUserReminderDate));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        setTime(hourOfDay, minute);
    }

    public void setTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "Time set: " + hour);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
        setTimeEditText();
    }

    private void setTimeEditText() {
        String dateFormat;
        if (DateFormat.is24HourFormat(this)) {
            dateFormat = "k:mm";
        } else {
            dateFormat = "h:mm a";
        }
        mTimeButton.setText(DateUtil.formatDate(dateFormat, mUserReminderDate));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAddTodoDateContainer:
                hideKeyboard(mTodoTextBodyEditText);
                break;
            case R.id.fabAddTodo:
                if (mUserReminderDate != null && mUserReminderDate.before(new Date())) {
                    makeResult(RESULT_CANCELED);

                } else {
                    makeResult(RESULT_OK);

                }
                hideKeyboard(mTodoTextBodyEditText);
                finish();
                break;
            case R.id.btnAddTodoDateChoose:

                chooseAddTodoDate();

                break;
            case R.id.btnAddTodoTimeChoose:
                chooseAddTodoTime();

                break;
        }

    }

    private void chooseAddTodoDate() {
        Date date;
        hideKeyboard(mTodoTextBodyEditText);
        if (mUserTodoItem.getToDoDate() != null) {
            date = mUserReminderDate;
        } else {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddTodoActivity.this, year, month, day);
        if (theme.equals(MainActivity.DARKTHEME)) {
            datePickerDialog.setThemeDark(true);
        }
        datePickerDialog.show(getFragmentManager(), "DateFragment");

    }

    private void chooseAddTodoTime() {
        Date date;
        hideKeyboard(mTodoTextBodyEditText);
        if (mUserTodoItem.getToDoDate() != null) {
            date = mUserReminderDate;
        } else {
            date = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddTodoActivity.this, hour, minute, DateFormat.is24HourFormat(AddTodoActivity.this));
        if (theme.equals(MainActivity.DARKTHEME)) {
            timePickerDialog.setThemeDark(true);
        }
        timePickerDialog.show(getFragmentManager(), "TimeFragment");

    }
}
