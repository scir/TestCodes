package org.scir.scir_android_app;

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
        Sewage,
        Water,
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
    private String mScirDataReportId;

    public ScirInfraFeedbackPoint() {
    }

    public ScirInfraFeedbackPoint(double mScirDataLat, double mScirDataLong, long mScirDataDateTime,
                              byte []mScirDataCameraImage, byte []mScirDataCameraCompressedImage,
                              SCIR_PROBLEM_TYPE mScirDataProblemType,
                              SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel,
                              String mScirDataMobileNumber, String mScirDataDeviceId,
                              String mScirDataFeedbackDescription, String mScirDataReportId)
    {
        this.setScirInfraFeedback(
                mScirDataLat, mScirDataLong, mScirDataDateTime, mScirDataCameraImage, mScirDataCameraCompressedImage,
                mScirDataProblemType, mScirDataProblemSeverityLevel,
                mScirDataMobileNumber, mScirDataDeviceId,
                mScirDataFeedbackDescription, mScirDataReportId);
    }

    public void setScirInfraFeedback(double mScirDataLat, double mScirDataLong, long mScirDataDateTime,
                                     byte []mScirDataCameraImage, byte []mScirDataCameraCompressedImage,
                                     SCIR_PROBLEM_TYPE mScirDataProblemType,
                                     SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel,
                                     String mScirDataMobileNumber, String mScirDataDeviceId,
                                     String mScirDataFeedbackDescription, String mScirDataReportId)
    {
        this.mScirDataLat = mScirDataLat;
        this.mScirDataLong = mScirDataLong;
        this.mScirDataDateTime = mScirDataDateTime;

        this.mScirDataCameraImage = mScirDataCameraImage ;
        this.mScirDataCameraCompressedImage = mScirDataCameraCompressedImage ;

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

    public void setUrlServer(String urlServer) { this.mUrlServer = urlServer;}
    public String getUrlServer() { return this.mUrlServer;}

    /*
    public void setScirDataLat(double mScirDataLat) {
        this.mScirDataLat = mScirDataLat;
    }
    public void setScirDataLong(double mScirDataLong) {
        this.mScirDataLong = mScirDataLong;
    }
    public void setScirDataDateTime(long mScirDataDateTime) {
        this.mScirDataDateTime = mScirDataDateTime;
    }
    public void setScirDataProblemSeverityLevel(SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel) {
        this.mScirDataProblemSeverityLevel = mScirDataProblemSeverityLevel;
    }
    public void setScirDataProblemType(SCIR_PROBLEM_TYPE mScirDataProblemType) {
        this.mScirDataProblemType = mScirDataProblemType;
    }
    public void setScirDataMobileNumber(String mScirDataMobileNumber) {
        this.mScirDataMobileNumber = mScirDataMobileNumber;
    }
    public void setScirDataDeviceId(String mScirDataDeviceId) {
        this.mScirDataDeviceId = mScirDataDeviceId;
    }
    */
}
