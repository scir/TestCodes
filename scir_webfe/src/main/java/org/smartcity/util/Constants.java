package org.smartcity.util;

public class Constants {

	public static enum TICKET_TYPE {INVALID, Electricity, Road, Sanitation, Sewage, Water};
	public static enum TICKET_SEVERITY {INVALID, Low, Normal, High, Urgent};
	
	public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	public static final String API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;

	public static final String FILE_SEPARATOR = "/" ;
	
	// Property File Keys
	public static final String PROPS_KEY_UPLOAD_PATH = "images.upload.location" ;
	public static final String PROPS_KEY_UPLOAD_ERROR_INVALID_REQ_TYPE = "api.upload.error.invalid-request-type" ;
	public static final String PROPS_KEY_UPLOAD_ERROR_GENERIC = "api.upload.error.generic" ;
	public static final String PROPS_KEY_UPLOAD_ERROR_DATABASE = "api.upload.error.database" ;
	
	// Upload Request Parameters
	public static final String UR_PARAM_SUMMARY = "summary" ;
	public static final String UR_PARAM_TYPE = "type" ;
	public static final String UR_PARAM_SEVERITY = "severity" ;
	public static final String UR_PARAM_MSISDN = "msisdn" ;
	public static final String UR_PARAM_DEVICE_ID = "deviceId" ;
	public static final String UR_PARAM_IMAGE = "imgFile" ;
	public static final String UR_PARAM_LATITUDE = "latitude" ;
	public static final String UR_PARAM_LONGITUDE = "longitude" ;
	public static final String UR_PARAM_TIME = "time" ;
	
	// Upload Request Status Code
	public static final Integer STATUS_SUCCESS = 0 ;
	public static final Integer STATUS_FAILURE = 1 ;
	
	// Upload Request Response Codes
	public static final Integer RC_SUCCESS = 0 ;
	public static final Integer RC_VALIDATION_INVALID_REQUEST_TYPE = 101 ;
	public static final Integer VALIDATION_NULL_TICKET = 102 ;
	public static final Integer VALIDATION_NULL_SUMMARY = 103 ;
	public static final Integer VALIDATION_NULL_SEVERITY = 104 ;
	public static final Integer VALIDATION_INVALID_SEVERITY = 105 ;
	public static final Integer VALIDATION_NULL_TYPE = 106 ;
	public static final Integer VALIDATION_INVALID_TYPE = 107 ;
	public static final Integer VALIDATION_NULL_IMAGE = 108 ;
	public static final Integer VALIDATION_NULL_LATITUDE = 109 ;
	public static final Integer VALIDATION_NULL_LONGITUDE = 110 ;
	public static final Integer VALIDATION_NULL_TIME = 111 ;
	public static final Integer VALIDATION_INVALID_TIME = 112 ;
	public static final Integer VALIDATION_NULL_MSISDN = 113 ;
	public static final Integer VALIDATION_NULL_DEVICE_ID = 114 ;
	
	public static final Integer RC_ERROR_GENEREAL = 201 ;
	public static final Integer RC_ERROR_DATABASE = 202 ;
	
}
