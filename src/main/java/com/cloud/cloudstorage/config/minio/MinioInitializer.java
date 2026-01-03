package com.cloud.cloudstorage.config.minio;

import com.cloud.cloudstorage.exception.MinioBucketInitializationException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class MinioInitializer {
    private final MinioClient minioClient;
    private final String minioBucketName ;

    public MinioInitializer(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioBucketName = minioProperties.getBucket();
    }

    @PostConstruct
    public void createRootBucketIfNotExists() {
        try {
            if (!isBucketExists()) {
                createNewBucket();
            }
        } catch (Exception ex) {
            throw new MinioBucketInitializationException();
        }
    }

    private boolean isBucketExists() throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioBucketName)
                .build());
    }

    private void createNewBucket() throws Exception{
        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(minioBucketName)
                .build());
    }
}
