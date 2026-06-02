package com.fgroupboss.ai.psm.identity.file.storage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.FileProperties;
import com.fgroupboss.ai.psm.identity.file.config.StorageBackend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Date;

/**
 * S3 协议兼容存储（MinIO、阿里云 OSS S3 兼容端点、腾讯 COS 等）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioStorageAdapter implements StorageAdapter {

    private final FileProperties fileProperties;

    @Override
    public String backend() {
        return StorageBackend.MINIO;
    }

    @Override
    public StoredObject store(StoreContext ctx, StorageProfileConfig config, InputStream inputStream, String fileName) {
        StorageProfileConfig effective = mergePlatform(config);
        if (!effective.isSdkReady()) {
            throw new BusinessException(400, "object storage sdk config incomplete");
        }
        String objectKey = ObjectKeySupport.buildObjectKey(ctx.getTenantId(), fileName);
        try {
            AmazonS3 client = buildClient(effective);
            ensureBucket(client, effective.getBucket());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            DigestInputStream digestStream = new DigestInputStream(inputStream, digest);
            ObjectMetadata metadata = new ObjectMetadata();
            if (ctx.getContentLength() > 0) {
                metadata.setContentLength(ctx.getContentLength());
            }
            String contentType = StringUtils.hasText(ctx.getContentType()) ? ctx.getContentType() : "application/octet-stream";
            metadata.setContentType(contentType);
            client.putObject(new PutObjectRequest(effective.getBucket(), objectKey, digestStream, metadata));
            StoredObject stored = new StoredObject();
            stored.setStorageBackend(ctx.getStorageBackend());
            stored.setBucket(effective.getBucket());
            stored.setObjectKey(objectKey);
            stored.setStoragePath(objectKey);
            stored.setRegion(effective.getRegion());
            stored.setSha256(toHex(digest.digest()));
            stored.setSizeBytes(ctx.getContentLength() > 0 ? ctx.getContentLength() : 0);
            return stored;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("s3 store failed objectKey={} reason={}", objectKey, ex.getMessage());
            throw new BusinessException(500, "object storage upload failed");
        }
    }

    @Override
    public InputStream open(StoredObjectRef ref) {
        StorageProfileConfig config = ref.getProfileConfig();
        if (config == null || !config.isSdkReady()) {
            throw new BusinessException(400, "object storage config missing");
        }
        try {
            AmazonS3 client = buildClient(config);
            S3Object object = client.getObject(ref.getBucket(), ref.getObjectKey());
            return object.getObjectContent();
        } catch (Exception ex) {
            throw new BusinessException(404, "object not found");
        }
    }

    @Override
    public PresignedUrl presignGet(StoredObjectRef ref, int ttlSeconds) {
        StorageProfileConfig config = ref.getProfileConfig();
        if (config == null || !config.isSdkReady()) {
            throw new BusinessException(400, "object storage config missing");
        }
        try {
            AmazonS3 client = buildClient(config);
            Date expiry = new Date(System.currentTimeMillis() + ttlSeconds * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    ref.getBucket(), ref.getObjectKey())
                    .withExpiration(expiry);
            URL url = client.generatePresignedUrl(request);
            PresignedUrl presigned = new PresignedUrl();
            presigned.setUrl(url.toString());
            presigned.setExpiresInSeconds(ttlSeconds);
            return presigned;
        } catch (Exception ex) {
            throw new BusinessException(500, "presign failed");
        }
    }

    @Override
    public HealthCheckResult testConnection(String backend, StorageProfileConfig config) {
        StorageProfileConfig effective = mergePlatform(config);
        if (!effective.isSdkReady()) {
            return HealthCheckResult.skipped("configure endpoint, accessKey, secretKey and bucket");
        }
        try {
            AmazonS3 client = buildClient(effective);
            if (!client.doesBucketExistV2(effective.getBucket())) {
                client.createBucket(effective.getBucket());
            }
            String probeKey = ".healthcheck/" + System.currentTimeMillis() + ".txt";
            byte[] bytes = "ok".getBytes("UTF-8");
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            client.putObject(effective.getBucket(), probeKey, new java.io.ByteArrayInputStream(bytes), metadata);
            client.deleteObject(effective.getBucket(), probeKey);
            return HealthCheckResult.ok("bucket ready");
        } catch (Exception ex) {
            return HealthCheckResult.failed(ex.getMessage());
        }
    }

    private AmazonS3 buildClient(StorageProfileConfig config) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                config.resolvedAccessKey(), config.resolvedSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        config.getEndpoint(),
                        StringUtils.hasText(config.getRegion()) ? config.getRegion() : "us-east-1"))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(new ClientConfiguration().withSignerOverride("AWSS3V4SignerType"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    private void ensureBucket(AmazonS3 client, String bucket) {
        if (!client.doesBucketExistV2(bucket)) {
            client.createBucket(bucket);
        }
    }

    private StorageProfileConfig mergePlatform(StorageProfileConfig config) {
        if (config != null && config.isSdkReady()) {
            return config;
        }
        FileProperties.PlatformMinio platform = fileProperties.getMinio();
        if (platform.isEnabled() && StringUtils.hasText(platform.getEndpoint())) {
            StorageProfileConfig merged = new StorageProfileConfig();
            merged.setEndpoint(platform.getEndpoint());
            merged.setAccessKey(platform.getAccessKey());
            merged.setSecretKey(platform.getSecretKey());
            merged.setBucket(platform.getBucket());
            merged.setRegion(platform.getRegion());
            return merged;
        }
        return config == null ? new StorageProfileConfig() : config;
    }

    private String toHex(byte[] digest) {
        StringBuilder builder = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
