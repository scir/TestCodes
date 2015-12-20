package org.smartcity.util;

import org.smartcity.util.Constants.TICKET_SEVERITY;
import org.smartcity.util.Constants.TICKET_TYPE;

public class Commons {

	public static void main(String[] args) {
		System.out.println("Result: " + isValidTicketType("Low"));
		System.out.println("Result: " + isValidTicketType(null));
		System.out.println("Result: " + isValidTicketType("Problem"));
		System.out.println("Result: " + isValidTicketType("Suggestion"));
	}
	
	public static boolean isValidSeverity(String severity){
		try {
			TICKET_SEVERITY.valueOf(severity);
			return true ;
		} catch (Exception e) {
			return false ;
		}
	}
	
	public static boolean isValidTicketType(String ticketType){
		try {
			TICKET_TYPE.valueOf(ticketType);
			return true ;
		} catch (Exception e) {
			return false ;
		}
	}
	
}
