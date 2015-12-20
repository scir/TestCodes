package org.smartcity.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Bean Factory provides access to Application Context and Application Properties.
 * 
 * @author kuntal.kumar
 *
 */
public class BeanFactory implements ApplicationContextAware {

	private static Logger log = LoggerFactory.getLogger(BeanFactory.class);
	
	private static ApplicationContext ctx;
	private static Properties props;
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		BeanFactory.ctx = appContext;
		BeanFactory.props = (Properties) appContext.getBean("appProperties");
	}

	/**
	 * This method is used to fetch a Bean from Application Context with the specified name.
	 * 
	 * @param name Name of the Bean to be fetched.
	 * @return Bean from Application Context.
	 */
	public static Object getBean(String name){
		if (ctx==null){
			log.error("Context Not Initialized.");
			return null ;
		}
		return ctx.getBean(name);
	}

	/**
	 * This method is used to fetch value of specified property.
	 * 
	 * @param key Key of the property whose value is required. 
	 * @return Property value of specified Key.
	 */
	public static String getProperty(String key){
		if (BeanFactory.props == null) {
			return null;
		}
		return BeanFactory.props.getProperty(key);
	}
	
}
