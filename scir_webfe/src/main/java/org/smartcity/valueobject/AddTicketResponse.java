package org.smartcity.valueobject;

import java.io.Serializable;

public class AddTicketResponse implements Serializable {

	private static final long serialVersionUID = 2396276598518250890L;

	private Integer status ;
	private Integer responseCode ;
	private String message ;
	private Integer uploadId ;
	
	public AddTicketResponse(){}
	public AddTicketResponse(Integer status, Integer responseCode, String message){
		this.status=status;	this.responseCode=responseCode;	this.message=message;
	}
	public AddTicketResponse(Integer status, Integer responseCode, String message, Integer uploadId){
		this(status,responseCode,message);	this.uploadId=uploadId;
	}

	public String toString(){
		return "status: " + status + ", responseCode: " + responseCode + ", id: " + uploadId + ", message: " + message ;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getUploadId() {
		return uploadId;
	}
	public void setUploadId(Integer uploadId) {
		this.uploadId = uploadId;
	}

}
