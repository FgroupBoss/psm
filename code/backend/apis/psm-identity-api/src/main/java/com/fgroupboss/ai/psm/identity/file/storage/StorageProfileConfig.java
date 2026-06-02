package com.fgroupboss.ai.psm.identity.file.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储配置档 JSON，与 application.yml / 管理端表单键名一致。
 */
@Data
public class StorageProfileConfig {

    /** SDK：MinIO/S3 兼容客户端；HTTP：企业统一对象网关 */
    private String mode = "SDK";
    private String storageRoot;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String accessKeyId;
    private String accessKeySecret;
    private String secretId;
    private String secretKeyId;
    private String bucket;
    private String region;
    private Boolean secure;
    private String httpUrl;
    private String signName;

    public static StorageProfileConfig fromJson(String json, ObjectMapper mapper) {
        if (!StringUtils.hasText(json)) {
            return new StorageProfileConfig();
        }
        try {
            return mapper.readValue(json, StorageProfileConfig.class);
        } catch (Exception ex) {
            throw new BusinessException(400, "invalid storage profile config json");
        }
    }

    public String toJson(ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception ex) {
            throw new BusinessException(500, "storage config serialize failed");
        }
    }

    public String resolvedAccessKey() {
        if (StringUtils.hasText(accessKey)) {
            return accessKey;
        }
        if (StringUtils.hasText(accessKeyId)) {
            return accessKeyId;
        }
        if (StringUtils.hasText(secretId)) {
            return secretId;
        }
        return null;
    }

    public String resolvedSecretKey() {
        if (StringUtils.hasText(secretKey)) {
            return secretKey;
        }
        if (StringUtils.hasText(accessKeySecret)) {
            return accessKeySecret;
        }
        return null;
    }

    public boolean isHttpMode() {
        return "HTTP".equalsIgnoreCase(mode) && StringUtils.hasText(httpUrl);
    }

    public boolean isSdkReady() {
        return StringUtils.hasText(endpoint)
                && StringUtils.hasText(resolvedAccessKey())
                && StringUtils.hasText(resolvedSecretKey())
                && StringUtils.hasText(bucket);
    }

    public Map<String, Object> toGatewayMap(String backend, StoreContext ctx, String objectKey) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("backend", backend);
        body.put("tenantId", ctx.getTenantId());
        body.put("bucket", ctx.getBucket());
        body.put("objectKey", objectKey);
        body.put("bizType", ctx.getBizType());
        body.put("contentType", ctx.getContentType());
        return body;
    }
}
