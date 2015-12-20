package org.smartcity.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseMain {

	private static final String[] APP_CTX_FILES = {"applicationContext-resources.xml", "applicationContext.xml"} ;
	protected static final Logger log = LoggerFactory.getLogger(BaseMain.class);
	protected static ApplicationContext ctx ;
	
	static {
		init();
	}
	
	public static void main(String[] args) {
		//init();
	}

	protected static void init (){
		System.out.println("Loading Context...");
		log.info("");
		log.info("*********************************************************************");
		log.info("*********************************************************************");
		log.info("Loading Application Context...");
		loadApplicationContext();
		log.info("Application Context Loaded.");		
		log.info("*********************************************************************");
		log.info("*********************************************************************");
		log.info("");
		System.out.println("Context Loaded.");
	}
	
	private static void loadApplicationContext(){
		// check whether with multiple context files provision, standalone context file can be separately added.
		//String[] paths = APP_CTX_FILES ;
		ctx = new ClassPathXmlApplicationContext(APP_CTX_FILES);
	}

}
