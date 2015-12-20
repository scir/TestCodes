package org.smartcity.web.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.tomcat.util.http.fileupload.FileItem;
//import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
//import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartcity.api.TicketAPI;
import org.smartcity.entity.Ticket;
import org.smartcity.exception.DatabaseException;
import org.smartcity.exception.ValidationException;
import org.smartcity.util.BeanFactory;
import org.smartcity.util.Constants;
import org.smartcity.util.StringUtils;
import org.smartcity.valueobject.AddTicketResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AddTicketServlet extends HttpServlet {

//	import org.apache.tomcat.util.http.fileupload.FileItem;
//	import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
//	import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

	private static final long serialVersionUID = -3267768264680743304L;
	private static Logger log = LoggerFactory.getLogger(AddTicketServlet.class);

	private TicketAPI ticketAPI = (TicketAPI) BeanFactory.getBean("ticketAPI");

	private static final String UPLOAD_PATH = BeanFactory.getProperty(Constants.PROPS_KEY_UPLOAD_PATH);
	
	private static final int THRESHOLD_SIZE 	= 1024 * 1024 * 3; 	//  3MB
	private static final int MAX_FILE_SIZE 		= 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE 	= 1024 * 1024 * 50; // 50MB

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// File location where images to be stored...
		//UPLOAD_DIRECTORY = getServletContext().getInitParameter(Constants.PROPS_KEY_UPLOAD_PATH);
		
		// constructs the directory path to store upload file
		// String uploadPath = UPLOAD_DIRECTORY ; // getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
		
		// creates the directory if it does not exist
		File uploadDir = new File(UPLOAD_PATH);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Validate Request Type validateRequestType(request);
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new ValidationException(Constants.RC_VALIDATION_INVALID_REQUEST_TYPE, Constants.PROPS_KEY_UPLOAD_ERROR_INVALID_REQ_TYPE);
			}
			
			response.setContentType("application/json");
			doProcessing(request, response);

		} catch (ValidationException e) {
			log.error("Unable to add Ticket. Validation Exception occurred. Exception Code:  " + e.getErrorCode() + ", Error Message: " + e.getMessage());
			AddTicketResponse addResponse = new AddTicketResponse(Constants.STATUS_FAILURE, e.getErrorCode(), e.getMessage());
			setJsonResponse(addResponse, response.getWriter());
		} catch (Exception e) {
			log.error("Unable to add Ticket: "+ e.getMessage(), e);
			AddTicketResponse addResponse = new AddTicketResponse(Constants.STATUS_FAILURE, Constants.RC_ERROR_GENEREAL, BeanFactory.getProperty(Constants.PROPS_KEY_UPLOAD_ERROR_GENERIC));
			setJsonResponse(addResponse, response.getWriter());
		}
		
	}

	private void doProcessing(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(THRESHOLD_SIZE);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// parses the request's content to extract file data
		List<FileItem> formItems = upload.parseRequest(request);

		Ticket ticket = initializeTicket(formItems);
		uploadImage(formItems, ticket);

		AddTicketResponse addResponse = addTicket(ticket);
		setJsonResponse(addResponse, response.getWriter());
		
//		Iterator<FileItem> iter = formItems.iterator();
//		// iterates over form's fields
//		while (iter.hasNext()) {
//			FileItem item = iter.next();
//
//			// processes only fields that are not form fields
//			if (!item.isFormField()) {
//				
//				String fileName = new File(item.getName()).getName();
//				String filePath = uploadPath + File.separator + fileName;
//				File storeFile = new File(filePath);
//
//				// saves the file on disk
//				item.write(storeFile);
//				
//				// add image information in ticket
//				ticket.setImageName(fileName);	//	(BASE_IMAGE_URL + fileName);
//				log.info("Setting Image URL as : " + ticket.getImageName());
//			}else{
//				//System.out.println("FieldName: " + item.getFieldName() + ", GetName: " + item.getName() + ", String: " + item.getString());
//				
//				if (item.getFieldName().equalsIgnoreCase("summary")){
//					ticket.setSummary(item.getString());
//				}
//				if (item.getFieldName().equalsIgnoreCase("ticketType")){
//					ticket.setTicketType(item.getString());
//				}
//				if (item.getFieldName().equalsIgnoreCase("severity")){
//					ticket.setSeverity(item.getString());
//				}
//			}
//		}
//		
//		return ticket ;
	}

	private AddTicketResponse addTicket(Ticket ticket){
		try {
			ticket.setCreatedBy(1); ticket.setUpdatedBy(1); // TODO: replace this later with user info in session 
			
			ticketAPI.addTicket(ticket);
			log.info("Ticket added successfully.");
			return new AddTicketResponse(Constants.STATUS_SUCCESS, Constants.RC_SUCCESS, "Ticket added with id: " + ticket.getTicketId(), ticket.getTicketId());
		} catch (ValidationException e) {
			return new AddTicketResponse(Constants.STATUS_FAILURE, e.getErrorCode(), e.getMessage());
		} catch (DatabaseException e) {
			log.error("Database Exception: Unable to add Ticket: "+ e.getMessage(), e);
			return new AddTicketResponse(Constants.STATUS_FAILURE, Constants.RC_ERROR_DATABASE, BeanFactory.getProperty(Constants.PROPS_KEY_UPLOAD_ERROR_DATABASE));
		} catch (Exception e) {
			log.error("Generic Exception: Unable to add Ticket: "+ e.getMessage(), e);
			return new AddTicketResponse(Constants.STATUS_FAILURE, Constants.RC_ERROR_GENEREAL, BeanFactory.getProperty(Constants.PROPS_KEY_UPLOAD_ERROR_GENERIC));
		}
	}

	private Ticket initializeTicket(List<FileItem> formItems){
		Ticket ticket = new Ticket();
		
		// iterates over form's fields
		Iterator<FileItem> iter = formItems.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();

			// processes only fields that are form fields
			if (item.isFormField()) {
				//System.out.println("FieldName: " + item.getFieldName() + ", GetName: " + item.getName() + ", String: " + item.getString());
				
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_SUMMARY)){
					ticket.setSummary(item.getString());
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_TYPE)){
					ticket.setTicketType(item.getString());
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_SEVERITY)){
					ticket.setSeverity(item.getString());
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_DEVICE_ID)){
					ticket.setDeviceId(item.getString());
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_MSISDN)){
					ticket.setMsisdn(item.getString());
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_LATITUDE)){
					ticket.setLatitude(ticketAPI.validateLatitude(item.getString()));
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_LONGITUDE)){
					ticket.setLongitude(ticketAPI.validateLongitude(item.getString()));
				}
				if (item.getFieldName().equalsIgnoreCase(Constants.UR_PARAM_TIME)){
					ticket.setTicketTime(ticketAPI.validateTime(item.getString()));
				}
			}
		}
		
		ticketAPI.validateTicket(ticket, false);
		return ticket ;
	}
	
	private void uploadImage(List<FileItem> formItems, Ticket ticket) throws Exception{

		// iterates over form's fields
		Iterator<FileItem> iter = formItems.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();

			// processes only fields that are not form fields
			if (!item.isFormField()) {
				
				System.out.println("Item Name: " + item.getName());

				// Check File Name Parameter
				if (!Constants.UR_PARAM_IMAGE.equals(item.getFieldName())){
					continue ;
				}

				// File should be specified, else break
				if (StringUtils.isEmpty(item.getName())){
					break ;
				}
				
				//String fileName = new File(item.getName()).getName();
				String fileName = getUploadFileName(ticket.getMsisdn(), item.getName());
				String imageLocation = ticketAPI.getImageLocation() ;	// Relative Location corresponding to Rendering context.
				String filePath = UPLOAD_PATH + imageLocation;
				ticketAPI.createImageLocation(filePath);

//				File storeFile = new File(filePath + Constants.FILE_SEPARATOR + fileName);
				File storeFile = new File(filePath + Constants.FILE_SEPARATOR + "TestFile.png");

				// saves the file on disk
				item.write(storeFile);
				
				// add image information in ticket
				ticket.setImageName(fileName);
				ticket.setImageUrl(imageLocation + Constants.FILE_SEPARATOR + fileName);
				log.info("Setting Image URL as : " + ticket.getImageUrl());
				
				break ;		//check whether break is required or not
			}
		}
		
		ticketAPI.validateImage(ticket);
	}

	private void setJsonResponse(AddTicketResponse addResponse, PrintWriter out){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		//mapper.setSerializationInclusion(Include.NON_EMPTY);
		try {
			//mapper.writeValue(System.out, addResponse);
			mapper.writeValue(out, addResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getUploadFileName(String msisdn, String fileName){
		return msisdn + "_" + StringUtils.getRandomFileName() + "_" + fileName ;
	}
	
//	private void validateRequestType(HttpServletRequest request) throws IOException{
//		// checks if the request actually contains upload file
//		if (!ServletFileUpload.isMultipartContent(request)) {
//			throw new ValidationException(Constants.RC_VALIDATION_INVALID_REQUEST_TYPE, "Invalid Request received.");
////			AddTicketResponse addResponse = new AddTicketResponse(Constants.STATUS_FAILURE, Constants.RC_VALIDATION_INVALID_REQUEST_TYPE, );
////			PrintWriter out = response.getWriter();
////			setJsonResponse(addResponse, out);
////			out.flush();
////			return false;
//		}
////		return true ;
//	}
	
//	private boolean validRequest(HttpServletRequest request, HttpServletResponse response, Ticket ticket) throws IOException {
//		try {
//			ticketAPI.validateTicket(ticket, true);
//			return true ;
//		} catch (ValidationException e) {
//			AddTicketResponse addResponse = new AddTicketResponse(Constants.STATUS_FAILURE, e.getErrorCode(), e.getMessage());
//			PrintWriter out = response.getWriter();
//			setJsonResponse(addResponse, out);
//			return false ;
//		}
//	}

//	private void setInvalidRequestResponse(PrintWriter out, AddTicketResponse addResponse){
//		out.print(addResponse);
//		
////		if (INVALID_HTML_RESPONSE){
////			out.println("<html>");
////	        out.println("<head>");
////	        out.println("<title>Servlet upload</title>");  
////	        out.println("</head>");
////	        out.println("<body>");
////	        out.println("<p>No file uploaded</p>"); 
////	        out.println("</body>");
////	        out.println("</html>");
////		}else{
////			out.println("Request does not contain upload data");
////		}
//	}
//	
	
}