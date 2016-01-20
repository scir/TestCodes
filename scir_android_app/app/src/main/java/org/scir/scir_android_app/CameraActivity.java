package org.scir.scir_android_app;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;




/**
 * Functionalities to be added / enhanced (TODO):
 * 1) In case of high resolution mobile cameras, photos with some minimum size should be captured, otherwise it will overload system
 * 2) Image Orientation needs to be handled in a device independent manner (Currently it might be running only SAMSUNG based devices in correct manner)
 * 3) Image contents need to be compressed in JPG format at source only (in case not being already handled)
 * 4) INTEGRATION with server Front End needs to be done ASAP.
 * 5) Deprecated APIs need to be planned for porting to newer versions
 */

public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    public enum SCIR_TICKET_SEVERITY {
        INVALID,
        NoProblem,
        Low,
        Normal,
        High,
        Urgent
    }
    public enum SCIR_PROBLEM_TYPE {
        None,
        Electricity,
        Road,
        Sewage,
        Water,
//        Sanitation,
        Other
    }

    public static final String EXTRA_CAMERA_DATA = "camera_data";
    private static final String KEY_IS_CAPTURING = "is_capturing";

    private Camera mCamera;
    private ImageView mCameraImage;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private byte[] mCameraData;
    private byte[] mCameraDataCompressed;
    private boolean mIsCapturing;


    private RatingBar mScirCtlProblemSeverityRating;
    private Button mScirCtlButtonSubmitFeedback;

    ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint;

    private String mScirDataMobileNumber;
    private String mScirDataDeviceId;
    private SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel;
    private SCIR_PROBLEM_TYPE mScirDataProblemType;

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

    private boolean reportInfraProblemToBackEnd(ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint) {
        String charset = "UTF-8", requestURL = "";
//        requestURL = "http://192.168.1.100:8080/smart-city/AddTicket";
//        requestURL = "http://192.168.1.123:9999/SmartCity/AddTicket";
//        requestURL = "http://103.242.62.23:9999/SmartCity/AddTicket";
//        requestURL = "http://sasan.twilightparadox.com:9999/SmartCity/AddTicket";

        requestURL = "http://103.242.62.23:8080/smart-city/AddTicket";

        String fileName = "Image" +
//                "_" + mScirDataMobileNumber.substring(1) + "_" + Long.toString(mScirDataInfraFeedbackPoint.getScirDataDateTime()) +
                ".jpg" ;
        String fullResponse = "";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addHeaderField("User-Agent", "SCIR");
            multipart.addHeaderField("Test-Header", "Header-Value");
            Log.i("CLientApp", "Level C1.0 2016-01-19 11:02");

            multipart.addFormField("description", "SCIR Grievance Ticket");
            multipart.addFormField("summary", "Sample String 2016-01-19 11:02");
            multipart.addFormField("type", mScirDataInfraFeedbackPoint.getScirDataProblemType().toString());
            multipart.addFormField("severity", mScirDataInfraFeedbackPoint.getScirDataProblemSeverityLevel().toString());
            multipart.addFormField("deviceId", mScirDataInfraFeedbackPoint.getScirDataDeviceId());
            multipart.addFormField("msisdn", mScirDataInfraFeedbackPoint.getScirDataMobileNumber());
            multipart.addFormField("latitude", Double.toString(mScirDataInfraFeedbackPoint.getScirDataLat()));
            multipart.addFormField("longitude", Double.toString(mScirDataInfraFeedbackPoint.getScirDataLong()));
            multipart.addFormField("time", String.valueOf(mScirDataInfraFeedbackPoint.getScirDataDateTimeFEServerForamt()));
//            multipart.addFormField("time", String.valueOf(mScirDataInfraFeedbackPoint.getScirDataDateTime()));
//            multipart.addFormField("time", "2016-01-19 15:37:00");

            Log.i("CLientApp", "Level C1.1");

            multipart.addFilePart("imgFile", fileName, mCameraDataCompressed, mCameraDataCompressed.length);
            // Hack for checking upload speed
//            byte [] altCameraData = Arrays.copyOfRange(mCameraData, 0, 128);
//            multipart.addFilePart("imgFile", fileName, altCameraData, altCameraData.length);

            List<String> response = multipart.finish();

            //display what returns the POST request
            for (String line : response) {
                Log.i("ClientApp", line);
                fullResponse = fullResponse.concat(line);
            }
        } catch (MalformedURLException eURL) {
            eURL.printStackTrace();
            fullResponse = fullResponse.concat(eURL.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            fullResponse = fullResponse.concat(ex.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fullResponse = fullResponse.concat(e.toString());
        } finally {
            Toast.makeText(CameraActivity.this, fullResponse, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    byte[] resizeImage(byte[] input, int PhotoWidth, int PhotoHeight) {
        Bitmap original = BitmapFactory.decodeByteArray(input , 0, input.length);
        Bitmap resized = Bitmap.createScaledBitmap(original, PhotoWidth, PhotoHeight, true);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

        return blob.toByteArray();
    }

    private OnClickListener mScirFeedbackButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCameraData != null) {
                // TODO : This is final processing stage of all submitted contents
                Double lat, lon ;
                long dateTime ;
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                mScirDataMobileNumber = tm.getLine1Number();
                mScirDataDeviceId = tm.getDeviceId();
                if (mScirDataMobileNumber == null) mScirDataMobileNumber = "UNAVAILABLE";
                if (mScirDataDeviceId == null) mScirDataDeviceId = "NOT_AVAILABLE";

                if( MainActivity.mScirCurrentLocation == null ) {
                    lat = 12.0;
                    lon = 23.0;
                    dateTime = 1234567;
                } else {
                    lat = MainActivity.mScirCurrentLocation.getLatitude();
                    lon = MainActivity.mScirCurrentLocation.getLongitude();
                    dateTime = MainActivity.mScirCurrentLocation.getTime();
                }
                mScirDataInfraFeedbackPoint = new ScirInfraFeedbackPoint(
                        lat, lon, dateTime,
                        mCameraData,
                        mScirDataProblemType, mScirDataProblemSeverityLevel,
                        mScirDataMobileNumber, mScirDataDeviceId,
                        "<<Description>>");
                new SubmitDataToBackEndTask().execute(mScirDataInfraFeedbackPoint);
                String dataSubmitted =
                        "Data Submitted to backend:" +
                                "\nLat:" + mScirDataInfraFeedbackPoint.getScirDataLat()  +
                                "\nLong:" + mScirDataInfraFeedbackPoint.getScirDataLong()  +
                                "\nDateTime:" + mScirDataInfraFeedbackPoint.getScirDataDateTime() +
                                "\nProblem: " + mScirDataProblemType.toString() +
                                "\nSeverity: " + mScirDataProblemSeverityLevel +
                                "\nMobile :" + mScirDataInfraFeedbackPoint.getScirDataMobileNumber() +
                                "";
                Toast.makeText(CameraActivity.this, dataSubmitted, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CameraActivity.this, "Can't submit data due to some problem", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    };

    private RadioGroup.OnCheckedChangeListener mScirProblemTypeGroupChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.scirCtrlRadioSelectElectricity:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.Electricity;
                    break;
                case R.id.scirCtrlRadioSelectWater:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.Water;
                    break;
                case R.id.scirCtrlRadioSelectPollution:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.Sewage;
                    break;
                case R.id.scirCtrlRadioSelectRoad:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.Road;
                    break;
                default:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.Other;
                    break;
            }
        }
    };

    private RatingBar.OnRatingBarChangeListener mScirSeverityLevelRatingBarListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            /* Set rating level */
//            mScirDataProblemSeverityLevel = rating;
            if( rating < 1.0) {
                mScirDataProblemSeverityLevel = SCIR_TICKET_SEVERITY.NoProblem ;
            } else if( rating <= 2.0) {
                mScirDataProblemSeverityLevel = SCIR_TICKET_SEVERITY.Low;
            } else if (rating <= 3.0) {
                mScirDataProblemSeverityLevel = SCIR_TICKET_SEVERITY.Normal;
            } else if (rating <= 4.0 ) {
                mScirDataProblemSeverityLevel = SCIR_TICKET_SEVERITY.High;
            } else if (rating <= 5.0 ) {
                mScirDataProblemSeverityLevel = SCIR_TICKET_SEVERITY.Urgent;
            } else {
                mScirDataProblemSeverityLevel = SCIR_TICKET_SEVERITY.INVALID ;
            }
        }
    };


    private void setupScirEnvForGrievanceCapturing() {
        RadioGroup mScirCtlRadioGroupProblemType ;

        mScirCtlRadioGroupProblemType = (RadioGroup) findViewById(R.id.scirCtrlRadioGroupProblemType);
        mScirCtlProblemSeverityRating = (RatingBar) findViewById(R.id.scirCtrlRatingBar);
        mScirCtlButtonSubmitFeedback = (Button) findViewById(R.id.scirCtrlButtonFeedback);

        // TODO: Remove any default settings selection to provide true picture to end user
//        mScirCtlRadioGroupProblemType.invalidate();
        mScirCtlRadioGroupProblemType.setSelected(false);

        mScirCtlRadioGroupProblemType.setOnCheckedChangeListener(mScirProblemTypeGroupChangeListener);
        mScirCtlProblemSeverityRating.setOnRatingBarChangeListener(mScirSeverityLevelRatingBarListener);
        mScirCtlButtonSubmitFeedback.setOnClickListener(mScirFeedbackButtonClickListener);
    }

    /******************************************************************
     * Main Activity
     *
     * @param savedInstanceState : Saved Instance State
     *****************************************************************
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_camera);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        setupScirEnvForGrievanceCapturing();

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
//        (< 1/4 of VGA)
        final int PHOTO_WIDTH = 100 ;
        final int PHOTO_HEIGHT = 75 ;
        mCameraData = data ;
        mCameraDataCompressed = resizeImage(data, PHOTO_WIDTH, PHOTO_HEIGHT);
        setupImageDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                // mCamera.setDisplayOrientation(180); // Sasan : Hack for time being !! => Works for simulator....
                mCamera.setDisplayOrientation(90); // Hack : Works For Samsung S3 Neo
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
        //      mCameraImage.setRotation(180); // Sasan : hack ExifInterface.ORIENTATION_FLIP_VERTICAL -> For Simulator
        mCameraImage.setRotation(90);
        mCaptureImageButton.setText(R.string.recapture_image); // ToDo : Fix string in strings.xml
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
    }


    class SubmitDataToBackEndTask extends AsyncTask<ScirInfraFeedbackPoint, Void, Void> {
        private Exception exception;
        ScirInfraFeedbackPoint mFeedbackPoint ;

        protected Void doInBackground(ScirInfraFeedbackPoint... feedbackPoint) {
            try {
                this.mFeedbackPoint = feedbackPoint[0] ;
                reportInfraProblemToBackEnd(mScirDataInfraFeedbackPoint);
            } catch (Exception e) {
                this.exception = e;
            }
            return null;
        }

        protected void onPostExecute(ScirInfraFeedbackPoint feedbackPoint) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

}
