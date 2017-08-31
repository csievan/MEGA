package com.example.weitan.mega.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.db.ResultItemDataSource;
import com.kanmanus.kmutt.sit.ijoint.db.TaskDataSource;
import com.kanmanus.kmutt.sit.ijoint.models.ResultItem;
import com.kanmanus.kmutt.sit.ijoint.models.Task;
import com.kanmanus.kmutt.sit.ijoint.net.HttpManager;
import com.kanmanus.kmutt.sit.ijoint.sensor.Orientation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class PerformABFActivity extends Activity implements Orientation.Listener {

    private TaskDataSource taskDataSource;
    private ResultItemDataSource resultItemDataSource;
    private ArrayList<ResultItem> resultItems;

    private Orientation mOrientation;
    private TextView tvSide, tvTargetAngle, tvNumberOfRound, tvTime, tvAngle;
    private LinearLayout uploadingLayout;
    private DecimalFormat df;

    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;
    public static final int S_BEEP = R.raw.s_beep;
    public static final int ALMOST = R.raw.almost;
    public static final int GREAT = R.raw.great;
    public static final int COUNT1 = R.raw.count_001;
    public static final int COUNT2 = R.raw.count_002;
    public static final int COUNT3 = R.raw.count_003;
    public static final int COUNT4 = R.raw.count_004;
    public static final int COUNT5 = R.raw.count_005;
    public static final int COUNT6 = R.raw.count_006;
    public static final int COUNT7 = R.raw.count_007;
    public static final int COUNT8 = R.raw.count_008;
    public static final int COUNT9 = R.raw.count_009;
    public static final int COUNT10 = R.raw.count_010;
    public static final int COUNT11 = R.raw.count_011;
    public static final int COUNT12 = R.raw.count_012;
    public static final int COUNT13 = R.raw.count_013;
    public static final int COUNT14 = R.raw.count_014;
    public static final int COUNT15 = R.raw.count_015;
    public static final int COUNT16 = R.raw.count_016;
    public static final int COUNT17 = R.raw.count_017;
    public static final int COUNT18 = R.raw.count_018;
    public static final int COUNT19 = R.raw.count_019;
    public static final int COUNT20 = R.raw.count_020;

    private String tid, side, targetAngle, numberOfRound;
    private double calibratedAngle;

    private boolean isRecording = false;
    private long begin;
    private String performDateTime;
    private float tempAngle = 0;
    private int score = 0;
    private double targetInc = 0;
    private double targetTmp = 0;
    private double tmp = 0 ;
    private String exercise_type;
    private String azimuthAngle;
    private String pitchAngle;
    private String rollAngle;
    private String isABF;
    private String YES = "y";
    private String NO = "n";
    private String LEFT = "l";
    private String EXERCISE_STATE = "1";
    private String EXERCISE_START = "1";
    private String EXERCISE_TARGET_80 = "2";
    private String EXERCISE_SUCCESS = "3";
    private String EXERCISE_INCREASE = "4";
    private Task taskObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        targetInc = Double.parseDouble(intent.getStringExtra("increase_target"));
        String date = intent.getStringExtra("date");
        side = intent.getStringExtra("side");
        targetAngle = intent.getStringExtra("target_angle");
        targetTmp = Double.parseDouble(targetAngle);
        tmp = targetTmp;
        numberOfRound = intent.getStringExtra("number_of_round");
        calibratedAngle = intent.getDoubleExtra("calibrated_angle",0);
        exercise_type = intent.getStringExtra("exercise_type");
        azimuthAngle = intent.getStringExtra("azimuthAngle");
        isABF = intent.getStringExtra("isABF");
        mOrientation = new Orientation((SensorManager) getSystemService(Activity.SENSOR_SERVICE),
                getWindow().getWindowManager());

        TextView tvHeadLine = (TextView) findViewById(R.id.headline);
        String title = "Perform Task";
        if(isABF.equals(YES)){
            title = "Perform ABF Task";
        }
        Log.d("ABF","calibratedAngle = "+calibratedAngle);
        tvHeadLine.setText(title);

        tvSide = (TextView) findViewById(R.id.side_value);
        tvTargetAngle = (TextView) findViewById(R.id.target_angle_value);
        tvNumberOfRound = (TextView) findViewById(R.id.number_of_round_value);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvAngle = (TextView) findViewById(R.id.tv_angle);

        uploadingLayout = (LinearLayout) findViewById(R.id.uploadingLayout);

        tvSide.setText((side.equals("l")?"Left":"Right"));
        tvTargetAngle.setText(targetTmp + "°");
        tvNumberOfRound.setText(score + "/" + numberOfRound);

        df = new DecimalFormat("0.00");

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>(1);
        soundPoolMap.put( S_BEEP, soundPool.load(getApplicationContext(), S_BEEP, 1) );
        soundPoolMap.put( ALMOST, soundPool.load(getApplicationContext(), ALMOST, 1) );
        soundPoolMap.put( GREAT, soundPool.load(getApplicationContext(), GREAT, 1) );
        soundPoolMap.put( COUNT1, soundPool.load(getApplicationContext(), COUNT1, 1) );
        soundPoolMap.put( COUNT2, soundPool.load(getApplicationContext(), COUNT2, 1) );
        soundPoolMap.put( COUNT3, soundPool.load(getApplicationContext(), COUNT3, 1) );
        soundPoolMap.put( COUNT4, soundPool.load(getApplicationContext(), COUNT4, 1) );
        soundPoolMap.put( COUNT5, soundPool.load(getApplicationContext(), COUNT5, 1) );
        soundPoolMap.put( COUNT6, soundPool.load(getApplicationContext(), COUNT6, 1) );
        soundPoolMap.put( COUNT7, soundPool.load(getApplicationContext(), COUNT7, 1) );
        soundPoolMap.put( COUNT8, soundPool.load(getApplicationContext(), COUNT8, 1) );
        soundPoolMap.put( COUNT9, soundPool.load(getApplicationContext(), COUNT9, 1) );
        soundPoolMap.put( COUNT10, soundPool.load(getApplicationContext(), COUNT10, 1) );
        soundPoolMap.put( COUNT11, soundPool.load(getApplicationContext(), COUNT11, 1) );
        soundPoolMap.put( COUNT12, soundPool.load(getApplicationContext(), COUNT12, 1) );
        soundPoolMap.put( COUNT13, soundPool.load(getApplicationContext(), COUNT13, 1) );
        soundPoolMap.put( COUNT14, soundPool.load(getApplicationContext(), COUNT14, 1) );
        soundPoolMap.put( COUNT15, soundPool.load(getApplicationContext(), COUNT15, 1) );
        soundPoolMap.put( COUNT16, soundPool.load(getApplicationContext(), COUNT16, 1) );
        soundPoolMap.put( COUNT17, soundPool.load(getApplicationContext(), COUNT17, 1) );
        soundPoolMap.put( COUNT18, soundPool.load(getApplicationContext(), COUNT18, 1) );
        soundPoolMap.put( COUNT19, soundPool.load(getApplicationContext(), COUNT19, 1) );
        soundPoolMap.put( COUNT20, soundPool.load(getApplicationContext(), COUNT20, 1) );

        taskDataSource = new TaskDataSource(getApplicationContext());
        taskDataSource.open();

        resultItemDataSource = new ResultItemDataSource(getApplicationContext());
        resultItemDataSource.open();

        resultItems = new ArrayList<ResultItem>();
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
    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll,float magRoll) {
        double angle = 0;
        double angleTv = 0;
        if(Task.HORIZONTAL.equals(exercise_type)){
            float temp = azimuth;
            if (!side.equals(LEFT)) {
                if(Math.abs(tempAngle - azimuth) > 270 && tempAngle != 0){
                    temp -= 360;
                }else{
                    tempAngle = azimuth;
                }
                if(tempAngle != 0){
                    angle = Double.parseDouble(azimuthAngle) - temp;
                    angleTv = Double.parseDouble(azimuthAngle) - temp;
                }
            }else{
                if(Math.abs(tempAngle - azimuth) > 270 && tempAngle != 0){
                    temp += 360;
                }else{
                    tempAngle = azimuth;
                }
                if(tempAngle != 0){
                    angle = temp - Double.parseDouble(azimuthAngle);
                    angleTv = temp - Double.parseDouble(azimuthAngle);
                }
            }
        }
        else if(Task.EXTENSION.equals(exercise_type)){
            angle = pitch + 90;
            angleTv = pitch + 90;
        }

        else if(Task.FLEXION.equals(exercise_type)){
                angle = magRoll + 90;
                angleTv = magRoll + 90;
        }

        String azimuthStr = "" + df.format(azimuth);
        String pitchStr = "" + df.format(pitch);
        String rollStr = "" + df.format(roll);
        String rawAngleStr = "" + df.format(angle);


        String angleStr = df.format(angle);
        tvAngle.setText(df.format(angleTv)+ "°");
        if (isRecording) {
            long current = System.currentTimeMillis();
            String time = "" + (current - begin);
            Date date = new Date(current-begin);
            DateFormat formatter = new SimpleDateFormat("mm:ss");
            String dateFormatted = formatter.format(date);

            tvTime.setText(dateFormatted);

            ResultItem item = resultItemDataSource.create(tid, time, angleStr, rawAngleStr, azimuthStr, pitchStr, rollStr);
            resultItems.add(item);
            if( Double.parseDouble(angleStr)<270){
                currentState(EXERCISE_STATE, Double.parseDouble(angleStr), Double.parseDouble(targetAngle));
            }
        }
    }
    public void currentState(String state , double angle, double target){
        if(target >0){
            positiveTarget(state,angle,target);
        }else{
            negativeTarget(state,angle,target);
        }
    }
    public void positiveTarget(String state , double angle, double target){
        tvTargetAngle.setText(targetTmp + "°");
        if(state.equals(EXERCISE_START) && angle > target*0.8){
            if(isABF.equals(YES)){
                soundPool.play(soundPoolMap.get(ALMOST), 0.6f, 0.6f, 1, 0, 1f);
            }
            EXERCISE_STATE = EXERCISE_TARGET_80;
        }
        if(state.equals(EXERCISE_TARGET_80) && angle > target){
            if(isABF.equals(YES) && score == 0){
                soundPool.play(soundPoolMap.get(S_BEEP), 0.6f, 0.6f, 1, 0, 1f);
                soundPool.play(soundPoolMap.get(GREAT), 0.6f, 0.6f, 1, 0, 1f);
            }
            if(angle > targetTmp) {
                increaseTarget(180);
            }
            EXERCISE_STATE = EXERCISE_SUCCESS;
        }
        if(state.equals(EXERCISE_SUCCESS) && angle > targetTmp){
            if(isABF.equals(YES) && score != 0){
                soundPool.play(soundPoolMap.get(S_BEEP), 0.6f, 0.6f, 1, 0, 1f);
                soundPool.play(soundPoolMap.get(GREAT), 0.6f, 0.6f, 1, 0, 1f);
            }
            increaseTarget(180);
        }
        if (state.equals(EXERCISE_SUCCESS) && angle <= calibratedAngle) {
            score++;
            tvNumberOfRound.setText(score + "/" + numberOfRound);
            if(isABF.equals(YES)){
                soundPoolCount(score);
            }
            if(targetTmp == tmp){
                decreaseTarget(30);
            }
            tmp = targetTmp;
            EXERCISE_STATE = EXERCISE_START;
        }
    }
    public void increaseTarget(double max){
        targetTmp +=targetInc;
        if(targetTmp >max){
            targetTmp = max;
        }
    }
    public void decreaseTarget(double min){
        targetTmp -=targetInc;
        if(targetTmp <min){
            targetTmp = min;
        }
    }
    public void negativeTarget(String state , double angle, double target){
        tvTargetAngle.setText(targetTmp + "°");
        if(state.equals(EXERCISE_START) && angle < target*0.8){
            if(isABF.equals(YES)){
                soundPool.play(soundPoolMap.get(ALMOST), 0.6f, 0.6f, 1, 0, 1f);
            }
            EXERCISE_STATE = EXERCISE_TARGET_80;
        }
        if(state.equals(EXERCISE_TARGET_80) && angle < target){
            if(isABF.equals(YES) && score == 0){
                soundPool.play(soundPoolMap.get(S_BEEP), 0.6f, 0.6f, 1, 0, 1f);
                soundPool.play(soundPoolMap.get(GREAT), 0.6f, 0.6f, 1, 0, 1f);
            }
            if(angle < targetTmp){
                decreaseTarget(-45);
            }
            EXERCISE_STATE = EXERCISE_SUCCESS;
        }
        if(state.equals(EXERCISE_SUCCESS) && angle < targetTmp){
            if(isABF.equals(YES) && score != 0){
                soundPool.play(soundPoolMap.get(S_BEEP), 0.6f, 0.6f, 1, 0, 1f);
                soundPool.play(soundPoolMap.get(GREAT), 0.6f, 0.6f, 1, 0, 1f);
            }
            decreaseTarget(-45);
        }
        if (state.equals(EXERCISE_SUCCESS) && angle > (calibratedAngle*-1)) {
            score++;
            tvNumberOfRound.setText(score + "/" + numberOfRound);
            if(isABF.equals(YES)){
                soundPoolCount(score);
            }
            if(targetTmp == tmp){
                increaseTarget(-5);
            }
            tmp = targetTmp;
            EXERCISE_STATE = EXERCISE_START;
        }
    }
    public void soundPoolCount(int score){
        try{
            int [] listSound = {COUNT1,COUNT2,COUNT3,COUNT4,COUNT5,COUNT6,COUNT7,COUNT8,COUNT9,COUNT10
                    ,COUNT11,COUNT12,COUNT13,COUNT14,COUNT15,COUNT16,COUNT17,COUNT18,COUNT19,COUNT20};
            soundPool.play(soundPoolMap.get(listSound[score-1]), 0.6f, 0.6f, 1, 0, 1f);
        }catch (Exception e){
            e.getStackTrace();
        }
    }
    public boolean checkExerciseType( double angleCheck){
        boolean chk = true;
        if(Task.FLEXION.equals(exercise_type)){
            if(angleCheck <  - 3 || angleCheck > + 3){
                chk = false;
            }
        }else{
            if(angleCheck <  - 9 || angleCheck > + 9){// error exercise type for input error 5 percent of around (360 degree)
                chk = false;
            }
        }
        return chk;
    }
    public void startExercise(View v){
        Button btn = (Button) v;

        if (!isRecording){  // Start Exercise
            Date cDate = new Date();
            performDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cDate);
            EXERCISE_STATE = EXERCISE_START;
            isRecording = true;
            btn.setBackgroundResource(R.drawable.btn_stop);
            btn.setText("Stop Exercise");

            begin = System.currentTimeMillis();
        }
        else{   // Stop Exercise
            isRecording = false;
            btn.setVisibility(View.GONE);
            tvAngle.setVisibility(View.GONE);
            uploadingLayout.setVisibility(View.VISIBLE);

            taskDataSource.updateIsSynced(tid, "f");
            taskDataSource.updateIsScore(tid, ""+score);
            taskDataSource.updatePerformDateTime(tid, performDateTime);

            new UploadToWeb().execute();
        }
    }

    private class UploadToWeb extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (haveNetworkConnection())
                saveToWebDB();

            return "Executed";
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String result) {
            if (haveNetworkConnection())
                Toast.makeText(getApplicationContext(), "Your data is saved and sycned to the web site.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "No Internet Connection. Your data is saved but it has not been synced to the web site yet.", Toast.LENGTH_SHORT).show();

            goBackToHomePage();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        public void saveToWebDB(){
            JSONArray resultJSONArray = new JSONArray();

            Iterator<ResultItem> iter = resultItems.iterator();
            while (iter.hasNext()){
                ResultItem resultItem = iter.next();
                resultJSONArray.put(resultItem.getJSONObject());
            }

            JSONObject json = new JSONObject();

            try {
                json.put("score", score);
                json.put("perform_datetime", performDateTime);
                json.put("result", resultJSONArray);

                HttpManager.getInstance().getService().uploadResultItems(json.toString()).execute();
                taskDataSource.updateIsSynced(tid, "y");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void goBackToHomePage(){
        finish();
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null;
    }
}
