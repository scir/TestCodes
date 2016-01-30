package org.scir.scir_android_app;

import android.app.Activity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by khelender on 27-01-2016.
 *
 * Responsible for submitting the report to backend
 * 1) Submit the report to back-end (active thread)
 * 2) Store reports in sqlite database
 * 3) Validate submission to back-end, if not plan for submission whenever condition is favorable.
 *
 */
public class SubmitReport {

    ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint ;

    SubmitReport(ScirInfraFeedbackPoint mScirDataInfraFeedbackPoint) {
        this.mScirDataInfraFeedbackPoint = mScirDataInfraFeedbackPoint ;
    }

    public boolean reportInfraProblemToBackEnd(Activity activity) {
        String charset = "UTF-8", requestURL = "";
        // For PC configuration
//        requestURL = "http://192.168.0.6:8080/smart-city/AddTicket";
//          requestURL = "http://103.242.60.127:9999/SmartCity/AddTicket";
        requestURL = "http://smartcity.dlinkddns.com:9999/SmartCity/AddTicket";

        // For laptop configuration
//        requestURL = "http://sasan.twilightparadox.com:8080/smart-city/AddTicket";

        String fileName = "Image.jpg" ;
        String fullResponse = "";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addHeaderField("User-Agent", "SCIR");
            multipart.addHeaderField("Test-Header", "Header-Value");
            Log.i("CLientApp", "Level C1.0 2016-01-19 11:02");

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

            Log.i("ClientApp", "Level C1.1");

            byte [] dataCompressedImage = mScirDataInfraFeedbackPoint.getScirDataCameraCompressedImage();
            multipart.addFilePart("imgFile", fileName, dataCompressedImage, dataCompressedImage.length) ;
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
            Toast.makeText(activity, fullResponse, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void collateReport(byte[] mCameraData, byte[] mCameraDataCompressed, TelephonyManager tm) {
        // TODO : This is final processing stage of all submitted contents
        Double lat, lon ;
        long dateTime ;
        String mobileNumber;
        String deviceId;

        mobileNumber = tm.getLine1Number();
        deviceId = tm.getDeviceId();
        if (mobileNumber == null) mobileNumber = "UNAVAILABLE";
        if (deviceId == null) deviceId = "NOT_AVAILABLE";

        if( MainActivity.mScirCurrentLocation == null ) {
            // TODO: Need to handle Errors here !!
            lat = 12.0;
            lon = 23.0;
            dateTime = 1234567;
        } else {
            lat = MainActivity.mScirCurrentLocation.getLatitude();
            lon = MainActivity.mScirCurrentLocation.getLongitude();
            dateTime = MainActivity.mScirCurrentLocation.getTime();
        }
        mScirDataInfraFeedbackPoint.setScirInfraFeedback(
                lat, lon, dateTime,
                mCameraData, mCameraDataCompressed,
                mScirDataInfraFeedbackPoint.getScirDataProblemType(), mScirDataInfraFeedbackPoint.getScirDataProblemSeverityLevel(),
                mobileNumber, deviceId,
                "<<Description>>", "");
        return;
    }


    public String getDataSubmitted() {
        String dataSubmitted =
                "Data Submitted to backend:" +
                        "\nLat:" + mScirDataInfraFeedbackPoint.getScirDataLat()  +
                        "\nProblem: " + mScirDataInfraFeedbackPoint.getScirDataProblemType().toString() +
                        "\nSeverity: " + mScirDataInfraFeedbackPoint.getScirDataProblemSeverityLevel().toString() +
                        "\nMobile :" + mScirDataInfraFeedbackPoint.getScirDataMobileNumber() +
                        "\nLong:" + mScirDataInfraFeedbackPoint.getScirDataLong()  +
                        "\nDateTime:" + mScirDataInfraFeedbackPoint.getScirDataDateTime() +
                        "";
        dataSubmitted = dataSubmitted.concat("\nURL:" + mScirDataInfraFeedbackPoint.getUrlServer() + "\n");
        Log.i("SubmitReport", dataSubmitted + mScirDataInfraFeedbackPoint.toString());
        return dataSubmitted ;
    }
}
