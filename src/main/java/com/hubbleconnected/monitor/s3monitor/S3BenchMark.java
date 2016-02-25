package com.hubbleconnected.monitor.s3monitor;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.AWSS3V4Signer;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class S3BenchMark {

    private static final Logger log = Logger.getLogger(S3BenchMark.class);
    private static ExecutorService s3CheckThreadPool;
    private static final int S3_CHECK_THREAD_POOL = 1000;
    private static ExecutorService s3TransferManagerThreadPool;
    private static final int S3_TRANSFER_MANAGER_THREAD_POOL = 1000;
    private static AmazonS3 s3 = new AmazonS3Client(HubbleAWSCredentialProvider.getAWSCredentials());
    private static TransferManager tx;

    private static final String BUCKET = "dev-h2o-upload-server";
    private static final String CLIP_PATH = "/home/ubuntu/clip.flv";
//    private static final String CLIP_PATH = "/Users/nikhilvs9999/Documents/poc.java";
    ///Users/nikhilvs9999/Documents/poc.java

    static void test() {
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

        s3 = new AmazonS3Client(HubbleAWSCredentialProvider.getAWSCredentials());
        tx = new TransferManager(s3, s3TransferManagerThreadPool, true);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {

        InstanceProfileCredentialsProvider in = new InstanceProfileCredentialsProvider(true);
//        AWSCredentialsProvider
        AmazonS3 s3Obj = new AmazonS3Client(in);
        for (;;) {
            log.info("static getAWSAccessKeyId :" + HubbleAWSCredentialProvider.getAWSCredentials().getAWSAccessKeyId());
            log.info("static getAWSSecretKey :" + HubbleAWSCredentialProvider.getAWSCredentials().getAWSSecretKey());
            log.info("o getAWSAccessKeyId :" + in.getCredentials().getAWSAccessKeyId());
            log.info("o getAWSSecretKey :" + in.getCredentials().getAWSSecretKey());
            if (!in.getCredentials().getAWSSecretKey().contentEquals(HubbleAWSCredentialProvider.getAWSCredentials().getAWSSecretKey())) {
                log.warn("credentials not equal");

            }

            List<Bucket> buckets = s3.listBuckets();

            for (Bucket b : buckets) {
                log.info("static s3 func :" + b.getName());
                break;
            }

            buckets = s3Obj.listBuckets();
            for (Bucket b : buckets) {
                log.info("obj s3 func :" + b.getName());
                break;
            }

            Thread.sleep(1000 * 60 * 1);
        }
    }

    void benchS3() throws FileNotFoundException {

        final Path filePath = Paths.get(CLIP_PATH);
        final File uploadFile = filePath.toFile();
        InputStream in = new FileInputStream(uploadFile);

//            final MultipartFile multipartFile = new MockMultipartFile(uploadFile.getName(),
//        uploadFile.getName(), "video/x-flv", IOUtils.toByteArray(in));
        Callable<Long> process = () -> {
            long t1 = System.currentTimeMillis();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(uploadFile.length());

//            Upload upload = tx.upload(BUCKET, UUID.randomUUID().toString(), multipartFile.getInputStream(), objectMetadata);
            PutObjectResult result = s3.putObject(BUCKET, UUID.randomUUID().toString(), uploadFile);
//            PutObjectResult result = s3.putObject(BUCKET, UUID.randomUUID().toString(), multipartFile.getInputStream(), objectMetadata);
//            Upload upload = tx.upload(BUCKET, "hello/mello" + System.currentTimeMillis(), uploadFile);
//            upload.waitForCompletion();
            long t2 = System.currentTimeMillis();
            long time = t2 - t1;
//            log.info("upload time :" + time + " status :" + upload.isDone());
            log.info("upload time :" + time + " status :" + result.getETag());
            return time;
        };

        List<Future<Long>> futures;
        List<Callable<Long>> tasks = new ArrayList<>(S3_CHECK_THREAD_POOL);
        for (int i = 0; i < S3_CHECK_THREAD_POOL; i++) {
            tasks.add(process);
        }
        long t1 = System.currentTimeMillis();
        try {
            futures = s3CheckThreadPool.invokeAll(tasks);

            futures.parallelStream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (InterruptedException ex) {
            log.error(ex);
        }
        long t2 = System.currentTimeMillis();
        long time = t2 - t1;
        log.info("Total time taken :" + time + " so req/sec :" + (S3_CHECK_THREAD_POOL * 1000 / time));
        s3CheckThreadPool.shutdown();
//        tx.shutd;

    }
}
