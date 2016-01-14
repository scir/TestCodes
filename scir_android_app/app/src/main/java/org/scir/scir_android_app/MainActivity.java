package org.scir.scir_android_app;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Functionalities to be added / enhanced :
 * 1) Location should be predicted accurately in a speedy manner using Location Services (out of speed and accuracy, ACCURACY has higher priority.
 */

public class MainActivity extends Activity {

    private static final int TAKE_PICTURE_REQUEST_B = 100;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private Button mViewReportedProblemsButton;
    private Button mLocationServicesButton ;

    private TextView mTextViewGeoLocationStatus;

    private Intent mIntentReportProblems = null ;
    private Intent mIntentLocationCheck = null ;

    ImageView imgLogo ;
    ImageView imgSmartCityPhoto ;

    private LocationManager scirLocationManager ;
    public static SCIRLocationFinder mScirLocationFinder ;
    private SingleShotLocationProvider.LocationCallback mScirLocationCallBack;
    public static Location mScirCurrentLocation ;


    private OnClickListener mCaptureInfraProblemButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startInfraProblemCapture();
        }
    };

    private void setupLocationServices() {
        try {
            int method = 2 ;
            if( method == 2 ) {
                mScirLocationCallBack =
                    new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(Location location) {
                        mScirCurrentLocation = location ;
                        Log.d("Location", "my location is " + location.toString());
                        Toast.makeText(getBaseContext(),"my location is " + location.toString(), Toast.LENGTH_SHORT).show();
                        if( mTextViewGeoLocationStatus != null ) {
                            mTextViewGeoLocationStatus.setTextColor(Color.rgb(0, 255, 0));
                            mTextViewGeoLocationStatus.setText("GOT GeoLocation! Please proceed...");
                        }
                    }};
                SingleShotLocationProvider.requestSingleUpdate(this.getBaseContext(), mScirLocationCallBack);
            } else {
                /*
                // final LocationManager locationManager = (LocationManager) Context.getSystemService(Context.LOCATION_SERVICE);
                scirLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mScirLocationFinder = new SCIRLocationFinder();
                scirLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mScirLocationFinder);
                mScirLocationFinder.updateBestLocation(scirLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                // mScirLocationFinder.updateBestLocation(scirLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                if (mScirLocationFinder.isLocationQualityGood() ) {
                    // Can go ahead with capturing of photo quality...
                } else {
                    // Need to tell user to wait for capturing problem
                }
                */
            }
        } catch(SecurityException se) {
            Toast.makeText(MainActivity.this, "Unable to setup Security Exception.....", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, "Unable to setup Location Services possibly.....", Toast.LENGTH_LONG).show();
        }
    }



    private OnClickListener mLocationCheckButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIntentLocationCheck == null ) {
                mIntentLocationCheck = new Intent(MainActivity.this, CheckLocationActivity.class);
            }
            if( mIntentLocationCheck != null ) {
                startActivityForResult(mIntentLocationCheck, TAKE_PICTURE_REQUEST_B);
            } else {
                // TODO : Show errro alert

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Prepare Status text view */
        mTextViewGeoLocationStatus = (TextView) findViewById(R.id.textViewGeoLocationStatus);
        mTextViewGeoLocationStatus.setTextColor(Color.rgb(255,0,0));

        setupLocationServices();

        /* Need to check view contents of final image view from camera activity */
        mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

        /* Set buttons Views */
        findViewById(R.id.capture_infra_problem_button).setOnClickListener(mCaptureInfraProblemButtonClickListener);

//        mLocationServicesButton = (Button) findViewById(R.id.check_problems_reported_button);
        mLocationServicesButton = (Button) findViewById(R.id.check_location_services_button);
        mLocationServicesButton.setOnClickListener(mLocationCheckButtonClickListener);
        mLocationServicesButton.setEnabled(true);

        mViewReportedProblemsButton = (Button) findViewById(R.id.check_problems_reported_button);

        /* Show Startup Images for Main Activity */
        imgLogo = (ImageView) findViewById(R.id.imageView);
        imgLogo.setImageResource(R.drawable.scir_image);
        imgSmartCityPhoto = (ImageView) findViewById(R.id.imageView2);
        imgSmartCityPhoto.setImageResource(R.drawable.creative_and_smart_city);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST_B) {
            if (resultCode == RESULT_OK) {
                // Recycle the previous bitmap.
                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }
                Bundle extras = data.getExtras();
 //               mCameraBitmap = (Bitmap) extras.get("data");
                byte[] cameraData = extras.getByteArray(CameraActivity.EXTRA_CAMERA_DATA);
                if (cameraData != null) {
                    mCameraBitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);
                    mCameraImageView.setImageBitmap(mCameraBitmap);
                    mViewReportedProblemsButton.setEnabled(true);
                }
            } else {
                mCameraBitmap = null;
                mViewReportedProblemsButton.setEnabled(false);
            }
        }
    }

    private void startInfraProblemCapture() {
        startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
    }

}