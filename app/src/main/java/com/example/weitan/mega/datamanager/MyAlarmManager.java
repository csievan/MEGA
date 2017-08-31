package com.example.weitan.mega.datamanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kanmanus.kmutt.sit.ijoint.Contextor;
import com.kanmanus.kmutt.sit.ijoint.receiver.AlarmReceiver;

import java.util.Calendar;

/**
 * Created by Semicolon07 on 7/6/2016 AD.
 */

public class MyAlarmManager {
    static MyAlarmManager INSTANCE;
    private final Context mContext;
    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private static final String TAG = "MyAlarmManager";


    public static MyAlarmManager getInstance(){
        if (INSTANCE == null)
            INSTANCE = new MyAlarmManager();
        return INSTANCE;
    }

    private MyAlarmManager(){
        mContext = Contextor.getInstance().getContext();
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE) ;
    }

    public void setExerciseNotificationTime(int hour,int minute,String summary){
        if(alarmManager != null && pendingIntent!=null)
            alarmManager.cancel(pendingIntent);

        alarmIntent = new Intent(mContext, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long diff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
        if(diff>0)
            calendar.add(Calendar.DAY_OF_MONTH,1);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        Log.d(TAG,"Set next time = "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+", diff = "+diff);
    }
}
