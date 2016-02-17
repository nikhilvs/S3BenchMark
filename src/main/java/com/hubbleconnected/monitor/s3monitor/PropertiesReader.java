package com.hubbleconnected.monitor.s3monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PropertiesReader 
{
	private static Properties serverConf;
	static Logger log = Logger.getLogger(PropertiesReader.class);
	static File propertyFile ;
	static InputStream inputStream;

	public static String readValueOf(String attribute) {
		try 
		{
			InputStream inputStream = PropertiesReader.class.getResourceAsStream("/cloudwatch-monitor.properties");
			serverConf = new Properties();
			inputStream = new FileInputStream(propertyFile);
			serverConf.load(inputStream);
			return serverConf.getProperty(attribute).trim();

		} catch (Exception e) {
			log.fatal(e,e);
			return null;
		}
		
	}


	public static String readVersionValue()  {
		try
		{
			InputStream inputStream = PropertiesReader.class.getResourceAsStream("/env.config");
			if (inputStream == null)
			{
				throw new FileNotFoundException("property file wowza_server.properties not found in the classpath");
			}
			serverConf = new Properties();
			serverConf.load(inputStream);
			return serverConf.getProperty("version").trim();

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}




	public static void main(String args[]) throws IOException {

		log.info("Log test : "+PropertiesReader.readValueOf("CREDENTIAL_PROFILE_NAME"));
		log.info("Log test : "+PropertiesReader.readValueOf("API_SERVER_ELB"));
		log.info("Log test : "+PropertiesReader.readValueOf("API_SERVER_ASG"));
		log.info("Log test : "+PropertiesReader.readValueOf("CS_SERVER_ELB"));
		log.info("Log test : "+PropertiesReader.readValueOf("CS_SERVER_ASG"));
		log.info("Log test : "+PropertiesReader.readValueOf("RDS_MASTER_NAME"));
		log.info("Log test : "+PropertiesReader.readValueOf("RDS_SLAVE1_NAME"));
		log.info("Log test : "+PropertiesReader.readValueOf("RDS_SLAVE2_NAME"));

	}

}