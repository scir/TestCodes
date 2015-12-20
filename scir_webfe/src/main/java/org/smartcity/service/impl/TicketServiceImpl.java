package org.smartcity.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartcity.dao.TicketDao;
import org.smartcity.entity.Ticket;
import org.smartcity.service.TicketService;
import org.smartcity.util.paging.Page;
import org.smartcity.valueobject.TicketSearchVO;

public class TicketServiceImpl implements TicketService {

	private static Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

	TicketDao ticketDao ;
	
	@Override
	public void addTicket(Ticket ticket) {
		log.debug("Got Ticket to Add: " + ticket);
		ticketDao.addTicket(ticket);
	}

	@Override
	public Ticket getTicket(Integer ticketId){
		log.debug("Get Ticket for Id: " + ticketId);
		return ticketDao.getTicket(ticketId);
	}
	
	@Override
	public List<Ticket> getTickets(TicketSearchVO criteria) {
		log.debug("Get Tickets for TicketSearchVO: " + criteria);
		return ticketDao.getTickets(criteria);
	}

	@Override
	public Page<Ticket> getTickets(TicketSearchVO criteria, Integer pageNo, Integer pageSize) {
		log.debug("Get Page wise Tickets for TicketSearchVO: " + criteria);
		return ticketDao.getTickets(criteria, pageNo, pageSize);
	}

	// Getter and Setter methods
	public void setTicketDao(TicketDao ticketDao) {
		this.ticketDao = ticketDao;
	}

}