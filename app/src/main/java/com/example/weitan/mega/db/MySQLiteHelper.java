package com.example.weitan.mega.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TASK = "task";
    public static final String COL_TREATMENT_NO = "treatment_no";
    public static final String COL_TASK_ID = "tid";
    public static final String COL_TASK_PID = "pid";
    public static final String COL_TASK_DATE = "date";
    public static final String COL_TASK_SIDE = "side";
    public static final String COL_TASK_TARGET_ANGLE = "target_angle";
    public static final String COL_TASK_NUMBER_OF_ROUND = "number_of_round";
    public static final String COL_TASK_IS_ABF = "is_abf";
    public static final String COL_TASK_IS_SYNCED = "is_sync";
    public static final String COL_TASK_PERFORM_DATETIME = "perform_datetime";
    public static final String COL_EXERCISE_TYPE = "exercise_type";
    public static final String COL_STATUS = "status";
    public static final String COL_SCORE = "score";
    public static final String COL_TASK_TYPE = "task_type";
    public static final String COL_INCREASE_TARGET = "increase_target";

    public static final String TABLE_RESULT_ITEM = "result_item";
    public static final String COL_RESULT_ITEM_ID = "iid";
    public static final String COL_RESULT_ITEM_TASK_ID = "tid";
    public static final String COL_RESULT_ITEM_TIME = "time";
    public static final String COL_RESULT_ITEM_ANGLE = "angle";
    public static final String COL_RESULT_ITEM_RAWANGLE = "rawAngle";
    public static final String COL_RESULT_ITEM_AZIMUTH = "azimuth";
    public static final String COL_RESULT_ITEM_PITCH = "pitch";
    public static final String COL_RESULT_ITEM_ROLL = "roll";

    private static final String DATABASE_NAME = "ijoint.db";
    private static final int DATABASE_VERSION = 9;    // last update: 06 FEB 15

    // Database creation sql statement
    private static final String DATABASE_CREATE_TASK = "create table "
            + TABLE_TASK + "(" + COL_TASK_ID + " primary key"
            + ", " + COL_TASK_PID + ", " + COL_TASK_DATE
            + ", " + COL_TASK_SIDE + ", " + COL_TASK_TARGET_ANGLE
            + ", " + COL_TASK_NUMBER_OF_ROUND + ", " + COL_TASK_IS_ABF
            + ", " + COL_TASK_IS_SYNCED + ", " + COL_TASK_PERFORM_DATETIME
            + ", " + COL_EXERCISE_TYPE + ", " + COL_STATUS + ", " + COL_SCORE
            + ", " +COL_TREATMENT_NO + ", "+COL_TASK_TYPE+ ", "+COL_INCREASE_TARGET
            + ");";

    private static final String DATABASE_CREATE_RESULT_ITEM = "create table "
            + TABLE_RESULT_ITEM + "(" + COL_RESULT_ITEM_ID + " integer primary key autoincrement"
            + ", " + COL_RESULT_ITEM_TASK_ID + ", " + COL_RESULT_ITEM_TIME
            + ", " + COL_RESULT_ITEM_ANGLE + ", " + COL_RESULT_ITEM_RAWANGLE
            + ", " + COL_RESULT_ITEM_AZIMUTH + ", " + COL_RESULT_ITEM_PITCH
            + ", " + COL_RESULT_ITEM_ROLL
            + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TASK);
        database.execSQL(DATABASE_CREATE_RESULT_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT_ITEM);
        onCreate(db);
    }

    public static void writeToSD(String DB_PATH) throws IOException {
        File sd = Environment.getExternalStorageDirectory();

        if (sd.canWrite()) {
            String currentDBPath = DATABASE_NAME;
            String backupDBPath = "backupname.db";
            File currentDB = new File(DB_PATH, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

} 
