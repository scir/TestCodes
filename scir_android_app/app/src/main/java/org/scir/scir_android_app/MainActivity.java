package org.scir.scir_android_app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sss.library.SssPreferences;
import org.sss.library.db.ScirSqliteHelper;
import org.sss.library.location.SingleShotLocationProvider;

import java.util.List;


/**
 * TODO: Functionalities to be added / enhanced :
 * 1) Location should be predicted accurately in a speedy manner using Location Services (out of speed and accuracy, ACCURACY has higher priority.
 * 2) Size of image captured should be small enough (of order of less than 100K)
 */

public class MainActivity extends Activity {

    private static final int START_INTENT_CAMERA = 100;
    private static final int START_INTENT_FEEDBACK_DATA = 101;
    private static final int START_INTENT_PREFERENCES = 102 ;
    private static final int START_INTENT_LOCATION_ACTIVITY = 103 ;

    public static final int RESULT_EXIT_CALLED = 200 ;
    public static final int RESULT_RESTART_INTENT_CAMERA = 201 ;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private Button mCaptureInfraProblemButton;
    private Button mViewReportedProblemsButton;
    private Button mLocationServicesButton ;
    private Button mPreferencesButton;

    private TextView mTextViewGeoLocationStatus;

    private Intent mIntentReportProblems = null ;
    private Intent mIntentLocationCheck = null ;
    private Intent mIntentFeedbackData = null ;
    private Intent mIntentPreferences = null ;

    ImageView imgLogo ;
    ImageView imgSmartCityPhoto ;

    private SingleShotLocationProvider.LocationCallback mScirLocationCallBack;

    private Runnable mRegularLocationUpdater = null ;
    private android.os.Handler mLocationHandler = null ;
    public static Location mScirCurrentLocation ;

    private SssPreferences sssPreferences ;



    static enum SCIR_LOCATION_SERVICE {
        SCIR_LOCATION_SERVICE_INVALID,
        SCIR_LOCATION_SERVICE_COMPLETE_LOCATION,
        SCIR_LOCATION_SERVICE_ONE_SHOT_LOCATION
    };
    SCIR_LOCATION_SERVICE mScirLocationService = SCIR_LOCATION_SERVICE.SCIR_LOCATION_SERVICE_ONE_SHOT_LOCATION;

