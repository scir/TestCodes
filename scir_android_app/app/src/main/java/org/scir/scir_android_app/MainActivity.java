package org.scir.scir_android_app;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int TAKE_PICTURE_REQUEST_B = 100;

    private ImageView mCameraImageView;
    private Bitmap mCameraBitmap;
    private Button mSaveImageButton;

    private Intent mIntentReportProblems = null ;
    private Intent mIntentLocationCheck = null ;

    ImageView imgLogo ;
    ImageView imgSmartCityPhoto ;

    private LocationManager scirLocationManager ;
    public static SCIRLocationFinder mScirLocationFinder ;

    private SingleShotLocationProvider.LocationCallback mScirLocationCallBack;
    public static Location mScirCurrentLocation ;


    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startImageCapture();
        }
    };

    private OnClickListener mSaveImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            File saveFile = openFileForImage();
            if (saveFile != null) {
                saveImageToFile(saveFile);
            } else {
                Toast.makeText(MainActivity.this, "Unable to open file for saving image.",
                        Toast.LENGTH_LONG).show();
            }
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
                        Toast.makeText(getBaseContext(),"my location is " + location.toString(), Toast.LENGTH_LONG).show();
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

        setupLocationServices();

        mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

        findViewById(R.id.capture_image_button).setOnClickListener(mCaptureImageButtonClickListener);

        mSaveImageButton = (Button) findViewById(R.id.save_image_button);
//        mSaveImageButton.setOnClickListener(mSaveImageButtonClickListener);
//        mSaveImageButton.setEnabled(false);
        mSaveImageButton.setOnClickListener(mLocationCheckButtonClickListener);
        mSaveImageButton.setEnabled(true);

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
                    mSaveImageButton.setEnabled(true);
                }
            } else {
                mCameraBitmap = null;
                mSaveImageButton.setEnabled(false);
            }
        }
    }

    private void startImageCapture() {
        startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), TAKE_PICTURE_REQUEST_B);
    }

    private File openFileForImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "com.oreillyschool.android2.camera");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }

    private void saveImageToFile(File file) {
        if (mCameraBitmap != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                if (!mCameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)) {
                    Toast.makeText(MainActivity.this, "Unable to save image to file.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Saved image to: " + file.getPath(),
                            Toast.LENGTH_LONG).show();
                }
                outStream.close();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Unable to save image to file.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}