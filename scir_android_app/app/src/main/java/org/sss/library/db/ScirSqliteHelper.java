package org.sss.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.sss.library.exception.SssUnhandledException;

/**
 * Created by khelender on 09-12-2016.
 */


public class ScirSqliteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
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
        db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return true ;
    }

    public void insertSomeRecords() {
        insertMessage("DDDD", "S131345", "2016-12-09", 3.5f, 4.6f, 5500, "Sanity", "High", "", null);
    }


}

