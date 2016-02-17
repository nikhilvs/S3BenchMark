package com.hubbleconnected.monitor.s3monitor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;


public class S3BenchMark
{

	private static ExecutorService s3CheckThreadPool;
	private static final int S3_CHECK_THREAD_POOL=10;
	private static ExecutorService s3TransferManagerThreadPool;
	private static final int S3_TRANSFER_MANAGER_THREAD_POOL=10;
	private static AmazonS3 s3 ;
	private static TransferManager tx;
	
	private static final String BUCKET="";
	
	static{
		
		ThreadFactory threadFactory = new HubbleThreadFactory()
				.setDaemon(false)
				.setPriority(Thread.MAX_PRIORITY)
				.setNamePrefix("S3_CHECK_THREAD_POOL-").build();
		s3CheckThreadPool = Executors.newFixedThreadPool(S3_CHECK_THREAD_POOL, threadFactory);
		
		ThreadFactory threadFactoryForS3 = new HubbleThreadFactory()
				.setDaemon(false)
				.setPriority(Thread.MAX_PRIORITY)
				.setNamePrefix("S3_TRANSFER_MANAGER_THREAD_POOL-").build();
		s3TransferManagerThreadPool = Executors.newFixedThreadPool(S3_TRANSFER_MANAGER_THREAD_POOL, threadFactoryForS3);
		
		AmazonS3 s3 = new AmazonS3Client(AWSCredentialProvider.getAWSCredentials());
		tx = new TransferManager(s3, s3TransferManagerThreadPool, true);
	}
	
	
	public static void main(String[] args)
	{
		
		

		
		Callable<String> c = new Callable<String>()
		{
			
			@Override
			public String call() throws Exception
			{
				// TODO Auto-generated method stub
                             log.info();
				return null;
			}
		};

		
		Future<Integer> future = s3CheckThreadPool.submit(() -> {
		    try {
		        TimeUnit.SECONDS.sleep(2);
		        return 123;
		    }
		    catch (InterruptedException e) {
		        throw new IllegalStateException("task interrupted", e);
		    }
		});
		
		
		
	}
}