    private void setupLocationServices() {
        try {
            switch( mScirLocationService ) {
                case SCIR_LOCATION_SERVICE_COMPLETE_LOCATION:
                    // Method # 1
                    // To be enabled in future after proper take care....
                    startLocationActivity();
                    break;
                case SCIR_LOCATION_SERVICE_ONE_SHOT_LOCATION:
                case SCIR_LOCATION_SERVICE_INVALID:
                default:
                    mScirLocationCallBack =
                            new SingleShotLocationProvider.LocationCallback() {
                                @Override public void onNewLocationAvailable(Location location) {
                                    mScirCurrentLocation = location ;
                                    String strLocation = String.format("(%.3f, %.3f)",location.getLatitude(), location.getLongitude());
                                    Log.i("Location", "my location is " + location.toString());
//                                    Toast.makeText(getBaseContext(),"my location is " + location.toString(), Toast.LENGTH_SHORT).show();
                                    if( mTextViewGeoLocationStatus != null ) {
                                        mTextViewGeoLocationStatus.setTextColor(Color.rgb(0, 255, 128));
                                        mTextViewGeoLocationStatus.setText("Geo " + strLocation + ". Capture photo.");
                                    }
                                }};
                    mLocationHandler = new android.os.Handler();
                    mRegularLocationUpdater = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SingleShotLocationProvider.requestSingleUpdate(getBaseContext(), mScirLocationCallBack);
                            } finally {
                                // Reschedule it for repeat here !!
                                // Future fetches programming through Timer : 12 seconds
                                mLocationHandler.postDelayed(mRegularLocationUpdater, 12*1000);
                            }
                        }
                    };
                    mLocationHandler.postDelayed(mRegularLocationUpdater, 0);
                    break ;

            }
        } catch(SecurityException se) {
            Toast.makeText(MainActivity.this, "Unable to setup Security Exception.....", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, "Unable to setup Location Services possibly.....", Toast.LENGTH_LONG).show();
        }
    }

    void initializeApplicationEnvironment() {
        // For setup sqlite database
        ScirSqliteHelper.initScirSqliteHelper(getApplicationContext());
        // Update initial level of preferences from Default Shared
        SettingsActivity.GeneralPreferenceFragment.updateAllCameraParams();
    }

    private void startInfraProblemCapture() {
        if (null == mIntentReportProblems) {
//            mIntentReportProblems = new Intent(MainActivity.this, CameraActivity.class);
            mIntentReportProblems = new Intent(MainActivity.this, Camera2Activity.class);
        }
        if( mIntentReportProblems  != null ) {
            startActivityForResult(mIntentReportProblems, START_INTENT_CAMERA);
        } else {
            Toast.makeText(getApplicationContext(),"Can't launch Feedback through Camera2 Activity", Toast.LENGTH_LONG).show();
        }
    }

    private void startFeedbackListActivity () {
        if (null == mIntentFeedbackData) {
            mIntentFeedbackData = new Intent(MainActivity.this, ScirFeedbackListActivity.class);
        }
        if( mIntentFeedbackData  != null ) {
            startActivityForResult(mIntentFeedbackData, START_INTENT_FEEDBACK_DATA);
        } else {
            Toast.makeText(getApplicationContext(),"Can't launch Feedback Activity", Toast.LENGTH_LONG).show();
        }
    }


    private void startLocationActivity() {
        if (mIntentLocationCheck == null ) {
            mIntentLocationCheck = new Intent(MainActivity.this, CheckLocationActivity.class);
        }
        if( mIntentLocationCheck != null ) {
            startActivityForResult(mIntentLocationCheck, START_INTENT_LOCATION_ACTIVITY);
        } else {
            Toast.makeText(getApplicationContext(),"Can't launch LOCATION Activity", Toast.LENGTH_LONG).show();
        }
    }

    private void startPreferencesActivity() {
        if (null == mIntentPreferences) {
            mIntentPreferences = new Intent(MainActivity.this, SettingsActivity.class);
        }
        if( mIntentPreferences!= null ) {
            startActivityForResult(mIntentPreferences, START_INTENT_PREFERENCES);
        } else {
            Toast.makeText(getApplicationContext(),"Can't launch Preferences Activity", Toast.LENGTH_LONG).show();
        }
    }



    private OnClickListener mCaptureInfraProblemButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startInfraProblemCapture();
        }
    };
    private OnClickListener mViewReportedProblemsButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startFeedbackListActivity();
        }
    };
    private OnClickListener mPreferencesButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startPreferencesActivity();
        }
    };
    private OnClickListener mLocationCheckButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startLocationActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sssPreferences = SssPreferences.getSssPreferences(getApplicationContext());

        /* Prepare Status text view */
        mTextViewGeoLocationStatus = (TextView) findViewById(R.id.textViewGeoLocationStatus);
        mTextViewGeoLocationStatus.setTextColor(Color.rgb(255,0,0));
        /* Show Startup Images for Main Activity */
        imgLogo = (ImageView) findViewById(R.id.imageView);
        imgLogo.setImageResource(R.drawable.scir_image);
        imgSmartCityPhoto = (ImageView) findViewById(R.id.imageView2);
        imgSmartCityPhoto.setImageResource(R.drawable.creative_and_smart_city);

        setupLocationServices();

        /* Need to check view contents of final image view from camera activity */
        mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

        /* Set buttons Views */
        mCaptureInfraProblemButton = (Button)findViewById(R.id.capture_infra_problem_button);
        mCaptureInfraProblemButton.setOnClickListener(mCaptureInfraProblemButtonClickListener);

        mViewReportedProblemsButton = (Button) findViewById(R.id.check_problems_reported_button);
        mViewReportedProblemsButton.setOnClickListener(mViewReportedProblemsButtonClickListener);
        mViewReportedProblemsButton.setEnabled(true);

        mPreferencesButton = (Button) findViewById(R.id.check_location_services_button);
        mPreferencesButton.setOnClickListener(mPreferencesButtonClickListener);
        mPreferencesButton.setEnabled(true);

        // TODO : Enable if required
//        mLocationServicesButton = (Button) findViewById(R.id.check_location_services_button);
//        mLocationServicesButton.setOnClickListener(mLocationCheckButtonClickListener);
//        mLocationServicesButton.setEnabled(true);

        initializeApplicationEnvironment();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_INTENT_CAMERA) {
            if (resultCode == RESULT_OK) {
                // Recycle the previous bitmap.
                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }
                Bundle extras = data.getExtras();
                byte[] cameraData = extras.getByteArray(CameraActivity.EXTRA_CAMERA_DATA);
                if (cameraData != null) {
                    mCameraBitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);
                    mCameraImageView.setImageBitmap(mCameraBitmap);
                }
            } else {
                mCameraBitmap = null;
            }
        } else if( requestCode == START_INTENT_FEEDBACK_DATA) {
        }
    }


}