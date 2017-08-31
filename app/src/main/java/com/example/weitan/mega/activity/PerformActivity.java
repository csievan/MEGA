package com.example.weitan.mega.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kanmanus.kmutt.sit.ijoint.R;
import com.kanmanus.kmutt.sit.ijoint.db.ResultItemDataSource;
import com.kanmanus.kmutt.sit.ijoint.db.TaskDataSource;
import com.kanmanus.kmutt.sit.ijoint.models.ResultItem;
import com.kanmanus.kmutt.sit.ijoint.net.HttpManager;
import com.kanmanus.kmutt.sit.ijoint.sensor.Orientation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class PerformActivity extends Activity implements Orientation.Listener {

    private TaskDataSource taskDataSource;
    private ResultItemDataSource resultItemDataSource;
    private ArrayList<ResultItem> resultItems;

    private Orientation mOrientation;
    private TextView tvSide, tvTargetAngle, tvNumberOfRound, tvTime, tvAngle;
    private LinearLayout uploadingLayout;
    private DecimalFormat df;

    private String tid, side, calibratedAngle;

    private boolean isRecording = false;
    private long begin;
    private String performDateTime;
    private String exercise_type;
    private String azimuthAngle;
    private String pitchAngle;
    private String rollAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        mOrientation = new Orientation((SensorManager) getSystemService(Activity.SENSOR_SERVICE),
                getWindow().getWindowManager());

        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        String date = intent.getStringExtra("date");
        side = intent.getStringExtra("side");
        String targetAngle = intent.getStringExtra("target_angle");
        String numberOfRound = intent.getStringExtra("number_of_round");
        calibratedAngle = intent.getStringExtra("calibrated_angle");
        exercise_type = intent.getStringExtra("exercise_type");
        azimuthAngle = intent.getStringExtra("azimuthAngle");
        pitchAngle = intent.getStringExtra("pitchAngle");
        rollAngle = intent.getStringExtra("rollAngle");

        tvSide = (TextView) findViewById(R.id.side_value);
        tvTargetAngle = (TextView) findViewById(R.id.target_angle_value);
        tvNumberOfRound = (TextView) findViewById(R.id.number_of_round_value);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvAngle = (TextView) findViewById(R.id.tv_angle);

        uploadingLayout = (LinearLayout) findViewById(R.id.uploadingLayout);

        tvSide.setText((side.equals("l")?"Left":"Right"));
        tvTargetAngle.setText(targetAngle + "°");
        tvNumberOfRound.setText(numberOfRound);

        df = new DecimalFormat("0.00");

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
    public void onOrientationChanged(float azimuth, float pitch, float roll, float surfaceType) {
        double angle = roll;

        if (side.equals("l"))
            angle = -angle;

        String azimuthStr = "" + df.format(azimuth);
        String pitchStr = "" + df.format(pitch);
        String rollStr = "" + df.format(roll);
        String rawAngleStr = "" + df.format(angle);

        angle -= Double.parseDouble(calibratedAngle);

        tvAngle.setText(df.format(angle) + "°");

        if (isRecording){
            long current = System.currentTimeMillis();
            String time = "" + (current - begin);

            Date date = new Date(current-begin);
            DateFormat formatter = new SimpleDateFormat("mm:ss");
            String dateFormatted = formatter.format(date);

            tvTime.setText(dateFormatted);

            // store tid / time / angle into result item
            ResultItem resultItem = resultItemDataSource.create(tid, time, df.format(angle), rawAngleStr, azimuthStr, pitchStr, rollStr);
            resultItems.add(resultItem);
        }
    }

    public void startExercise(View v){
        Button btn = (Button) v;

        if (!isRecording){  // Start Exercise
            Date cDate = new Date();
            performDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cDate);

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
                json.put("score", "-1");
                json.put("perform_datetime", performDateTime);
                json.put("result", resultJSONArray);
                HttpManager.getInstance().getService().uploadResultItems(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void goBackToHomePage(){
        Intent i = new Intent(getApplicationContext(), TasksActivity.class);
        startActivity(i);
        finish();
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null;
    }
}
