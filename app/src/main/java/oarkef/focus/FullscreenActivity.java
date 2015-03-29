package oarkef.focus;

import oarkef.focus.util.IO;
import oarkef.focus.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

    private Task task = new Task();
    private CountDownTimer countdown;
    private TextView full_screen;
    private IO task_storage = new IO();

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.new_button).setOnTouchListener(mDelayHideTouchListener);

        full_screen = (TextView) findViewById(R.id.fullscreen_content);

        try {
            if (task.fromString(task_storage.loadNextDeadlineFromFile(getApplicationContext())) == true) {
                restartCounter();
            }
        } catch (IOException exc) {
            task.description = "Unable to load from file. Check your settings.";
        } catch (ParseException exc) {
            task.description = "Could not parse the stored file. Reset it to blank.";
            deleteFile(IO.getMainFileName());
            deleteFile(IO.getTemporaryFilename());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void newEvent(View view)
    {
        Intent intent = new Intent(this, InsertDataIntoNew.class);
        startActivityForResult(intent, 0);
    }

    public void deleteEvent(View view)
    {
        try {
            task_storage.deleteSpecificEntry(getApplicationContext(), task.toString());
            if (task.fromString(task_storage.loadNextDeadlineFromFile(getApplicationContext())) == false) {
                countdown.cancel();
                full_screen.setText("Focus");
                task.finish_time = null;
            } else {
                restartCounter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopCounterIfItExists() {
        if (countdown != null)
            countdown.cancel();
    }

    private void restartCounter()
    {
        if (countdown != null)
            countdown.cancel();
        countdown = new CountDownTimer(task.finish_time.getTimeInMillis() - java.util.Calendar.getInstance().getTimeInMillis(), 1000)
        {
            public void onTick(long millis_until_finished)
            {
                long secs = millis_until_finished / 1000;
                long seconds = secs % 60;
                long mins = secs / 60;
                long minutes = mins % 60;
                long hours = mins / 60;
                full_screen.setText(task.description + "\n\n" + hours + ":" + String.format("%02d:%02d\nRemaining", minutes, seconds));
            }

            public void onFinish()
            {
                full_screen.setText(
                    task.description
                    + "\n\n"
                    + task.finish_time.get(Calendar.YEAR)
                    + "/" + task.finish_time.get(Calendar.MONTH) + 1
                    + "/" + task.finish_time.get(Calendar.DAY_OF_MONTH)
                    + String.format(
                        "\n%02d:%02d:%02d\nOverdue",
                        task.finish_time.get(Calendar.HOUR_OF_DAY),
                        task.finish_time.get(Calendar.MINUTE),
                        task.finish_time.get(Calendar.SECOND)
                    )
                );
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data)
    {
        if (result_code == RESULT_CANCELED)
            return;

        java.util.Calendar calendar = (java.util.Calendar) data.getSerializableExtra("Date");
        String description = data.getStringExtra("Description").replace("\n", " ");

        if (task.finish_time == null || calendar.before(task.finish_time) /*the event is closer than the current, store the current and load this event instead*/ )
        {
            if (task.finish_time != null) {
                try {
                    task_storage.saveTask(getApplicationContext(), task.toString());
                } catch (IOException exc) {
                    stopCounterIfItExists();
                    task.description = "Unable to store to file. Check your settings.";
                }
            }
            task.finish_time = calendar;
            task.description = description;
            try {
                task_storage.saveTask(getApplicationContext(), task.toString());
            } catch (IOException exc) {
                stopCounterIfItExists();
                task.description = "Unable to store the new event to file. Check your settings.";
            }
            restartCounter();
        }
        else /*Store the event in the database(just a simple file)*/
        {
            Task temp_task = new Task();
            temp_task.finish_time = calendar;
            temp_task.description = description;
            try {
                task_storage.saveTask(getApplicationContext(), temp_task.toString());
            } catch (IOException exc) {
                stopCounterIfItExists();
                task.description = "Unable to store the new event to file. Check your settings.";
            }
        }
    }
}
