package org.smartcity.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartcity.dao.TicketDao;
import org.smartcity.entity.Ticket;
import org.smartcity.exception.DatabaseException;
import org.smartcity.util.DateUtils;
import org.smartcity.util.StringUtils;
import org.smartcity.util.paging.Page;
import org.smartcity.util.paging.PaginationHelper;
import org.smartcity.valueobject.TicketSearchVO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class TicketDaoImpl implements TicketDao {

	private static Logger log = LoggerFactory.getLogger(TicketDaoImpl.class);

	static final String DB_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	static final String DB_DATE_FORMAT = "yyyy-MM-dd" ;
	
	private JdbcTemplate jdbcTemplate ;
	private SimpleJdbcInsert insertRow ;

	@Override
	public void addTicket(Ticket ticket) {
		log.info("Adding Ticket (via TicketDaoImpl)...");
		
		Map<String, Object> parameters = new HashMap<String, Object>(8);
        parameters.put("summary", ticket.getSummary());
        parameters.put("ticket_type", ticket.getTicketType());
        parameters.put("severity", ticket.getSeverity());
        parameters.put("image_url", ticket.getImageUrl());
        
        parameters.put("latitude", ticket.getLatitude());
        parameters.put("longitude", ticket.getLongitude());
        parameters.put("ticket_time", ticket.getTicketTime());
        
        parameters.put("msisdn", ticket.getMsisdn());
        parameters.put("device_id", ticket.getDeviceId());
        parameters.put("created_by", ticket.getCreatedBy());
        parameters.put("updated_by", ticket.getUpdatedBy());
        
        Date currentTime = new Date();
        parameters.put("created_on", currentTime);
        parameters.put("updated_on", currentTime);

		try {
			Integer newId = (int) (long) (Long) insertRow.executeAndReturnKey(parameters);
			ticket.setTicketId(newId);
			ticket.setCreatedOn(currentTime);
			ticket.setUpdatedOn(currentTime);
		} catch (Exception e) {
			log.error("Database Exception occurred. " + e.getMessage(), e);
			throw new DatabaseException("Unable to add Ticket. " + e.getMessage());
		}
	}

	@Override
	public Ticket getTicket(Integer ticketId){
		log.debug("getTicket of TicketDaoImpl called...");
		
		if (ticketId==null || ticketId <= 0){
			return null ;
		}

		String sql = "Select ticket_id, summary, ticket_type, severity, image_url, latitude, longitude, ticket_time, msisdn, device_id, created_by, created_on, updated_by, updated_on " + 
				" FROM sci_tickets WHERE ticket_id = " + ticketId ;
		
		log.debug("SQL: " + sql);

		Ticket ticket = new Ticket();
		try {
			ticket = jdbcTemplate.queryForObject(sql, new TicketRowMapper(), ticketId);
		} catch (EmptyResultDataAccessException e) {
			log.error("EmptyResultDataAccessException occurred. getTicket returning NULL.");
			return null;
		}
		
		log.debug("getTicket returning Ticket.");
		return ticket ;
	}

	@Override
	public List<Ticket> getTickets(TicketSearchVO criteria){
		log.debug("getTickets of TicketDaoImpl called...");
		ArrayList<Ticket> docs = new ArrayList<Ticket>();

		String params = getCriteria(criteria);
		String sql = "Select ticket_id, summary, ticket_type, severity, image_url, latitude, longitude, ticket_time, msisdn, device_id, created_by, created_on, updated_by, updated_on " + 
				" FROM sci_tickets " + params.toString() ;

		log.debug("sql: " + sql);
		
		docs = (ArrayList<Ticket>) jdbcTemplate.query(sql, new TicketRowMapper());
		log.debug("GetDocuments returning Documents.");
		return docs ;
	}

	@Override
	public Page<Ticket> getTickets(TicketSearchVO criteria, final Integer pageNo, final Integer pageSize){
		PaginationHelper<Ticket> ph = new PaginationHelper<Ticket>();
		
		String params = getCriteria(criteria);
		String sqlCountRows = "SELECT count(1) FROM sci_tickets " + params.toString() ;
		String sqlFetchRows = "SELECT ticket_id, summary, ticket_type, severity, image_url, latitude, longitude, ticket_time, msisdn, device_id, created_by, created_on, updated_by, updated_on " + 
								" FROM sci_tickets " + params.toString() ;

		log.debug("sqlCount: " + sqlCountRows);
		log.debug("sqlFetch: " + sqlFetchRows);
		
		return ph.fetchPage(
					jdbcTemplate, 
					sqlCountRows, 
					sqlFetchRows, 
					null, pageNo, pageSize, new TicketRowMapper()
				);
	}

	private String getCriteria(TicketSearchVO criteria){
		String andOperand = " AND " ;
		StringBuffer params = new StringBuffer("");
		if (criteria!=null){
			if (StringUtils.isNotEmpty(criteria.getTicketId())){
				params.append(andOperand + "ticket_id = " + criteria.getTicketId()) ;
			}
			if (StringUtils.isNotEmpty(criteria.getMsisdn())){	// Exact Match for MSISDN
				//params.append(andOperand + "msisdn LIKE '%" + criteria.getMsisdn() + "%' ") ;
				params.append(andOperand + "msisdn = '" + criteria.getMsisdn() + "' ") ;
			}
			if (StringUtils.isNotEmpty(criteria.getSummary())){
				params.append(andOperand + "summary LIKE '%" + criteria.getSummary() + "%'") ;
			}
			if (StringUtils.isNotEmpty(criteria.getTicketType())){
				params.append(andOperand + "ticket_type LIKE '%" + criteria.getTicketType() + "%' ") ;
			}
			if (StringUtils.isNotEmpty(criteria.getSeverity())){
				params.append(andOperand + "severity LIKE '%" + criteria.getSeverity() + "%' ") ;
			}
			if (criteria.getStartDate()!=null){
				params.append(andOperand + "created_on >= '" + DateUtils.formatDate(criteria.getStartDate(), DB_DATE_FORMAT, null) + "'") ;
			}
			if (criteria.getEndDate()!=null){
				params.append(andOperand + "created_on <= '" + DateUtils.formatDate(criteria.getEndDate(), DB_DATE_FORMAT, null) + "'") ;
			}
		}
		
		return params.length()>0 ? (" WHERE " + params.substring(andOperand.length())) : "" ;
	}

	// Getter and Setter Methods
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
		this.insertRow = new SimpleJdbcInsert(dataSource)
		.withTableName("sci_tickets")
		.usingGeneratedKeyColumns("ticket_id");
	}

}

class TicketRowMapper implements RowMapper<Ticket> {

	@Override
	public Ticket mapRow(ResultSet rs, int arg1) throws SQLException {

		Ticket entity = new Ticket();
		entity.setTicketId(rs.getInt("ticket_id"));
		entity.setSummary(rs.getString("summary"));
		entity.setTicketType(rs.getString("ticket_type"));
		entity.setSeverity(rs.getString("severity"));
		entity.setImageUrl(rs.getString("image_url"));
		entity.setLatitude(rs.getDouble("latitude"));
		entity.setLongitude(rs.getDouble("longitude"));
		entity.setTicketTime(rs.getDate("ticket_time"));
		entity.setMsisdn(rs.getString("msisdn"));
		entity.setDeviceId(rs.getString("device_id"));
		entity.setCreatedOn(rs.getDate("created_on"));
		entity.setCreatedBy(rs.getInt("created_by"));
		entity.setUpdatedOn(rs.getDate("updated_on"));
		entity.setUpdatedBy(rs.getInt("updated_by"));
		return entity;
	}

}