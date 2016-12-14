package org.scir.scir_android_app;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sss.library.db.ScirFeedbackPointSqliteAdaptor;
import org.sss.library.db.ScirSqliteHelper;
import org.sss.library.exception.SssUnhandledException;

import static org.sss.library.db.ScirSqliteHelper.COLUMN_DATE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_DATETIME;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_DEVICE_ID;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_ID;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_IMAGE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_IMAGE_DIMENSION;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_IMAGE_SIZE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_LAT;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_LONG;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_PROBLEM_TYPE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_RESPONSE_ID;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_SEVERITY;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_SUBMIT_DATETIME;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_SUBMIT_RESPONSE;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_SUBMIT_STATUS;
import static org.sss.library.db.ScirSqliteHelper.COLUMN_UNIQUE_ID;

public class ScirFeedbackListActivity extends /* AppCompatActivity */ Activity {

    private ScirFeedbackPointSqliteAdaptor dbAdaptor;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scir_feedback_list);

        try {
            dbAdaptor = new ScirFeedbackPointSqliteAdaptor();
            dbAdaptor.open();
            displayListView();
        } catch(SssUnhandledException e) {
            e.printStackTrace();
            Log.e("SCIR_FeedbkListActivity", e.getStackTrace().toString());
        }

    }


    private void displayListView() {
            Cursor cursor = dbAdaptor.fetchAllFeedbacks();


            // create the adapter using the cursor pointing to the desired data
            //as well as the layout information
            dataAdapter = new ImageCursorAdapter(
                    this, R.layout.content_scir_feedback_list,
                    cursor,
                    ScirSqliteHelper.scirSqlTableFields,
                    ScirSqliteHelper.scirSqlTableFieldsMappingToXML
                    );

            ListView listView = (ListView) findViewById(R.id.listView1);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);


/*
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
*/

    }

    class ImageCursorAdapter extends SimpleCursorAdapter {
        private Cursor c;
        private Context context;

        public ImageCursorAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.c = c;
            this.context = context;
        }

        public View getView(int pos, View inView, ViewGroup parent) {
            View v = inView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.content_scir_feedback_list, null);
            }
            this.c.moveToPosition(pos);
            String colId    = this.c.getString(this.c.getColumnIndex(COLUMN_ID));
            String colReportedDateReadable  = this.c.getString(this.c.getColumnIndex(COLUMN_DATE));
            String colDeviceId = this.c.getString(this.c.getColumnIndex(COLUMN_DEVICE_ID));
            String colUniqueId = this.c.getString(this.c.getColumnIndex(COLUMN_UNIQUE_ID));
            String colProblemType = this.c.getString(this.c.getColumnIndex(COLUMN_PROBLEM_TYPE));
            String colSeverity = this.c.getString(this.c.getColumnIndex(COLUMN_SEVERITY));
            String colImageDimension = this.c.getString(this.c.getColumnIndex(COLUMN_IMAGE_DIMENSION));
            long colImageSize = this.c.getLong(this.c.getColumnIndex(COLUMN_IMAGE_SIZE));
            float colLatitude = this.c.getFloat(this.c.getColumnIndex(COLUMN_LAT));
            float colLongitude = this.c.getFloat(this.c.getColumnIndex(COLUMN_LONG));
            byte [] colImage = this.c.getBlob(this.c.getColumnIndex(COLUMN_IMAGE));

            String colSubmitStatus = this.c.getString(this.c.getColumnIndex(COLUMN_SUBMIT_STATUS));
            String colSubmitResponse = this.c.getString(this.c.getColumnIndex(COLUMN_SUBMIT_RESPONSE));
            long colSubmitDatetime = this.c.getLong(this.c.getColumnIndex(COLUMN_SUBMIT_DATETIME));
            String colResponseId = this.c.getString(this.c.getColumnIndex(COLUMN_RESPONSE_ID));
            long colReportedDatetime = this.c.getLong(this.c.getColumnIndex(COLUMN_DATETIME));

            if (colImage != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.imgViewFeedback);
                iv.setImageBitmap(BitmapFactory.decodeByteArray(colImage, 0, colImage.length));
            }
            TextView tId = (TextView) v.findViewById(R.id.id);
            TextView tReportedDateReadable = (TextView) v.findViewById(R.id.date);
            TextView tDeviceId = (TextView) v.findViewById(R.id.deviceId);
            TextView tUniqueId = (TextView) v.findViewById(R.id.uniqueId);
            TextView tLat = (TextView) v.findViewById(R.id.latitude);
            TextView tLong = (TextView) v.findViewById(R.id.longitude);
            TextView tProblemType =  (TextView) v.findViewById(R.id.problemType);
            TextView tProblemSeverity = (TextView) v.findViewById(R.id.problemSeverity);
            TextView tImageDimension = (TextView) v.findViewById(R.id.imageDimension);
            TextView tImageSize = (TextView) v.findViewById(R.id.imageSize);

            TextView tSubmitStatus = (TextView) v.findViewById(R.id.submitStatus);
            TextView tSubmitResponse = (TextView) v.findViewById(R.id.submitResponse);
            TextView tSubmitDatetime = (TextView) v.findViewById(R.id.submitDatetime);
            TextView tResponseId = (TextView) v.findViewById(R.id.responseId);
            TextView tProblemReportDatetime = (TextView) v.findViewById(R.id.problemReportDatetime);

            tId.setText( "Id:" + colId);

            tSubmitStatus.setText("(Submit Status and acceptance datetime" + colSubmitStatus );
            tSubmitDatetime.setText( ", " + colSubmitDatetime + ")" );

            tImageSize.setText(Long.toString(colImageSize));
            tImageDimension.setText("#" + colImageDimension + "#" );

            tDeviceId.setText("(" + colDeviceId);
            tUniqueId.setText("," + colUniqueId + ")");

            tLat.setText("(Lat,Long)=(" + Float.toString(colLatitude));
            tLong.setText(", " + Float.toString(colLongitude) + ")" );

            tProblemType.setText("(Issue Type, Severity) = (" + colProblemType);
            tProblemSeverity.setText(", " + colSeverity + ")" );

            tResponseId.setText("(Resp Id, desc)=" + colResponseId);
            tSubmitResponse.setText(", " + colSubmitResponse);

            tProblemReportDatetime.setText("(Rep. datetime/date" + colReportedDatetime);
            tReportedDateReadable.setText(", " + colReportedDateReadable + ")");

            return(v);
        }
    }


}
