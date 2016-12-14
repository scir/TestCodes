package org.sss.library.handler;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.scir.scir_android_app.CameraActivity;
import org.sss.library.MultipartUtility;
import org.sss.library.SssPreferences;
import org.sss.library.db.ScirSqliteHelper;
import org.sss.library.exception.SssUnhandledException;
import org.sss.library.scir.ScirInfraFeedbackPoint;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by khelender on 09-12-2016.
 */
/*
 * Responsible for submitting the report to backend (in long term)
        * 1) Submit the report to back-end (active thread)
        * 2) Store reports in sqlite database
        * 3) Validate submission to back-end, if not plan for submission whenever condition is favorable.
*/

public class ScirRequestProcessingHandler extends Handler {
    private int numRequests = 0 ;

    ScirSqliteHelper mScirSqliteHelper ;
    /**
     * Use the provided {@link Looper} instead of the default one.
     *
     * @param looper The looper, must not be null.
     */
    public ScirRequestProcessingHandler(Looper looper) {
        super(looper);
        try {
            mScirSqliteHelper = ScirSqliteHelper.getScirSqliteHelper();
        } catch (SssUnhandledException e) {
            e.printStackTrace();
        }
//        mScirSqliteHelper = new ScirSqliteHelper(
//                CameraActivity.myContext(),null,null,1);
    }

    /**
     * Handle system messages here.
     *
     * @param msg
     */
    @Override
    public void dispatchMessage(Message msg) {
        super.dispatchMessage(msg);
    }

    /**
     * Subclasses must implement this to receive messages.
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        numRequests++ ;
        Log.d("SCIR_BE_Process-Handle","Received another request: Total Request:" + numRequests);
        ScirInfraFeedbackPoint scirInfraFeedbackPoint = (ScirInfraFeedbackPoint) msg.obj;
        Log.w("SCIR_BE_Process-Handle","Object details :" +
                "Problem Type:" + scirInfraFeedbackPoint.getScirDataProblemType().toString()+
                "Severity Type:" + scirInfraFeedbackPoint.getScirDataProblemSeverityLevel().toString()+
                "DateTime:" + scirInfraFeedbackPoint.getScirDataDateTimeFEServerForamt().toString()
            );
        scirInfraFeedbackPoint.storeIntoDatabase(mScirSqliteHelper);
        processRequest(scirInfraFeedbackPoint);
        super.handleMessage(msg);
    }

    private boolean processRequest(ScirInfraFeedbackPoint scirInfraFeedbackPoint) {
        boolean statusSuccess = false;
        try {
            Log.i("SCIR_BE_RequestProcess", "Starting Submission process to backend");
            statusSuccess = reportInfraProblemToBackEnd(scirInfraFeedbackPoint);
            Log.i("SCIR_BE_RequestProcess", "Status : " + (statusSuccess ? "Good" : "Failed"));
            Log.i("SCIR_BE_RequestProcess", scirInfraFeedbackPoint.getDataSubmitted());
        } catch (Exception e) {
            Log.w("SCIR_BE_RequestProcess", "Got exception " + e.getMessage() + e.toString());
        }
        return statusSuccess;
    }

    public boolean reportInfraProblemToBackEnd(ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint) {
        String charset = "UTF-8", requestURL = "";
        boolean statusSuccess = false ;

        SssPreferences sssPreferences = SssPreferences.getSssPreferences() ;
        requestURL = "http://sws-international.com:8080/smart-city/AddTicket";

        String fileName = "Image.jpg" ;
        String fullResponse = "";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addHeaderField("User-Agent", "SCIR");
            multipart.addHeaderField("Test-Header", "Header-Value");
            Log.i("BackendProcess-Submit", "Level C1.0 2016-01-19 11:02");

            mScirDataInfraFeedbackPoint.setUrlServer(requestURL);

            multipart.addFormField("description", "SCIR Grievance Ticket");
            multipart.addFormField("summary", "Sample String 2016-01-19 11:02");
            multipart.addFormField("type", mScirDataInfraFeedbackPoint.getScirDataProblemType().toString());
            multipart.addFormField("severity", mScirDataInfraFeedbackPoint.getScirDataProblemSeverityLevel().toString());
            multipart.addFormField("deviceId", mScirDataInfraFeedbackPoint.getScirDataDeviceId());
            multipart.addFormField("msisdn", mScirDataInfraFeedbackPoint.getScirDataMobileNumber());
            multipart.addFormField("latitude", Double.toString(mScirDataInfraFeedbackPoint.getScirDataLat()));
            multipart.addFormField("longitude", Double.toString(mScirDataInfraFeedbackPoint.getScirDataLong()));
            multipart.addFormField("time", String.valueOf(mScirDataInfraFeedbackPoint.getScirDataDateTimeFEServerForamt()));

            Log.i("BackendProcess-Submit", "Level C1.1");

            if( sssPreferences.isStoreFullPicture()) {
                multipart.addFilePart("imgFile", fileName,
                        mScirDataInfraFeedbackPoint.getmScirDataCameraImage(),
                        mScirDataInfraFeedbackPoint.getmScirDataCameraImage().length);
            } else {
                byte [] dataCompressedImage = mScirDataInfraFeedbackPoint.getScirDataCameraCompressedImage();
                multipart.addFilePart("imgFile", fileName, dataCompressedImage, dataCompressedImage.length) ;
            }

            List<String> response = multipart.finish();

            //display what returns the POST request
            for (String line : response) {
                Log.i("BackendProcess-Submit", line);
                fullResponse = fullResponse.concat(line);
            }
            statusSuccess = true ;
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
            Log.i("BackendProcess-Submit",fullResponse);
        }
        return statusSuccess;
    }

}
