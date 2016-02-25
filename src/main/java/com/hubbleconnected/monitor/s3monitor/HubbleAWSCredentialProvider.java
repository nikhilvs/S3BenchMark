package com.hubbleconnected.monitor.s3monitor;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class HubbleAWSCredentialProvider {

    private static final Logger log = Logger.getLogger(HubbleAWSCredentialProvider.class);
    private static AWSCredentials credentials = null;

    private static  InstanceProfileCredentialsProvider instanceProfileProvider =null;
    static {
        try {
            credentials = new ProfileCredentialsProvider("binatone-key").getCredentials();

            log.info("using ProfileCredentialsProvider");
        } catch (Exception e) {
            try {
                instanceProfileProvider = new InstanceProfileCredentialsProvider(true);
                log.info("InstanceProfileCredentialsProvider: ");
            } catch (Exception ex) {
                log.fatal("Failed to load aws credentials :" + e, e);
            }
        }
    }

    public static AWSCredentials getAWSCredentials() {
        if(instanceProfileProvider!=null)
           return instanceProfileProvider.getCredentials();
        else
           return credentials; 
    }
    

}
