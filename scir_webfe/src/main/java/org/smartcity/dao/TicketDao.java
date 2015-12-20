package org.smartcity.dao;

import java.util.List;

import org.smartcity.entity.Ticket;
import org.smartcity.util.paging.Page;
import org.smartcity.valueobject.TicketSearchVO;

public interface TicketDao {

	void addTicket(Ticket ticket);
	Ticket getTicket(Integer ticketId);
	List<Ticket> getTickets(TicketSearchVO criteria);
	Page<Ticket> getTickets(TicketSearchVO criteria, Integer pageNo, Integer pageSize);

}
