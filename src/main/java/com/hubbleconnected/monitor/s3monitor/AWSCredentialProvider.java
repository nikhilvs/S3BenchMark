package com.hubbleconnected.monitor.s3monitor;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;

public class AWSCredentialProvider {

    private static final Logger log = Logger.getLogger(AWSCredentialProvider.class);
    private static AWSCredentials credentials = null;

    static {
        try {
            credentials = new ProfileCredentialsProvider("binatone-key").getCredentials();

            log.info("using ProfileCredentialsProvider");
        } catch (Exception e) {
            try {

//                AWSCredentialsProvider provider = new InstanceProfileCredentialsProvider(true);
//                AWSSecurityTokenServiceClient stsClient
//                        = new AWSSecurityTokenServiceClient(new InstanceProfileCredentialsProvider());
//                GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest();
//                getSessionTokenRequest.setDurationSeconds(7200);
//                GetSessionTokenResult sessionTokenResult
//                        = stsClient.getSessionToken(getSessionTokenRequest);
//                Credentials sessionCredentials = sessionTokenResult.getCredentials();
//                
//                BasicSessionCredentials basicSessionCredentials
//                        = new BasicSessionCredentials(sessionCredentials.getAccessKeyId(),
//                                sessionCredentials.getSecretAccessKey(),
//                                sessionCredentials.getSessionToken());
////                sessionCredentials.
//                AmazonS3Client s3 = new AmazonS3Client(basicSessionCredentials);
//                TransferManager m = new TransferManager(basicSessionCredentials);
                credentials = new InstanceProfileCredentialsProvider(true).getCredentials();
                log.info("InstanceProfileCredentialsProvider: " + credentials.getAWSAccessKeyId());
            } catch (Exception ex) {
                log.fatal("Failed to load aws credentials :" + e, e);
            }
        }
    }

    public static AWSCredentials getAWSCredentials() {
        return credentials;
    }

}
