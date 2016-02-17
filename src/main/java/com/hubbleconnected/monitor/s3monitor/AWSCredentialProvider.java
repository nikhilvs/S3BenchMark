package com.hubbleconnected.monitor.s3monitor;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class AWSCredentialProvider
{

	private static final Logger	log				= Logger.getLogger(AWSCredentialProvider.class);
	private static AWSCredentials		credentials		= null;

	static
	{
		try
		{
			credentials = new ProfileCredentialsProvider("binatone-key").getCredentials();
			log.info("using ProfileCredentialsProvider");
		} catch (Exception e)
		{
			try
			{
				credentials = new InstanceProfileCredentialsProvider().getCredentials();
				log.info("InstanceProfileCredentialsProvider: " + credentials.getAWSAccessKeyId());
			} catch (Exception ex)
			{
				log.fatal("Failed to load aws credentials :" + e, e);
			}
		}
	}

	

	public static AWSCredentials getAWSCredentials()
	{
		return credentials;
	}


}
