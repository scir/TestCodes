package org.scir.scir_android_app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.provider.SyncStateContract;
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
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.HttpClient ;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.EntityReference;


// import org.scir.scir_android_app.SCIRLocationFinder ;

/**
 * Functionalities to be added / enhanced (TODO):
 * 1) In case of high resolution mobile cameras, photos with some minimum size should be captured, otherwise it will overload system
 * 2) Image Orientation needs to be handled in a device independent manner (Currently it might be running only SAMSUNG based devices in correct manner)
 * 3) Image contents need to be compressed in JPG format at source only (in case not being already handled)
 * 4) INTEGRATION with server Front End needs to be done ASAP.
 * 5) Deprecated APIs need to be planned for porting to newer versions
 */

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
    private RatingBar mScirCtlProblemSeverityRating;
    private SeekBar mScirCtlSeveritySeekBar;
    private Button mScirCtlButtonSubmitFeedback;

    ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint;

    final String uploadFilePath = "/mnt/sdcard/saved_data";
    final String uploadFileName = "service_lifecycle.png";

    public enum SCIR_PROBLEM_TYPE {
        E_SCIR_WATER,
        E_SCIR_ELECTRICITY,
        E_SCIR_ROAD,
        E_SCIR_SANITATION,
        E_SCIR_POLLUTION,
        E_SCIR_OTHER
    }

    ;

    private double mScirDataLat, mScirDataLong;
    private long mScirDataDateTime;
    private String mScirDataMobileNumber;
    private String mScirDataDeviceId;
    private float mScirDataProblemSeverityLevel;
    private SCIR_PROBLEM_TYPE mScirDataProblemType;

    private int mServerResponseCode = 0;

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
        String fname = "Image-" + number + ".dat";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();

        String strLocation = "";
        try {
            strLocation = String.format("Latitude(%.3f)\nLongitude(%.3f)\nEpoch(%tc)\n",
                    mScirDataLat, mScirDataLong, mScirDataDateTime);
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

            if (mCameraData != null) {
                String fnameImage = "Image-" + number + ".jpg";
                File fileImage = new File(myDir, fnameImage);
                if (fileImage.exists()) fileImage.delete();
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

    private OutputStream os ;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary  = "*****";
    String delimiter = "*****";




    private boolean reportInfraProblemToBackEnd(ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint) {
        // Call the appropriate interface of Backend Web Service here
        System.out.println("onClick!!!");

        DataOutputStream dos = null ;

        String charset = "UTF-8";
        File uploadFile1 = new File("e:/Test/PIC1.JPG");
        File uploadFile2 = new File("e:/Test/PIC2.JPG");
        String requestURL = "http://192.168.1.104:8080/smart-city/AddTicket";
        String fileName = uploadFilePath + uploadFileName ;


        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addHeaderField("User-Agent", "SCIR");
            multipart.addHeaderField("Test-Header", "Header-Value");
//            multipart.addHeaderField("Connection", "Keep-Alive");
//            multipart.addHeaderField("ENCTYPE", "multipart/form-data");
            Log.i("CLientApp", "Level C1.0");

            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");
            multipart.addFormField("summary", "Sample String");
            multipart.addFormField("type", mScirDataInfraFeedbackPoint.getScirDataProblemType().toString());
            multipart.addFormField("severity", "Urgent");
            multipart.addFormField("imgFile", "samplefilename.txt");
            multipart.addFormField("deviceId", mScirDataInfraFeedbackPoint.getScirDataDeviceId());
            multipart.addFormField("msisdn", mScirDataInfraFeedbackPoint.getScirDataMobileNumber());
            multipart.addFormField("latitude", "12.34");
            multipart.addFormField("longitude", "3.8");
            multipart.addFormField("time", "123434");

            Log.i("CLientApp", "Level C1.1");
//            multipart.addFilePart("fileUpload", fileName);
//            multipart.addFilePart("fileUpload", uploadFile2);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");
            Log.i("ClientApp", "SERVER REPLY");

            //display what returns the POST request
            for (String line : response) {
                System.out.println(line);
                Log.i("ClientApp", line);
            }
        } catch (MalformedURLException eURL) {
            eURL.printStackTrace();
        } catch (IOException ex) {
            System.err.println(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }




        return true;
    }

    public void addFormPart(String paramName, String value) throws Exception {
        writeParamData(paramName, value);
    }

    private void writeParamData(String paramName, String value) throws Exception {
        os.write( (delimiter + boundary + "\r\n").getBytes());
        os.write( "Content-Type: text/plain\r\n".getBytes());
        os.write( ("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());;
        os.write( ("\r\n" + value + "\r\n").getBytes());

    }
    private OnClickListener mScirFeedbackButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCameraData != null) {
                // TODO : This is final processing stage of all submitted contents
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                mScirDataMobileNumber = tm.getLine1Number();
                mScirDataDeviceId = tm.getDeviceId();
                if (mScirDataMobileNumber == null) mScirDataMobileNumber = "UNAVAILABLE";
                if (mScirDataDeviceId == null) mScirDataDeviceId = "NOT_AVAILABLE";


                mScirDataInfraFeedbackPoint = new ScirInfraFeedbackPoint(
                        MainActivity.mScirCurrentLocation.getLatitude(),
                        MainActivity.mScirCurrentLocation.getLongitude(),
                        MainActivity.mScirCurrentLocation.getTime(),
                        mCameraData,
                        mScirDataProblemType, mScirDataProblemSeverityLevel,
                        mScirDataMobileNumber, mScirDataDeviceId,
                        "<<Description>>");


                SaveData(); // TODO: To be cleaned up and removed !! after below is in order
                new SubmitDataToBackEndTask().execute(mScirDataInfraFeedbackPoint);
//                reportInfraProblemToBackEnd(mScirDataInfraFeedbackPoint);
            } else {
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
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_ELECTRICITY;
                    break;
                case R.id.scirCtrlRadioSelectWater:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_WATER;
                    break;
                case R.id.scirCtrlRadioSelectPollution:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_POLLUTION;
                    break;
                case R.id.scirCtrlRadioSelectRoad:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_ROAD;
                    break;
                default:
                    mScirDataProblemType = SCIR_PROBLEM_TYPE.E_SCIR_OTHER;
                    break;
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
            mScirDataProblemSeverityLevel = rating;
        }
    };


    private void setupScirEnvProblemCapturing() {
        mScirCtlRadioGroupProblemType = (RadioGroup) findViewById(R.id.scirCtrlRadioGroupProblemType);
        mScirCtlProblemSeverityRating = (RatingBar) findViewById(R.id.scirCtrlRatingBar);
        mScirCtlButtonSubmitFeedback = (Button) findViewById(R.id.scirCtrlButtonFeedback);
        // mScirCtlSeveritySeekBar = (SeekBar) findViewById(R.id.scirCtrlSeekBar);

        // TODO: Remove any default settings selection to provide true picture to end user
        mScirCtlRadioGroupProblemType.invalidate();

        mScirCtlRadioGroupProblemType.setOnCheckedChangeListener(mScirProblemTypeGroupChangeListener);
        mScirCtlProblemSeverityRating.setOnRatingBarChangeListener(mScirSeverityLevelRatingBarListener);
        mScirCtlButtonSubmitFeedback.setOnClickListener(mScirFeedbackButtonClickListener);
        // mScirCtlSeveritySeekBar.setOnSeekBarChangeListener(mScirSeverityLevelSeekBarListener);
    }

    /******************************************************************
     * Main Activity
     *
     * @param savedInstanceState
     *****************************************************************
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
//            String, Void, RSSFeed>

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
