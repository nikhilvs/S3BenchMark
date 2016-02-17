package com.hubbleconnected.monitor.s3monitor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class S3BenchMark {

    private static final Logger log = Logger.getLogger(S3BenchMark.class);
    private static ExecutorService s3CheckThreadPool;
    private static final int S3_CHECK_THREAD_POOL = 1000;
    private static ExecutorService s3TransferManagerThreadPool;
    private static final int S3_TRANSFER_MANAGER_THREAD_POOL = 1000;
    private static AmazonS3 s3;
    private static TransferManager tx;

    private static final String BUCKET = "dev-h2o-upload-server";
        private static final String CLIP_PATH="/home/ec2-user/clip.flv";
//    private static final String CLIP_PATH = "/Users/nikhilvs9999/Documents/poc.java";
    ///Users/nikhilvs9999/Documents/poc.java

    static {
//		BasicConfigurator.configure();
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

    public static void main(String[] args) {

//        String pool=System.getProperty("pool");
//        if(pool!=null && !pool.isEmpty())
//        {
//            
//        }
        final Path filePath = Paths.get(CLIP_PATH);
        final File uploadFile = filePath.toFile();
        Callable<Long> c = () -> {
            long t1 = System.currentTimeMillis();
                    Upload upload = tx.upload(BUCKET, UUID.randomUUID().toString(), uploadFile);
//            Upload upload = tx.upload(BUCKET, "hello/mello" + System.currentTimeMillis(), uploadFile);
            upload.waitForCompletion();
            long t2 = System.currentTimeMillis();
            long time = t2 - t1;
            log.info("upload time :" + time + " status :" + upload.isDone());
            return time;
        };

        List<Future<Long>> futures;
        List<Callable<Long>> task = new ArrayList<>(S3_CHECK_THREAD_POOL);
        for (int i = 0; i < S3_CHECK_THREAD_POOL; i++) {
            task.add(c);
        }
        long t1 = System.currentTimeMillis();
        try {
            futures = s3CheckThreadPool.invokeAll(task);

            futures.parallelStream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (InterruptedException ex) {
            log.error(ex);
        }
        long t2 = System.currentTimeMillis();
        long time = t2 - t1;
        log.info("Total time taken :" + time + " so req/sec :" + (S3_CHECK_THREAD_POOL*1000 / time));
        s3CheckThreadPool.shutdown();
//        tx.shutd;

    }
}
