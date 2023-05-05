package com.example.cmproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Workouts extends AppCompatActivity {
    private Button Workout;
    private Button AddExercice;
    private Button ResetWorkout;
    private Button PauseWorkout;
    EditText ExerciseName;
    EditText ExerciseTime;
    RelativeLayout parent;
    TextView timeTextView;
    TextView exerciseTextView;
    CountDownTimer mCountDownTimer;
    PopupWindow popupWindow;
    long mTimeLeftInMillis;
    int stage = 1;
    boolean isCounterRunning = false;
    int button;
    String[] ExercisesNames;
    long[] ExercisesTimes;
    int indexExercise = 0;
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        ExercisesNames = new String[20];
        ExercisesTimes = new long[20];

        Workout = (Button) findViewById(R.id.button);
        AddExercice = (Button) findViewById(R.id.button4);
        ResetWorkout = (Button) findViewById(R.id.button5);

        ExerciseName = (EditText) findViewById(R.id.ExerciceNameText);
        ExerciseTime = (EditText) findViewById(R.id.ExerciceTimeText);

        parent = findViewById(R.id.parent_layout);

        Workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopup();
            }
        });

        AddExercice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputName;
                String inputTimeTxt;
                long inputTime;

                inputName = ExerciseName.getText().toString();
                inputTimeTxt = ExerciseTime.getText().toString();

                if (inputName.length() == 0)
                {
                    Toast.makeText(Workouts.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(inputTimeTxt.length() == 0)
                {
                    Toast.makeText(Workouts.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                inputTime = Long.parseLong(inputTimeTxt) * 1000;
                if(inputTime == 0)
                {
                    Toast.makeText(Workouts.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    ExercisesNames[indexExercise] = inputName;
                    ExercisesTimes[indexExercise] = inputTime;
                    indexExercise++;
                    Toast.makeText(Workouts.this, "Exercice was added", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ResetWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Arrays.fill(ExercisesNames , null);
                Arrays.fill(ExercisesTimes , 0);
                indexExercise = 0;
                Toast.makeText(Workouts.this, "Workouts Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void createPopup()
    {
        View view = View.inflate(this , R.layout.workouts_popup , null);

        popupWindow = new PopupWindow(view , 900 , 1500 , false );

        popupWindow.showAtLocation(parent ,Gravity.CENTER , 0 ,0);

        timeTextView = (TextView) popupWindow.getContentView().findViewById(R.id.textView2);
        exerciseTextView = (TextView) popupWindow.getContentView().findViewById(R.id.textView);
        PauseWorkout = (Button) popupWindow.getContentView().findViewById(R.id.buttonPause);

        PauseWorkout.setOnClickListener(new View.OnClickListener()
        {
        @Override
            public void onClick(View view) {
                if(isCounterRunning == true)
                {
                    pauseTimer();
                    PauseWorkout.setText("START");
                }
                else
                {
                    startTimer();
                    PauseWorkout.setText("PAUSE");
                }
            }
        });

        TimerHandler(index);

    }

    private void pauseTimer()
    {
        mCountDownTimer.cancel();
        isCounterRunning = false;
        updateCountDownText();
    }
    private void TimerHandler(int index)
    {
        exerciseTextView.setText(ExercisesNames[index]);
        mTimeLeftInMillis = ExercisesTimes[index];
        startTimer();
    }

    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isCounterRunning = false;
                if(index < indexExercise + 1 )
                {
                    index++;
                    TimerHandler(index);
                }
                else
                {
                    index = 0;
                    popupWindow.dismiss();
                }

            }
        }.start();

        isCounterRunning = true;
    }

    private void updateCountDownText() {
        String timeLeftFormatted;

        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds);

        timeTextView.setText(timeLeftFormatted);
    }
}