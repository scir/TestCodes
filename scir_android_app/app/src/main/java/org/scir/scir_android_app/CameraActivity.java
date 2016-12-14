package org.scir.scir_android_app;

import android.content.Context;
import android.os.Bundle;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import org.sss.library.SssImageLibrary;
import org.sss.library.SssPreferences;
import org.sss.library.handler.RequestHandlerThread;
import org.sss.library.scir.ScirInfraFeedbackPoint;


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
    private int mImageHeight = -1 , mImageWidth = -1 ;

    private synchronized void setCameraData(byte[] cameraData, int width, int height) {
        mCameraData = cameraData ;
        mImageHeight = height ;
        mImageWidth = width ;
    }

    private byte[] mCameraDataCompressed;
    private boolean mIsCapturing;

    private RatingBar mScirCtlProblemSeverityRating;
    private Button mScirCtlButtonSubmitFeedback;

    ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint;
    RequestHandlerThread mRequestHandlerThread ;

    private SssPreferences sssPreferences ;


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

    private OnClickListener mScirFeedbackButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            try {
                if (mCameraData != null) {
                    String reportDim =
                        "CameraImage:" + mImageWidth + "x" + mImageHeight + "& length=" + mCameraData.length + "\n";
                    mScirDataInfraFeedbackPoint.setScirImageDimension(reportDim);
                    mScirDataInfraFeedbackPoint.appendScirReportDescription(reportDim);
                    mScirDataInfraFeedbackPoint.collateReport(mCameraData, mCameraDataCompressed, tm);

                    // New Way : Simply submit the request to backend.
                    Message msgFeedbackPoint = Message.obtain(RequestHandlerThread.getRequestHandlerThread().getSubmitHandler());
                    msgFeedbackPoint.obj = mScirDataInfraFeedbackPoint ;
                    msgFeedbackPoint.what = RequestHandlerThread.MSG_SCIR_FEEDBACK_POINT;
                    msgFeedbackPoint.setTarget(RequestHandlerThread.getScirRequestProcessingHandler());
                    msgFeedbackPoint.sendToTarget();

                    Log.i("SCIR_CameraActivity", "Content submitted on queue!"
                    + mScirDataInfraFeedbackPoint.getScirReportDescription());

                } else {
                    Toast.makeText(CameraActivity.this, "Picture has not been captured!!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                }
            } catch(Exception e) {
                Log.e("SCIR_CameraActivity", "Error while capturing Feedback Point details\n" + e.getMessage());
                setResult(RESULT_CANCELED);
            }

            finish();
        }
    };

    private RadioGroup.OnCheckedChangeListener mScirProblemTypeGroupChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mScirDataInfraFeedbackPoint.setScirDataProblemType(checkedId);
        }
    };

    private RatingBar.OnRatingBarChangeListener mScirSeverityLevelRatingBarListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            /* Set rating level */
            mScirDataInfraFeedbackPoint.setScirDataProblemSeverityLevel(rating);
        }
    };


    private void setupScirEnvForGrievanceCapturing() {
        RadioGroup mScirCtlRadioGroupProblemType ;

        mScirDataInfraFeedbackPoint = new ScirInfraFeedbackPoint();

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

//    static private Context myContext = null ;
//    static public Context myContext() {
//        return myContext;
//    }

    private void setupCameraDimensions() {
        if( mCamera != null ) {
            Camera.Size oldSize = mCamera.getParameters().getPictureSize();
            mCamera.getParameters().setPictureSize(sssPreferences.getImageWidth(), sssPreferences.getImageHeight());
            Camera.Size newSize = mCamera.getParameters().getPictureSize();
        }
        if( mCameraPreview != null ) {
            mCameraPreview.getLayoutParams().height = sssPreferences.getImageHeight();
            mCameraPreview.getLayoutParams().width = sssPreferences.getImageWidth();
        }
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

        sssPreferences = SssPreferences.getSssPreferences() ;
        setContentView(R.layout.activity_camera);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);

        setupCameraDimensions();
        setupScirEnvForGrievanceCapturing();


        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton = (Button) findViewById(R.id.capture_infra_problem_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        mRequestHandlerThread = RequestHandlerThread.getRequestHandlerThread();

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
                setupCameraDimensions();
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
        mCameraData = data ;

        if( ! sssPreferences.isStoreFullPicture()) {
            mScirDataInfraFeedbackPoint.appendScirReportDescription("Orig Size:(" +
                    camera.getParameters().getPictureSize().width + "x" +
                    camera.getParameters().getPictureSize().height + ")\n"
                    );
            mCameraDataCompressed = SssImageLibrary.resizeImage(data,
                    sssPreferences.getImageWidth(), sssPreferences.getImageHeight());
            mScirDataInfraFeedbackPoint.appendScirReportDescription("New Size:(" +
                    camera.getParameters().getPictureSize().width + "x" +
                    camera.getParameters().getPictureSize().height + ")\n");
            Log.i("SCIR_CameraActivity", mScirDataInfraFeedbackPoint.getScirReportDescription());
        } else {
            String strImageDim = "Orig Size:(" +
                    camera.getParameters().getPictureSize().width + "x" +
                    camera.getParameters().getPictureSize().height + ")\n";
            mScirDataInfraFeedbackPoint.appendScirReportDescription(strImageDim);
            mScirDataInfraFeedbackPoint.setScirImageDimension(strImageDim);
            if( mCameraDataCompressed != null) {
                Log.w("SCIR_CameraActivity", "Case of INCORRECT compressed data repeat Possible!");
                mCameraDataCompressed = null;
            }
        }
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
        setupCameraDimensions();
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

}
