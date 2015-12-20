package org.smartcity.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartcity.api.TicketAPI;
import org.smartcity.entity.Ticket;
import org.smartcity.exception.DatabaseException;
import org.smartcity.exception.ValidationException;
import org.smartcity.util.BeanFactory;
import org.smartcity.util.paging.Page;
import org.smartcity.valueobject.TicketSearchVO;

public class TestUsage extends BaseMain {

	private static final Logger log = LoggerFactory.getLogger(TestUsage.class);
	private TicketAPI ticketAPI = (TicketAPI) BeanFactory.getBean("ticketAPI");

	public static void main(String[] args) {
		log.info("Testing Main");

		new TestUsage().testAPI();
		log.info("Done...");
	}

	public void testAPI(){
		testUsage();
	}


	protected void testUsage(){
		addTickets();
		//fetchTickets();
	}

	protected void addTickets(){
		Ticket input ;

		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.APRIL, 26);
		for (int i=1; i<=1 ; i++){
			cal.add(Calendar.MONTH, 1);
			input = getTicket(i, cal.getTime());
			log.info("");
			try {
				ticketAPI.addTicket(input);
				log.info("Added Ticket, iteration #" + i);
			} catch (ValidationException e) {
				log.error("ValidationException occurred: " + e.getMessage());
			} catch (DatabaseException e) {
				log.error("DatabaseException occurred: " + e.getMessage());
			} catch (Exception e) {
				log.error("Exception occurred: "+e.getMessage(), e);
			}
			
			if (i==8){
				input.setSeverity("Urgent");
//				ticketAPI.updateTicket(input);
//				log.info("Updated Ticket, iteration #" + i);
			}
		}
	}
	
	protected void fetchTickets(){
		log.info("<br><br>");
		TicketSearchVO search = new TicketSearchVO();
		//search.setSummary("Tree");
		//search.setTicketType("Problem");
		search.setMsisdn("9891100100");
		search.setSeverity("High");
		//search.setStartDate(new Date());
		
		int pageNo = 1, pageSize = 2 ;
		
		Page<Ticket> list ;
		while (true){
			list = ticketAPI.getTickets(search, pageNo, pageSize);
			
			System.out.println("\n\nTotal Records: " + list.getRowCount() + ". Displaying Page "+ list.getPageNumber() +" of "+list.getPagesAvailable());
			displayTickets(list);
			
			// check for last page
			if (list==null || list.getPageItems()==null || list.getPageItems().size()==0 || list.getPageNumber()==list.getPagesAvailable()){
				break ;
			}
			
			pageNo++ ;
		}
		
	}
	
	protected Ticket getTicket(int id, Date date){
		Ticket input = new Ticket();
		//input.setTicketId(String.valueOf(id));
		//input.setTicketId(id);
		input.setSummary("Ticket-0000"+id);
		input.setTicketType("Problem");
		input.setSeverity("High");
		input.setCreatedBy(1);input.setUpdatedBy(1);
		input.setImageName("Tree.jpg");
		input.setMsisdn("9891100100");
		input.setDeviceId("DEV-OPO-343534654");
		return input ;
	}

	protected void displayTickets(Page<Ticket> page){
		displayTickets(page==null ? new ArrayList<Ticket>() : page.getPageItems());
	}
	
	protected void displayTickets(List<Ticket> list){
		try {
			System.out.println("Ticket List");
			
			if (list==null || list.size()<1){
				System.out.println("No Ticket Found.");
			}
			
			for (Ticket doc : list){
				System.out.println("Ticket: " + doc);
			}
		} catch (Exception e) {
			log.error("Error Occurred: " + e.getMessage(), e);
		}
		
	}

}
