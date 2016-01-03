package org.scir.scir_android_app;

import android.os.Bundle;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;

// import org.scir.scir_android_app.SCIRLocationFinder ;

public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    public static final String EXTRA_CAMERA_DATA = "camera_data";
    private static final String KEY_IS_CAPTURING = "is_capturing";

    private Camera mCamera;
    private ImageView mCameraImage;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private byte[] mCameraData;
    private boolean mIsCapturing;


    private RadioGroup mScirCtlRadioGroupProblemType;
    private RatingBar mScirCtlProblemSeverityRating ;
    private SeekBar mScirCtlSeveritySeekBar ;
    private Button mScirCtlButtonSubmitFeedback;

    public enum SCIR_PROBLEM_TYPE {
        E_SCIR_WATER,
        E_SCIR_ELECTRICITY ,
        E_SCIR_ROAD,
        E_SCIR_SANITATION ,
        E_SCIR_POLLUTION ,
        E_SCIR_OTHER
    };

    private float mScirDataLat, mScirDataLong, mScirDataDateTime ;
    private float mScirDataProblemSeverityLevel;
    private SCIR_PROBLEM_TYPE mScirDataProblemType ;


    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    private OnClickListener mRecaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
        }
    };

    private void SaveData() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_data");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000, offset = 0;
        int number = generator.nextInt(n);
        String fname = "Image-"+ number + ".dat";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();

        String strLocation = "" ;
        try {
            strLocation = String.format("Latitude(%.3f)\nLongitude(%.3f)\nEpoch(%tc)\n",
                    /*
                    MainActivity.mScirLocationFinder.getmScirDataLatitude(), MainActivity.mScirLocationFinder.getmScirDataLongitude(),
                    MainActivity.mScirLocationFinder.getmScirDataDatetime());
                    */

                    MainActivity.mScirCurrentLocation.getLatitude(), MainActivity.mScirCurrentLocation.getLongitude(),
                    MainActivity.mScirCurrentLocation.getTime());
            /*
            strLocation = String.format("Latitude(%.3f)\nLongitude(%.3f)\n",
                    (float) MainActivity.mScirLocationFinder.getmScirDataLatitude(),
                    (float) MainActivity.mScirLocationFinder.getmScirDataLongitude()
            );
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String myData = "Saved Data :\n";
            FileOutputStream out = new FileOutputStream(file);
            myData = String.format("Saved Data :\nSeverity Level : %.3f\nProblem Type : %s\n",
                    mScirDataProblemSeverityLevel,
                    mScirDataProblemType.toString()
                    );
            myData = strLocation.concat(myData);
            out.write(myData.getBytes(), offset, myData.length());
            offset += myData.length();
            out.flush();
            out.close();

            if( mCameraData != null ) {
                String fnameImage = "Image-"+ number +".jpg";
                File fileImage = new File (myDir, fnameImage);
                if (fileImage.exists ()) fileImage.delete ();
                FileOutputStream outImage = new FileOutputStream(fileImage);
                outImage.write(mCameraData, 0, mCameraData.length);
                outImage.flush();
                outImage.close();
                myData = myData.concat("Image Saved : YES");
            }

            Toast.makeText(CameraActivity.this, myData, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private OnClickListener mScirFeedbackButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCameraData != null) {
                // TODO : This is final processing stage of all submitted contents
                SaveData();
                /*
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CAMERA_DATA, mCameraData);
                setResult(RESULT_OK, intent);
                setResult(RESULT_OK);
                */
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    };

    private RadioGroup.OnCheckedChangeListener mScirProblemTypeGroupChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId) {
                case R.id.scirCtrlRadioSelectElectricity:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_ELECTRICITY;
                    break ;
                case R.id.scirCtrlRadioSelectWater:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_WATER;
                    break ;
                case R.id.scirCtrlRadioSelectPollution:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_POLLUTION;
                    break ;
                case R.id.scirCtrlRadioSelectRoad:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_ROAD;
                    break ;
                default:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_OTHER;
                    break ;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mScirSeverityLevelSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private RatingBar.OnRatingBarChangeListener mScirSeverityLevelRatingBarListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            /* Set rating level */
            mScirDataProblemSeverityLevel = rating ;
        }
    };


    private void setupScirEnvProblemCapturing() {
            mScirCtlRadioGroupProblemType = (RadioGroup) findViewById(R.id.scirCtrlRadioGroupProblemType);
            mScirCtlProblemSeverityRating = (RatingBar) findViewById(R.id.scirCtrlRatingBar);
            mScirCtlButtonSubmitFeedback = (Button) findViewById(R.id.scirCtrlButtonFeedback);
            mScirCtlSeveritySeekBar = (SeekBar) findViewById(R.id.scirCtrlSeekBar);

            // TODO: Remove any default settings selection to provide true picture to end user
            mScirCtlRadioGroupProblemType.invalidate();

            mScirCtlRadioGroupProblemType.setOnCheckedChangeListener(mScirProblemTypeGroupChangeListener);
            mScirCtlProblemSeverityRating.setOnRatingBarChangeListener(mScirSeverityLevelRatingBarListener);
            mScirCtlButtonSubmitFeedback.setOnClickListener(mScirFeedbackButtonClickListener);
            mScirCtlSeveritySeekBar.setOnSeekBarChangeListener(mScirSeverityLevelSeekBarListener);
    }

    /******************************************************************
     * Main Activity
     * @param savedInstanceState
     * ****************************************************************
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        setupScirEnvProblemCapturing();

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton = (Button) findViewById(R.id.capture_infra_problem_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        mIsCapturing = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to open camera.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        setupImageDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(180); // Sasan : Hack for time being !!
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mCamera.startPreview();
        mCaptureImageButton.setText(R.string.capture_image); // ToDo : Fix string in strings.xml
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    private void setupImageDisplay() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        mCameraImage.setImageBitmap(bitmap);
        mCamera.stopPreview();
        mCameraPreview.setVisibility(View.INVISIBLE);
        mCameraImage.setVisibility(View.VISIBLE);
        mCameraImage.setRotation(180); // Sasan : hack ExifInterface.ORIENTATION_FLIP_VERTICAL
        mCaptureImageButton.setText(R.string.recapture_image); // ToDo : Fix string in strings.xml
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
    }
}



