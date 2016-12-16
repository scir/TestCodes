package org.scir.scir_android_app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import org.sss.library.SssImageLibrary;
import org.sss.library.SssPreferences;
import org.sss.library.camera.Camera2BasicFragment;
import org.sss.library.handler.RequestHandlerThread;
import org.sss.library.scir.ScirInfraFeedbackPoint;

import static org.scir.scir_android_app.MainActivity.RESULT_EXIT_CALLED;
import static org.scir.scir_android_app.MainActivity.RESULT_RESTART_INTENT_CAMERA;

public class Camera2Activity extends Activity {

    protected byte[] mCameraData;
    private int mImageHeight = -1 , mImageWidth = -1 ;

    public synchronized void setCameraData(byte[] cameraData, int width, int height) {
        mCameraData = cameraData ;
        mImageHeight = height ;
        mImageWidth = width ;

        Log.i("SCIR_Camera2Activity", "Setting Camera Data in setCameraData()");
        if( ! sssPreferences.isStoreFullPicture()) {
            mScirDataInfraFeedbackPoint.appendScirReportDescription("Orig Size:("
                    + width + "X" + height + ")" );;
            mCameraDataCompressed = SssImageLibrary.resizeImage(mCameraData,
                    sssPreferences.getImageWidth(), sssPreferences.getImageHeight());
            mScirDataInfraFeedbackPoint.appendScirReportDescription("New Size:(" +
              sssPreferences.getImageWidth() + "x" + sssPreferences.getImageHeight() + ")");
            Log.i("SCIR_Camera2Activity", mScirDataInfraFeedbackPoint.getScirReportDescription());
        } else {
            String strImageDim = "Orig Size:(" +
                    + width + "X" + height + ")" ;
            mScirDataInfraFeedbackPoint.appendScirReportDescription(strImageDim);
            mScirDataInfraFeedbackPoint.setScirImageDimension(strImageDim);
            if( mCameraDataCompressed != null) {
                Log.w("SCIR_Camera2Activity", "Case of INCORRECT compressed data repeat Possible!");
                mCameraDataCompressed = null;
            }
        }
    }

    private byte[] mCameraDataCompressed = null ;
    private RatingBar mScirCtlProblemSeverityRating;
    private Button mScirCtlButtonSubmitFeedback;

    private ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint;
    private RequestHandlerThread mRequestHandlerThread ;
    private SssPreferences sssPreferences ;



    private Camera2BasicFragment mCamera2BasicFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sssPreferences = SssPreferences.getSssPreferences() ;
        setContentView(R.layout.activity_camera2);
        setupScirEnvForGrievanceCapturing();
        mRequestHandlerThread = RequestHandlerThread.getRequestHandlerThread();

        if (null == savedInstanceState) {
            mCamera2BasicFragment = Camera2BasicFragment.newInstance(this, mRequestHandlerThread);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mCamera2BasicFragment )
                    .commit();
        }
        Button buttonScirCamera2Exit = (Button)findViewById(R.id.scirCamera2BtnExit);
        buttonScirCamera2Exit.setOnClickListener(mScirExitButtonClickListener);
    }

    private View.OnClickListener mScirExitButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_EXIT_CALLED);
            finish();
        }
    };

    private View.OnClickListener mScirFeedbackButtonClickListener = new View.OnClickListener() {
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

                    Log.i("SCIR_Camera2Activity", "Content submitted on queue!"
                            + mScirDataInfraFeedbackPoint.getScirReportDescription());
                    Toast.makeText(getApplicationContext(), "Feedback submitted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Camera2Activity.this, "Picture has not been captured!!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_RESTART_INTENT_CAMERA);
                }
            } catch(Exception e) {
                String errorMessage = "Error while capturing Feedback Point details\n" + e.getMessage();
                Log.e("SCIR_Camera2Activity", errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                setResult(RESULT_RESTART_INTENT_CAMERA);
            } finally {
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener mScirProblemTypeGroupChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            group.clearCheck();
            mScirDataInfraFeedbackPoint.setScirDataProblemType(checkedId);
            group.check(checkedId);
        }
    };

    private int mLastProblemTypeSetting = -1 ;
    RadioButton mLastRadioButton = null ;
    public void updateProblemTypeRadioButton(View view) {
        if( (mLastProblemTypeSetting != -1 ) && (mLastRadioButton != null)) {
            mLastRadioButton.setChecked(false);
        }
        mLastRadioButton = (RadioButton) view ;
        mLastProblemTypeSetting = mLastRadioButton.getId();
        mScirDataInfraFeedbackPoint.setScirDataProblemType(mLastProblemTypeSetting);
        mLastRadioButton.setChecked(true);
    }

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


}