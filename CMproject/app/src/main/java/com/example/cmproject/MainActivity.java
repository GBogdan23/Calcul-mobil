package com.example.cmproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private Button workoutsButton;
    private Button nutritionButton;
    private TextView textViewStepCounter;
    private SensorManager sensorManager = null;
    CircularProgressBar circularProgressBar;
    boolean isRunning = false;
    boolean isNotificationSent = false;
    int totalSteps = 0;
    int previousTotalSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workoutsButton = (Button) findViewById(R.id.button2);
        nutritionButton = (Button) findViewById(R.id.button3);
        textViewStepCounter = findViewById(R.id.TextSteps);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgressMax(20000f);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("My notification" , "My notification" , NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }


        loadData();
        resetSteps();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        workoutsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWorkoutsActivity();
            }
        });

        nutritionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNutritionActivity();
            }
        });


    }

    public void openWorkoutsActivity()
    {
        Intent intent = new Intent(this , Workouts.class);
        startActivity(intent);
    }

    public void openNutritionActivity()
    {
        Intent intent = new Intent(this , Nutrition.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepSensor != null)
        {
            sensorManager.registerListener(this , stepSensor , SensorManager.SENSOR_DELAY_UI);
        }
        else
        {
            Toast.makeText(this , "Sensor not found" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning =  false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(isRunning)
        {
            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - previousTotalSteps;
            textViewStepCounter.setText(String.valueOf(currentSteps));

            circularProgressBar.setProgressWithAnimation((float)currentSteps, 1000L);

            if(currentSteps > 20)
            {
                if(isNotificationSent == false)
                {
                    sendNotification();
                    isNotificationSent = true;
                }
            }
            else
            {
                isNotificationSent = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void resetSteps()
    {
        textViewStepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext() , "Long tap to reset" , Toast.LENGTH_SHORT).show();
            }
        });

        textViewStepCounter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                previousTotalSteps = totalSteps;
                textViewStepCounter.setText(String.valueOf(0));
                saveData();
                return true;
            }
        });

    }

    private void saveData()
    {
        SharedPreferences sharedPref = getSharedPreferences("myPrefs" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putFloat("key1" , previousTotalSteps);
        editor.apply();
    }

    private void loadData()
    {
        SharedPreferences sharedPref = getSharedPreferences("myPrefs" , Context.MODE_PRIVATE);
        float savedNumer = sharedPref.getFloat("key1", 0f);
        previousTotalSteps = (int) savedNumer;
    }

    private void sendNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this , "My notification");
        builder.setContentTitle("Congratulation!!!");
        builder.setContentText("Congratulation on reaching your step goal!");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(1 , builder.build());
    }
}