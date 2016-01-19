package org.scir.scir_android_app;

/**
 * Created by khelender on 06-01-2016.
 */
public class ScirInfraFeedbackPoint {
    private double mScirDataLat, mScirDataLong;
    private long mScirDataDateTime ;
    private CameraActivity.SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel;
    private CameraActivity.SCIR_PROBLEM_TYPE mScirDataProblemType ;
    private String mScirDataMobileNumber ;
    private String mScirDataDeviceId ;
    private String mScirDataFeedbackDescription ;
    private byte [] mScirDataCameraImage ;


    public ScirInfraFeedbackPoint(double mScirDataLat, double mScirDataLong, long mScirDataDateTime,
                                  byte []mScirDataCameraImage,
                                  CameraActivity.SCIR_PROBLEM_TYPE mScirDataProblemType,
                                  CameraActivity.SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel,
                                  String mScirDataMobileNumber, String mScirDataDeviceId,
                                  String mScirDataFeedbackDescription) {
        this.mScirDataLat = mScirDataLat;
        this.mScirDataLong = mScirDataLong;
        this.mScirDataDateTime = mScirDataDateTime;

        this.mScirDataCameraImage = mScirDataCameraImage ;

        this.mScirDataProblemSeverityLevel = mScirDataProblemSeverityLevel;
        this.mScirDataProblemType = mScirDataProblemType;

        this.mScirDataMobileNumber = mScirDataMobileNumber;
        this.mScirDataDeviceId = mScirDataDeviceId;

        this.mScirDataFeedbackDescription = mScirDataFeedbackDescription ;
    }

    public double getScirDataLat() {
        return mScirDataLat;
    }
    public double getScirDataLong() {
        return mScirDataLong;
    }
    public long getScirDataDateTime() {
        return mScirDataDateTime;
    }

    public byte[] getmScirDataCameraImage() {
        return mScirDataCameraImage;
    }

    public CameraActivity.SCIR_TICKET_SEVERITY getScirDataProblemSeverityLevel() {
        return mScirDataProblemSeverityLevel;
    }
    public CameraActivity.SCIR_PROBLEM_TYPE getScirDataProblemType() {
        return mScirDataProblemType;
    }

    public String getScirDataMobileNumber() {
        return mScirDataMobileNumber;
    }
    public String getScirDataDeviceId() {
        return mScirDataDeviceId;
    }


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
    public void setScirDataProblemSeverityLevel(CameraActivity.SCIR_TICKET_SEVERITY mScirDataProblemSeverityLevel) {
        this.mScirDataProblemSeverityLevel = mScirDataProblemSeverityLevel;
    }
    public void setScirDataProblemType(CameraActivity.SCIR_PROBLEM_TYPE mScirDataProblemType) {
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
