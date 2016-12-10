package org.scir.scir_android_app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.Toast;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;

import org.sss.library.db.ScirFeedbackPointSqliteAdaptor;
import org.sss.library.db.ScirSqliteHelper;

public class ScirFeedbackListActivity extends /* AppCompatActivity */ Activity {
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


    private ScirSqliteHelper dbHelper ;
    private ScirFeedbackPointSqliteAdaptor dbAdaptor ;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scir_feedback_list);

//        dbHelper = new ScirSqliteHelper(getApplicationContext(), "FeedbackList", null, 1);
//        dbHelper.open();

        dbAdaptor = new ScirFeedbackPointSqliteAdaptor(getApplicationContext());
        dbAdaptor.open();
        displayListView() ;

    }


    private void displayListView() {
        Cursor cursor = dbAdaptor.fetchAllFeedbacks();
        String[] columns = new String []{
            COLUMN_ID, COLUMN_DATE, COLUMN_DEVICE_ID, COLUMN_LAT,
                COLUMN_LONG, COLUMN_PROBLEM_TYPE, COLUMN_SEVERITY, COLUMN_UNIQUE_ID,
                COLUMN_IMAGE_SIZE
        };


        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.id,
                R.id.date,
                R.id.deviceId,
                R.id.latitude,
                R.id.longitude,
                R.id.problemType,
                R.id.problemSeverity,
                R.id.uniqueId,
                R.id.imageSize
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.content_scir_feedback_list,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String dateValue =
                        cursor.getString(cursor.getColumnIndexOrThrow("date"));
                Toast.makeText(getApplicationContext(),
                        dateValue, Toast.LENGTH_SHORT).show();
            }
        });

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbAdaptor.fetchMessagesByDate(constraint.toString());
            }
        });

    }


}
