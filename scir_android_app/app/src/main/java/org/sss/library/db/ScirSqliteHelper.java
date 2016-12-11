package org.sss.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by khelender on 09-12-2016.
 */


public class ScirSqliteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
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

    SQLiteDatabase dbFeedback ;


    public ScirSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                     int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        dbFeedback = getWritableDatabase();
        dbFeedback.close();
    }

    public void open() {
        getReadableDatabase();
    }

    public static void setupSqliteDatabase(Context context) {
        ScirSqliteHelper dbSqlite = new ScirSqliteHelper(context, "FeedbackDatabase", null,1);

//        dbSqlite.getWritableDatabase();
//        dbSqlite.insertMessage("DDDD", "S13545", "2016-12-09", 3.5f, 4.6f, 55300, "Sanity", "High");
//        dbSqlite.insertSomeRecords();
        dbSqlite.close();
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
            + COLUMN_IMAGE + " BLOB"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("ScirSqliteHelper", "Creating database");
        db.execSQL(query);

//        insertMessage("DDDD", "S131345", "2016-12-09", 3.5f, 4.6f, 5500, "Sanity", "High");
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
                                 byte[] baCOLUMN_IMAGE
                                 ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_ID,12345);
        contentValues.put(COLUMN_DATE, sCOLUMN_DATE);
        contentValues.put(COLUMN_DEVICE_ID, sCOLUMN_DEVICE_ID);
        contentValues.put(COLUMN_PROBLEM_TYPE, sCOLUMN_PROBLEM_TYPE);
        contentValues.put(COLUMN_LAT,fCOLUMN_LAT);
        contentValues.put(COLUMN_LONG, fCOLUMN_LONG);
        contentValues.put(COLUMN_IMAGE_SIZE, lCOLUMN_IMAGE_SIZE);
        contentValues.put(COLUMN_SEVERITY, sCOLUMN_SEVERITY);
        contentValues.put(COLUMN_UNIQUE_ID, sCOLUMN_UNIQUE_ID);
        contentValues.put(COLUMN_IMAGE, baCOLUMN_IMAGE);
        db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return true ;
    }

    public void insertSomeRecords() {
        insertMessage("DDDD", "S131345", "2016-12-09", 3.5f, 4.6f, 5500, "Sanity", "High", null);
        insertMessage("DDDD", "S135415", "2016-12-09", 3.5f, 4.6f, 55300, "Road", "High", null);
        insertMessage("DDDD", "S123542", "2016-12-11", 3.5f, 4.6f, 55600, "Electricity", "High", null);
        insertMessage("DDDD", "S135455", "2016-12-12", 3.5f, 4.6f, 15500, "Road", "High", null);
        insertMessage("DDDD", "S153545", "2016-12-09", 3.5f, 4.6f, 335500, "Sanity", "High", null);
    }


}

