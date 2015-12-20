package org.smartcity.entity;

import java.io.Serializable;
import java.util.Date;

public class Ticket implements Serializable {

	private static final long serialVersionUID = 4942624970835693817L;

	private Integer ticketId ;
	private String summary ;
	private String ticketType ;
	private String severity ;
	private String imageName ;
	private String imageUrl ;
	private Double latitude ;
	private Double longitude ;
	private Date ticketTime ;
	
	private String deviceId ;
	private String msisdn ;
	private Date createdOn ;
	private Integer createdBy ;
	private Date updatedOn ;
	private Integer updatedBy ;
	
	public Ticket(){}
	public Ticket(String summary, String ticketType, String severity){
		this.summary=summary; this.ticketType=ticketType; this.severity=severity;
	}
	
	@Override
	public String toString() {
		return "Ticket [ticketId=" + ticketId + ", summary=" + summary + ", ticketType=" + ticketType + ", severity=" + severity
				+ ", imageName=" + imageName + "]";
	}

	// Getter and Setter methods
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Date getTicketTime() {
		return ticketTime;
	}
	public void setTicketTime(Date ticketTime) {
		this.ticketTime = ticketTime;
	}

}
