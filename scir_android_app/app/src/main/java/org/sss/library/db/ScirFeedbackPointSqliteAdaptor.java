package org.sss.library.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by khelender on 09-12-2016.
 */

public class ScirFeedbackPointSqliteAdaptor {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "scirMsgs.db";
    public static final String TABLE_USERS = "msgs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PROBLEM_TYPE = "problem_type";
    public static final String COLUMN_SEVERITY = "severity";
    public static final String COLUMN_DEVICE_ID = "device_id";
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_IMAGE_SIZE = "image_size";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";

    private ScirSqliteHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mContext ;

    public ScirFeedbackPointSqliteAdaptor(Context ctx) {
        this.mContext = ctx;
        mDbHelper = new ScirSqliteHelper(mContext,"FeedbackDatabase",null, DATABASE_VERSION);
    }

    public ScirSqliteHelper open() throws SQLException {
        mDb = mDbHelper.getReadableDatabase();
        return mDbHelper;
    }

    public Cursor fetchAllFeedbacks() {
        Cursor mCursor =
                mDb.query(TABLE_USERS,
                        new String [] {COLUMN_ID, COLUMN_DATE, COLUMN_DEVICE_ID, COLUMN_ID, COLUMN_LAT,
                                COLUMN_LONG, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY, COLUMN_UNIQUE_ID,
                                COLUMN_IMAGE_SIZE},
                        null, null, null, null, null);
        if( mCursor != null ) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public static final String TAG = "FeedbackSqlAdaptor";
    public Cursor fetchMessagesByDate(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(TABLE_USERS,
                    new String [] {COLUMN_ID, COLUMN_DATE, COLUMN_DEVICE_ID, COLUMN_ID, COLUMN_LAT,
                            COLUMN_LONG, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY, COLUMN_UNIQUE_ID,
                            COLUMN_IMAGE_SIZE},
                    null, null, null, null, null);
        }
        else {
            mCursor = mDb.query(true, TABLE_USERS,
                    new String [] {COLUMN_ID, COLUMN_DATE, COLUMN_DEVICE_ID, COLUMN_ID, COLUMN_LAT,
                            COLUMN_LONG, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY, COLUMN_UNIQUE_ID, COLUMN_IMAGE_SIZE},
                    COLUMN_DATE + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


}
