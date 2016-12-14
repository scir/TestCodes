package org.sss.library.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.sss.library.exception.SssUnhandledException;

import static org.sss.library.db.ScirSqliteHelper.COLUMN_DATE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_DEVICE_ID;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_ID;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_IMAGE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_IMAGE_DIMENSION;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_IMAGE_SIZE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_LAT;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_LONG;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_PROBLEM_TYPE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_SEVERITY;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_UNIQUE_ID;
import static org.sss.library.db.ScirSqliteHelper.DATABASE_VERSION;
import static org.sss.library.db.ScirSqliteHelper.TABLE_USERS;

/**
 * Created by khelender on 09-12-2016.
 */

public class ScirFeedbackPointSqliteAdaptor {

    private ScirSqliteHelper mDbHelper;
    private SQLiteDatabase mDb;

    public ScirFeedbackPointSqliteAdaptor() throws SssUnhandledException {
        mDbHelper = ScirSqliteHelper.getScirSqliteHelper();
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
                                COLUMN_IMAGE_SIZE, COLUMN_IMAGE_DIMENSION, COLUMN_IMAGE},
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
                            COLUMN_IMAGE_SIZE, COLUMN_IMAGE_DIMENSION, COLUMN_IMAGE},
                    null, null, null, null, null);
        }
        else {
            mCursor = mDb.query(true, TABLE_USERS,
                    new String [] {COLUMN_ID, COLUMN_DATE, COLUMN_DEVICE_ID, COLUMN_ID, COLUMN_LAT,
                            COLUMN_LONG, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY, COLUMN_UNIQUE_ID,
                            COLUMN_IMAGE_SIZE, COLUMN_IMAGE_DIMENSION, COLUMN_IMAGE},
                    COLUMN_DATE + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


}
