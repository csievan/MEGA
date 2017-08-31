package com.example.weitan.mega.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.models.ResultItem;
import com.kanmanus.kmutt.sit.ijoint.models.Task;
import com.kanmanus.kmutt.sit.ijoint.sensor.Orientation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class CalibrationActivity extends BaseActivity implements Orientation.Listener {
    private CalibrationTimer calibrationTimer;

    public static Intent callingIntent(Context context, Task task) {
        Intent intent = new Intent(context,CalibrationActivity.class);
        intent.putExtra(INTENT_PARAM_TASK,new Gson().toJson(task));
        return intent;
    }

    public static final String INTENT_PARAM_TASK = "INTENT_PARAM_TASK";

    private Orientation mOrientation;

    private ProgressBar progressBar;
    private TextView tvTime, tvAngle;
    private Button btnNext;

    private DecimalFormat df;

    private String tid, date, side, targetAngle, numberOfRound, isABF;
    private double calibratedAngle;

    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;
    public static final int L_BEEP = R.raw.l_beep;

    private boolean isRecording = false;
    private boolean isFinish = false;
    private ArrayList<ResultItem> resultItems;
    private String exercise_type;
    private String azimuthAngle;
    private String increase_target;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        setupUpButton();

        mOrientation = new Orientation((SensorManager) getSystemService(Activity.SENSOR_SERVICE),
                getWindow().getWindowManager());

        Intent intent = getIntent();
        String taskJson = intent.getStringExtra(INTENT_PARAM_TASK);
        task = new Gson().fromJson(taskJson,Task.class);

        tid = task.tid;
        date = task.date;
        side = task.side;
        targetAngle = task.target_angle;
        numberOfRound = task.number_of_round;
        exercise_type = task.exercise_type;
        isABF = task.is_abf;
        increase_target = task.increase_target;
        df = new DecimalFormat("0.00");

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvAngle = (TextView) findViewById(R.id.tv_angle);
        btnNext = (Button) findViewById(R.id.btn_next);

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>(1);
        soundPoolMap.put( L_BEEP, soundPool.load(getApplicationContext(), L_BEEP, 1) );

        resultItems = new ArrayList<ResultItem>();
        String side = (task.side.equals("l"))?"Left":"Right";
        int exerciseTypeRedId = R.string.task_exercise_type_extension;
        if(task.exercise_type.equals(Task.EXTENSION)){
            exerciseTypeRedId = R.string.task_exercise_type_extension;
        }
        if(task.exercise_type.equals(Task.HORIZONTAL)){
            exerciseTypeRedId = R.string.task_exercise_type_horizontal_flexion;
        }
        if(task.exercise_type.equals(Task.FLEXION)){
            exerciseTypeRedId = R.string.task_exercise_type_flexion;
        }
        String title = side + " Shoulder Exercise" + ((task.is_abf.equals("y"))?" (ABF)":"");
        String subTitle = getString(exerciseTypeRedId)+", "+task.target_angle + "\u00b0"+", Round "+task.number_of_round;
        setTitle(title);
        getSupportActionBar().setSubtitle(subTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientation.startListening(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientation.stopListening();
        if(calibrationTimer!=null)calibrationTimer.stop();

    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll, float magneticRoll) {
        //Log.d("surfaceType","surfaceType => "+surfaceType);
        if (!isFinish){
            float angle = 0;
            float supAngle = 0;
            if(Task.HORIZONTAL.equals(exercise_type)){
                angle = azimuth;
                azimuthAngle = df.format(azimuth);
                supAngle = pitch;
            }
            else if(Task.EXTENSION.equals(exercise_type)){
                angle = pitch;
                supAngle = pitch + 90 ;
            }
            else if(Task.FLEXION.equals(exercise_type)){
                supAngle = magneticRoll + 90 ;
                angle = magneticRoll;
            }
            //Log.d("Orientation","azimuth:"+azimuth+",pitch:"+pitch+",roll:"+roll) ;

            tvAngle.setText(df.format(supAngle) + "°");
            if (isRecording) {
                ResultItem resultItem = new ResultItem(tid, df.format(angle));
                resultItem.setCalibate(df.format(supAngle));
                resultItems.add(resultItem);
            }
        }
    }

    public void startCalibration(View v) {
        Button btn = (Button) v;
        btn.setVisibility(View.GONE);

        calibrationTimer =new CalibrationTimer(5000, 1000);
        calibrationTimer.start();
    }



    class CalibrationTimer {
        private long millisInFuture;
        private long countDownInterval;
        private Runnable counterRunnable;
        final Handler handler = new Handler();

        public CalibrationTimer(long pMillisInFuture, long pCountDownInterval) {
            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
        }

        public void start() {
            isRecording = true;

            counterRunnable = new Runnable(){

                public void run() {
                    if(millisInFuture <= 0) {
                        soundPool.play(soundPoolMap.get(L_BEEP), 0.6f, 0.6f, 1, 0, 1f);

                        tvTime.setText("");
                        tvAngle.setText("Complete");
                        isRecording = false;
                        isFinish = true;

                        btnNext.setVisibility(View.VISIBLE);

                        double sum = 0;
                        double sum_calibate = 0;
                        Iterator<ResultItem> iter = resultItems.iterator();
                        while (iter.hasNext()){
                            ResultItem item = iter.next();

                            sum += Double.parseDouble(item.angle);
                            sum_calibate += Double.parseDouble(item.getCalibate());
                        }
                        //calibratedAngle = "" + (sum/resultItems.size());
                        calibratedAngle = sum_calibate/resultItems.size();
                        Toast.makeText(getApplicationContext(), "The calibrated angle is " + df.format(sum_calibate/resultItems.size()), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int x = (int) (millisInFuture-1000) / 1000;
                        progressBar.setProgress(x*20);

                        tvTime.setText("00:0" + (x));

                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                    }
                }
            };

            handler.postDelayed(counterRunnable, countDownInterval);
        }
        public void stop(){
            handler.removeCallbacks(counterRunnable);
        }
    }

    public void next(View v){
        Class targetClass = PerformABFActivity.class;

        Intent i = new Intent(getApplicationContext(), targetClass);
        i.putExtra("tid", tid);
        i.putExtra("date", date);
        i.putExtra("side", side);
        i.putExtra("target_angle", targetAngle);
        i.putExtra("number_of_round", numberOfRound);
        i.putExtra("calibrated_angle", calibratedAngle);
        i.putExtra("exercise_type",exercise_type);
        i.putExtra("azimuthAngle",azimuthAngle);
        i.putExtra("increase_target",increase_target);
        i.putExtra("isABF",isABF);
        startActivity(i);
        finish();
    }
}
