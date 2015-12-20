package org.smartcity.api;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartcity.entity.Ticket;
import org.smartcity.exception.ValidationException;
import org.smartcity.service.TicketService;
import org.smartcity.util.BeanFactory;
import org.smartcity.util.Commons;
import org.smartcity.util.Constants;
import org.smartcity.util.DateUtils;
import org.smartcity.util.StringUtils;
import org.smartcity.util.paging.Page;
import org.smartcity.valueobject.TicketSearchVO;

public class TicketAPI {

	private static Logger log = LoggerFactory.getLogger(TicketAPI.class);
	private static final boolean IS_SUMMARY_MANDATORY = BeanFactory.getProperty("ticket.summary.mandatory") == "1" ? true : false ;
	private static final String VALIDATION_ERROR_GENERIC = BeanFactory.getProperty("api.upload.error.validation.generic") ;
	
	TicketService ticketService ;
	//private String IMAGES_BASE_PATH = "" ; // Need to get it from props file.

	public List<Ticket> getTickets(TicketSearchVO criteria){
		return ticketService.getTickets(criteria);
	}
	
	public Page<Ticket> getTickets(TicketSearchVO criteria, Integer pageNo, Integer pageSize){
		return ticketService.getTickets(criteria, pageNo, pageSize);
	}
	
	public void addTicket(Ticket ticket){
		log.info("Adding Ticket: "+ ticket);
		validateTicket(ticket, true);
		//validateImage(ticket);
		ticketService.addTicket(ticket);
	}

	public void validateTicket(Ticket ticket, boolean validateImage){
		if (ticket==null){
			log.debug("One of the mandatory attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_TICKET, VALIDATION_ERROR_GENERIC) ;
		}
		
		if (ticket.getSummary()==null || (IS_SUMMARY_MANDATORY && StringUtils.isEmpty(ticket.getSummary()))){
			log.debug("Summary attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_SUMMARY, VALIDATION_ERROR_GENERIC) ;
		}

		if (StringUtils.isEmpty(ticket.getSeverity())){
			log.debug("Severity attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_SEVERITY, VALIDATION_ERROR_GENERIC) ;
		}else if(!Commons.isValidSeverity(ticket.getSeverity())) {
			log.debug("Severity attribute not valid.");
			throw new ValidationException(Constants.VALIDATION_INVALID_SEVERITY, VALIDATION_ERROR_GENERIC) ;
		}

		if (StringUtils.isEmpty(ticket.getTicketType())){
			log.debug("Ticket Type attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_TYPE, VALIDATION_ERROR_GENERIC) ;
		}else if (!Commons.isValidTicketType(ticket.getTicketType())){
			log.debug("Ticket Type attribute not valid.");
			throw new ValidationException(Constants.VALIDATION_INVALID_SEVERITY, VALIDATION_ERROR_GENERIC) ;
		}

		if (ticket.getLatitude()==null){
			log.debug("Latitude attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_LATITUDE, VALIDATION_ERROR_GENERIC) ;
		}

		if (ticket.getLongitude()==null){
			log.debug("Longitude attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_LONGITUDE, VALIDATION_ERROR_GENERIC) ;
		}

		if (ticket.getTicketTime()==null){
			log.debug("Ticket Time attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_TIME, VALIDATION_ERROR_GENERIC) ;
		}

		if (StringUtils.isEmpty(ticket.getMsisdn())){
			log.debug("MSISDN attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_MSISDN, VALIDATION_ERROR_GENERIC) ;
		}

		if (StringUtils.isEmpty(ticket.getDeviceId())){
			log.debug("DeviceId attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_DEVICE_ID, VALIDATION_ERROR_GENERIC) ;
		}

		if (validateImage){
			validateImage(ticket);
		}
		
	}

	public void validateImage(Ticket ticket){
		if (StringUtils.isEmpty(ticket.getImageName()) ){
			log.debug("Image attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_IMAGE, VALIDATION_ERROR_GENERIC) ;
		}
	}

	public Double validateLatitude(String input){
		try {
			return Double.valueOf(input);
		} catch (Exception e) {
			log.debug("Latitude attribute not valid.");
			throw new ValidationException(Constants.VALIDATION_NULL_LATITUDE, VALIDATION_ERROR_GENERIC) ;
		}
	}

	public Double validateLongitude(String input){
		try {
			return Double.valueOf(input);
		} catch (Exception e) {
			log.debug("Longitude attribute not valid.");
			throw new ValidationException(Constants.VALIDATION_NULL_LONGITUDE, VALIDATION_ERROR_GENERIC) ;
		}
	}

	public Date validateTime(String input){
		if (input==null){
			log.debug("Ticket Time attribute not found.");
			throw new ValidationException(Constants.VALIDATION_NULL_TIME, VALIDATION_ERROR_GENERIC) ;
		}
		
		if (!DateUtils.isValidDate(input, Constants.API_DATE_FORMAT)){
			log.debug("Ticket Time attribute not valid.");
			throw new ValidationException(Constants.VALIDATION_INVALID_TIME, "Mandatory attribute not found or invalid.") ;
		}
		
		return DateUtils.getDate(input, Constants.API_DATE_FORMAT);
	}

	public String getImageLocation(){
		return StringUtils.getTodaysDateString() ;
	}

	public void createImageLocation(String path){
		File uploadDir = new File(path);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
	}

	// Getter and Setter methods
	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

}
