package org.sss.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.scir.scir_android_app.R;
import org.sss.library.exception.SssUnhandledException;

/**
 * Created by khelender on 09-12-2016.
 */


public class ScirSqliteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "scirMsgs.db";
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
    public static final String COLUMN_IMAGE = "image" ;
    public static final String COLUMN_IMAGE_DIMENSION = "image_dimension";
    public static final String COLUMN_SUBMIT_STATUS = "submit_status";
    public static final String COLUMN_SUBMIT_RESPONSE = "submit_response";
    public static final String COLUMN_RESPONSE_ID = "submit_response_id";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_SUBMIT_DATETIME = "response_datetime";

    public static String [] scirSqlTableFields = new String []
            {
                            COLUMN_ID, COLUMN_DATE, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY,
                            COLUMN_DEVICE_ID, COLUMN_UNIQUE_ID, COLUMN_LAT,COLUMN_LONG,
                            COLUMN_IMAGE_SIZE, COLUMN_IMAGE_DIMENSION, COLUMN_IMAGE,
                            COLUMN_SUBMIT_STATUS, COLUMN_SUBMIT_RESPONSE, COLUMN_RESPONSE_ID,
                            COLUMN_DATETIME, COLUMN_SUBMIT_DATETIME
            };

    public static int [] scirSqlTableFieldsMappingToXML = new int []
            {
                // the XML defined views which the data will be bound to
                R.id.id, R.id.date, R.id.deviceId, R.id.uniqueId,
                R.id.latitude, R.id.longitude, R.id.problemType, R.id.problemSeverity,
                R.id.imageSize, R.id.imageDimension, R.id.image,
                R.id.submitStatus, R.id.submitResponse, R.id.submitDatetime,
                R.id.problemReportDatetime, R.id.responseId
            };
    /*
                            new String [] {
                                COLUMN_ID, COLUMN_DATE, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY,
                                COLUMN_DEVICE_ID, COLUMN_UNIQUE_ID, COLUMN_LAT,COLUMN_LONG,
                                COLUMN_IMAGE_SIZE, COLUMN_IMAGE_DIMENSION, COLUMN_IMAGE,
                                COLUMN_SUBMIT_STATUS, COLUMN_SUBMIT_RESPONSE, COLUMN_RESPONSE_ID,
                                COLUMN_DATETIME, COLUMN_SUBMIT_DATETIME
                        },
                    new String [] {COLUMN_ID, COLUMN_DATE, COLUMN_DEVICE_ID, COLUMN_ID, COLUMN_LAT,
                            COLUMN_LONG, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY, COLUMN_UNIQUE_ID,
                            COLUMN_IMAGE_SIZE, COLUMN_IMAGE_DIMENSION, COLUMN_IMAGE},

     */

    private static ScirSqliteHelper mScirSqliteHelper = null ;
    private SQLiteDatabase dbFeedback ;

    public static ScirSqliteHelper getScirSqliteHelper() throws SssUnhandledException {
        if( mScirSqliteHelper  == null ) {
            throw new SssUnhandledException("ScirSqliteHelper has not been initialized");
        } else {
            return mScirSqliteHelper;
        }
    }

    public static void initScirSqliteHelper(Context context) {
        mScirSqliteHelper = new ScirSqliteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private ScirSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbFeedback = getWritableDatabase();
        dbFeedback.close();
    }

    public void open() {
        getReadableDatabase();
    }

    String query = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DATE + " Text, "
            + COLUMN_PROBLEM_TYPE + " Text ,"
            + COLUMN_SEVERITY + " Text ,"
            + COLUMN_DEVICE_ID + " Text ,"
            + COLUMN_UNIQUE_ID + " Text, "
            + COLUMN_LAT + " Float, "
            + COLUMN_LONG + " Float, "
            + COLUMN_IMAGE_SIZE + " Long, "
            + COLUMN_IMAGE_DIMENSION + " Text, "

            + COLUMN_SUBMIT_STATUS + " Text,"
            + COLUMN_SUBMIT_RESPONSE + " Text,"
            + COLUMN_SUBMIT_DATETIME + " Long,"
            + COLUMN_RESPONSE_ID + " Text,"
            + COLUMN_DATETIME + " Long,"

            + COLUMN_IMAGE + " BLOB"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("ScirSqliteHelper", "Creating database");
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    public boolean insertMessage(String sCOLUMN_DEVICE_ID,
                                 String sCOLUMN_UNIQUE_ID,
                                 String sCOLUMN_DATE,
                                 float fCOLUMN_LAT,
                                 float fCOLUMN_LONG,
                                 long lCOLUMN_IMAGE_SIZE,
                                 String sCOLUMN_PROBLEM_TYPE,
                                 String sCOLUMN_SEVERITY,
                                 String sCOLUMN_IMAGE_DIMENSION,

                                 String sCOLUMN_SUBMIT_STATUS,
                                 String sCOLUMN_SUBMIT_RESPONSE,
                                 long lCOLUMN_SUBMIT_DATETIME,
                                 String sCOLUMN_RESPONSE_ID,
                                 long lCOLUMN_DATETIME,

                                 byte[] baCOLUMN_IMAGE
                                 ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, sCOLUMN_DATE);
        contentValues.put(COLUMN_DEVICE_ID, sCOLUMN_DEVICE_ID);
        contentValues.put(COLUMN_PROBLEM_TYPE, sCOLUMN_PROBLEM_TYPE);
        contentValues.put(COLUMN_LAT,fCOLUMN_LAT);
        contentValues.put(COLUMN_LONG, fCOLUMN_LONG);
        contentValues.put(COLUMN_IMAGE_SIZE, lCOLUMN_IMAGE_SIZE);
        contentValues.put(COLUMN_SEVERITY, sCOLUMN_SEVERITY);
        contentValues.put(COLUMN_UNIQUE_ID, sCOLUMN_UNIQUE_ID);
        contentValues.put(COLUMN_IMAGE_DIMENSION, sCOLUMN_IMAGE_DIMENSION);
        contentValues.put(COLUMN_IMAGE, baCOLUMN_IMAGE);

        contentValues.put(COLUMN_SUBMIT_STATUS, sCOLUMN_SUBMIT_STATUS);
        contentValues.put(COLUMN_SUBMIT_RESPONSE, sCOLUMN_SUBMIT_RESPONSE);
        contentValues.put(COLUMN_SUBMIT_DATETIME, lCOLUMN_SUBMIT_DATETIME);
        contentValues.put(COLUMN_RESPONSE_ID, sCOLUMN_RESPONSE_ID);
        contentValues.put(COLUMN_DATETIME, lCOLUMN_DATETIME);

        db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return true ;
    }

    public void insertSomeRecords() {
        insertMessage("DDDD", "S131345", "2016-12-09", 3.5f, 4.6f, 5500, "Sanity", "High", "", "", "", 4567L, "", 123L, null);
    }


}

