package com.example.weitan.mega.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kanmanus.kmutt.sit.ijoint.models.ResultItem;

import java.util.ArrayList;
import java.util.List;

public class ResultItemDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	private String TABLE = MySQLiteHelper.TABLE_RESULT_ITEM;
	private String PRIMARY_KEY = MySQLiteHelper.COL_RESULT_ITEM_ID;

	private String[] allColumns = { MySQLiteHelper.COL_RESULT_ITEM_ID,
			MySQLiteHelper.COL_RESULT_ITEM_TASK_ID, MySQLiteHelper.COL_RESULT_ITEM_TIME,
			MySQLiteHelper.COL_RESULT_ITEM_ANGLE, MySQLiteHelper.COL_RESULT_ITEM_RAWANGLE,
            MySQLiteHelper.COL_RESULT_ITEM_AZIMUTH, MySQLiteHelper.COL_RESULT_ITEM_PITCH,
            MySQLiteHelper.COL_RESULT_ITEM_ROLL};

	public ResultItemDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}

	public void close() {
	    dbHelper.close();
	}

    public ResultItem create(String tid, String time, String angle, String rawAngle, String azimuth, String pitch, String roll) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COL_RESULT_ITEM_TASK_ID, tid);
        values.put(MySQLiteHelper.COL_RESULT_ITEM_TIME, time);
        values.put(MySQLiteHelper.COL_RESULT_ITEM_ANGLE, angle);
        values.put(MySQLiteHelper.COL_RESULT_ITEM_RAWANGLE, rawAngle);
        values.put(MySQLiteHelper.COL_RESULT_ITEM_AZIMUTH, azimuth);
        values.put(MySQLiteHelper.COL_RESULT_ITEM_PITCH, pitch);
        values.put(MySQLiteHelper.COL_RESULT_ITEM_ROLL, roll);

        long insertId = database.insert(TABLE, null, values);
        Cursor cursor = database.query(TABLE,
            allColumns, PRIMARY_KEY + " = " + insertId, null,
            null, null, null);
        cursor.moveToFirst();

        ResultItem newElement = cursorToElement(cursor);
        cursor.close();

        return newElement;
    }

    public void edit(long dbId, String tid, String time, String angle, String rawAngle, String azimuth, String pitch, String roll) {

        database.execSQL("UPDATE " + TABLE + " SET "
            + MySQLiteHelper.COL_RESULT_ITEM_TASK_ID + " = '" + tid + "', "
            + MySQLiteHelper.COL_RESULT_ITEM_TIME + " = '" + time + "', "
            + MySQLiteHelper.COL_RESULT_ITEM_ANGLE + " = '" + angle + "', "
            + MySQLiteHelper.COL_RESULT_ITEM_RAWANGLE + " = '" + rawAngle + "', "
            + MySQLiteHelper.COL_RESULT_ITEM_AZIMUTH + " = '" + azimuth + "', "
            + MySQLiteHelper.COL_RESULT_ITEM_PITCH + " = '" + pitch+ "', "
            + MySQLiteHelper.COL_RESULT_ITEM_ROLL + " = '" + roll + "'"
            + " WHERE " + PRIMARY_KEY + " = " + dbId);
    }

    public void delete(ResultItem elem) {
        long dbId = elem.dbId;

        database.delete(TABLE, PRIMARY_KEY + " = " + dbId, null);
    }

    public List<ResultItem> getAll() {
        List<ResultItem> elems = new ArrayList<ResultItem>();

        Cursor cursor = database.query(TABLE, allColumns, null, null, null, null, PRIMARY_KEY + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ResultItem elem = cursorToElement(cursor);
            elems.add(elem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return elems;
    }

    public ResultItem get(long id){
      Cursor cursor = database.query(TABLE, allColumns,
              PRIMARY_KEY + " = " + id,
              null, null, null, null);

      cursor.moveToFirst();
      if (!cursor.isAfterLast()){
          ResultItem elem = cursorToElement(cursor);

          return elem;
      }
      else
          return null;
    }

    public List<ResultItem> getByTid(String tid){
        List<ResultItem> elems = new ArrayList<ResultItem>();

        Cursor cursor = database.query(TABLE, allColumns,
                MySQLiteHelper.COL_RESULT_ITEM_TASK_ID + " = '" + tid +"'",
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ResultItem elem = cursorToElement(cursor);
            elems.add(elem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return elems;
    }

    public void delete(long id){
      database.execSQL("delete from " + TABLE + " where _id = " + id);
    }

    private ResultItem cursorToElement(Cursor cursor) {
        ResultItem elem = new ResultItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)
                                            , cursor.getString(5), cursor.getString(6), cursor.getString(7));

        return elem;
    }
}
