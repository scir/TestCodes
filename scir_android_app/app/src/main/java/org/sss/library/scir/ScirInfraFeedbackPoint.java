package org.sss.library.scir;

import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.scir.scir_android_app.MainActivity;
import org.scir.scir_android_app.R;
import org.sss.library.db.ScirSqliteHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Represents the contents submitted for feedback report
 *
 * Created by khelender on 06-01-2016.
 */
public class ScirInfraFeedbackPoint {

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
        Sanitation,
        Water,
        Sewage,
        //        Sanitation,
        Other
    }


    private double mScirDataLat, mScirDataLong;
    private long mScirDataDateTime ;
    private SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel;
    private SCIR_PROBLEM_TYPE mScirDataProblemType ;
    private String mScirDataMobileNumber ;
    private String mScirDataDeviceId ;
    private String mScirDataFeedbackDescription ;
    private byte [] mScirDataCameraImage ;
    private byte [] mScirDataCameraCompressedImage ;
    private String mUrlServer ;

    private String mScirImageDimension;
    private String mStatus ;
    private String mScirDataReportId;

    public String getScirReportDescription() {
        return mScirReportDescription;
    }

    public void setScirReportDescription(String scirReportDescription) {
        this.mScirReportDescription = scirReportDescription;
    }

    public void appendScirReportDescription(String scirReportDescription) {
        this.mScirReportDescription += scirReportDescription;
    }

    private String mScirReportDescription ;

    public ScirInfraFeedbackPoint() {
    }

    public ScirInfraFeedbackPoint(double mScirDataLat, double mScirDataLong, long mScirDataDateTime,
                              byte []mScirDataCameraImage, byte []mScirDataCameraCompressedImage,
                              SCIR_PROBLEM_TYPE mScirDataProblemType,
                              SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel,
                              String mScirDataMobileNumber, String mScirDataDeviceId,
                                  String mScirImageDimension,
                              String mScirDataFeedbackDescription, String mScirDataReportId)
    {
        this.setScirInfraFeedback(
                mScirDataLat, mScirDataLong, mScirDataDateTime, mScirDataCameraImage, mScirDataCameraCompressedImage,
                mScirDataProblemType, mScirDataProblemSeverityLevel,
                mScirDataMobileNumber, mScirDataDeviceId,
                mScirImageDimension,
                mScirDataFeedbackDescription, mScirDataReportId);
    }

    public void setScirInfraFeedback(double mScirDataLat, double mScirDataLong, long mScirDataDateTime,
                                     byte []mScirDataCameraImage, byte []mScirDataCameraCompressedImage,
                                     SCIR_PROBLEM_TYPE mScirDataProblemType,
                                     SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel,
                                     String mScirDataMobileNumber, String mScirDataDeviceId,
                                     String scirImageDimension,
                                     String mScirDataFeedbackDescription, String mScirDataReportId)
    {
        this.mScirDataLat = mScirDataLat;
        this.mScirDataLong = mScirDataLong;
        this.mScirDataDateTime = mScirDataDateTime;

        this.mScirDataCameraImage = mScirDataCameraImage ;
        this.mScirDataCameraCompressedImage = mScirDataCameraCompressedImage ;
        this.mScirImageDimension = scirImageDimension ;

        this.mScirDataProblemSeverityLevel = mScirDataProblemSeverityLevel;
        this.mScirDataProblemType = mScirDataProblemType;

        this.mScirDataMobileNumber = mScirDataMobileNumber;
        this.mScirDataDeviceId = mScirDataDeviceId;

        this.mScirDataFeedbackDescription = mScirDataFeedbackDescription ;
        this.mScirDataReportId = mScirDataReportId;
    }

    public double getScirDataLat() { return mScirDataLat; }
    public double getScirDataLong() { return mScirDataLong;}
    public long getScirDataDateTime() {return mScirDataDateTime;}
    public String getScirDataId() { return mScirDataReportId; }
    public String getScirDataDateTimeFEServerForamt() {
        Date date = new Date(mScirDataDateTime);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormatter.format(date);
    }
    public byte [] getScirDataCameraCompressedImage() { return mScirDataCameraCompressedImage;}
    public byte[] getmScirDataCameraImage() {        return mScirDataCameraImage; }

    public SCIR_TICKET_SEVERITY getScirDataProblemSeverityLevel() {return mScirDataProblemSeverityLevel;}
    public SCIR_PROBLEM_TYPE getScirDataProblemType() { return mScirDataProblemType;}

    public String getScirDataMobileNumber() {
        return mScirDataMobileNumber;
    }
    public String getScirDataDeviceId() {
        return mScirDataDeviceId;
    }


    public void setScirDataProblemSeverityLevel(float rating) {
        if( rating < 1.0) {
            mScirDataProblemSeverityLevel = ScirInfraFeedbackPoint.SCIR_TICKET_SEVERITY.NoProblem ;
        } else if( rating <= 2.0) {
            mScirDataProblemSeverityLevel = ScirInfraFeedbackPoint.SCIR_TICKET_SEVERITY.Low;
        } else if (rating <= 3.0) {
            mScirDataProblemSeverityLevel = ScirInfraFeedbackPoint.SCIR_TICKET_SEVERITY.Normal;
        } else if (rating <= 4.0 ) {
            mScirDataProblemSeverityLevel = ScirInfraFeedbackPoint.SCIR_TICKET_SEVERITY.High;
        } else if (rating <= 5.0 ) {
            mScirDataProblemSeverityLevel = ScirInfraFeedbackPoint.SCIR_TICKET_SEVERITY.Urgent;
        } else {
            mScirDataProblemSeverityLevel = ScirInfraFeedbackPoint.SCIR_TICKET_SEVERITY.INVALID ;
        }
    }
    public void setScirDataProblemType(int checkedId) {
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

    public boolean collateReport(byte[] mCameraData, byte[] mCameraDataCompressed, TelephonyManager tm) throws Exception {
        // TODO : This is collation of all report contents
        Double lat, lon ;
        long dateTime ;
        String mobileNumber;
        String deviceId;

        mobileNumber = tm.getLine1Number();
        String simNumber = tm.getSimSerialNumber();
        String subNumber = tm.getSubscriberId();

        String uniqueId ;
        if ((mobileNumber != null) && ("".equals(mobileNumber)) == false) {
            uniqueId = "M" + mobileNumber;
        } else if( (simNumber != null) && ("".equals(simNumber) == false)) {
            uniqueId = "S" + simNumber ;
        } else if ((subNumber != null) && ("".equals(subNumber)) == false) {
            uniqueId = "B" + subNumber ;
        } else {
            uniqueId = "UNAVAILABLE" ;
        }

        deviceId = tm.getDeviceId();
        if (deviceId == null) deviceId = "NA";

        if( MainActivity.mScirCurrentLocation == null ) {
            // TODO: Need to handle Errors here !!
            throw new Exception("Location not available!");
        } else {
            lat = MainActivity.mScirCurrentLocation.getLatitude();
            lon = MainActivity.mScirCurrentLocation.getLongitude();
            dateTime = MainActivity.mScirCurrentLocation.getTime();
        }

        setScirInfraFeedback(
                lat, lon, dateTime,
                mCameraData, mCameraDataCompressed,
                getScirDataProblemType(), getScirDataProblemSeverityLevel(),
                uniqueId, deviceId,
                mScirImageDimension,
                "<<Description>>", "");
        return true;
    }

    public String getDataSubmitted() {
        String dataSubmitted =
                "Data Submitted to backend:" +
                        "\nLat:" + getScirDataLat()  +
                        "\nProblem: " + getScirDataProblemType().toString() +
                        "\nSeverity: " + getScirDataProblemSeverityLevel().toString() +
                        "\nMobile :" + getScirDataMobileNumber() +
                        "\nLong:" + getScirDataLong()  +
                        "\nDateTime:" + getScirDataDateTime() +
                        "\nURL:" + getUrlServer() + "\n" +
                        "";
        Log.i("FeedbackPoint", dataSubmitted + this.toString());
        return dataSubmitted ;
    }

    public boolean storeIntoDatabase(ScirSqliteHelper dbHandler) {
        return dbHandler.insertMessage(
                getScirDataDeviceId(),
                getScirDataMobileNumber(),
                getScirDataDateTimeFEServerForamt(),
                (float) getScirDataLat(),
                (float) getScirDataLong(),
                getmScirDataCameraImage().length,
                getScirDataProblemType().toString(),
                getScirDataProblemSeverityLevel().toString(),
                mScirImageDimension,
                (mScirDataCameraCompressedImage == null ) ? mScirDataCameraImage : mScirDataCameraCompressedImage
        );
    }

    public void setUrlServer(String urlServer) { this.mUrlServer = urlServer;}
    public String getUrlServer() { return this.mUrlServer;}


    public String getScirImageDimension() {
        return mScirImageDimension;
    }

    public void setScirImageDimension(String scirImageDimension) {
        this.mScirImageDimension = scirImageDimension;
    }
}
