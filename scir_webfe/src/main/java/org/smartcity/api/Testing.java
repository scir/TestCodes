package org.smartcity.api;

import java.io.IOException;

import org.smartcity.valueobject.AddTicketResponse;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Testing {

	public static void main(String[] args) {
		testJsonToString();
	}
	
	protected static void testJsonToString(){
		AddTicketResponse resp = new AddTicketResponse(1, 101, "Invalid request. Kindly check parameters!!!");
		
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		try {
			mapper.writeValue(System.out, resp);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
