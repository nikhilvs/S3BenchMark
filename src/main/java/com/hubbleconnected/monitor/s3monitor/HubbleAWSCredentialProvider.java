package com.hubbleconnected.monitor.s3monitor;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class HubbleAWSCredentialProvider implements AWSCredentialsProvider {

    private static final Logger log = Logger.getLogger(HubbleAWSCredentialProvider.class);
    private static AWSCredentialsProvider credentials = null;

    private static  InstanceProfileCredentialsProvider instanceProfileProvider =null;
    static {
        try {
            credentials = new ProfileCredentialsProvider("binatone-key");
            credentials.getCredentials();
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

    public static AWSCredentialsProvider getAWSCredentials() {
        if(instanceProfileProvider!=null)
           return instanceProfileProvider;
        else
           return credentials; 
    }

    @Override
    public AWSCredentials getCredentials() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

}
