package com.fgroupboss.ai.psm.identity.file.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgroupboss.ai.psm.common.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过 HTTP 网关对接阿里云/腾讯/华为等对象存储，填 httpUrl 即可。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpGatewayStorageAdapter {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public StoredObject store(String backend, StoreContext ctx, StorageProfileConfig config,
                              InputStream inputStream, String fileName) {
        if (!config.isHttpMode()) {
            throw new BusinessException(400, "http gateway url not configured");
        }
        String objectKey = ObjectKeySupport.buildObjectKey(ctx.getTenantId(), fileName);
        try {
            byte[] payload = readAll(inputStream);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(payload);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
            body.add("backend", backend);
            body.add("tenantId", String.valueOf(ctx.getTenantId()));
            body.add("bucket", ctx.getBucket());
            body.add("objectKey", objectKey);
            body.add("fileName", fileName);
            body.add("contentType", ctx.getContentType());
            body.add("file", new ByteArrayResource(payload) {
                @Override
                public String getFilename() {
                    return ObjectKeySupport.sanitizeFileName(fileName);
                }
            });
            HttpHeaders headers = buildHeaders(config);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String url = normalizeUrl(config.getHttpUrl()) + "/upload";
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<MultiValueMap<String, Object>>(body, headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(502, "storage gateway upload failed");
            }
            StoredObject stored = new StoredObject();
            stored.setStorageBackend(backend);
            stored.setBucket(ctx.getBucket());
            stored.setObjectKey(objectKey);
            stored.setStoragePath(objectKey);
            stored.setSizeBytes(payload.length);
            stored.setSha256(toHex(digest.digest()));
            return stored;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("http gateway store failed reason={}", ex.getMessage());
            throw new BusinessException(500, "storage gateway upload failed");
        }
    }

    public PresignedUrl presignGet(String backend, StoredObjectRef ref, int ttlSeconds) {
        StorageProfileConfig config = ref.getProfileConfig();
        if (config == null || !config.isHttpMode()) {
            throw new BusinessException(400, "http gateway url not configured");
        }
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("backend", backend);
        body.put("tenantId", ref.getTenantId());
        body.put("bucket", ref.getBucket());
        body.put("objectKey", ref.getObjectKey());
        body.put("fileName", ref.getFileName());
        body.put("expiresInSeconds", ttlSeconds);
        try {
            HttpHeaders headers = buildHeaders(config);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = normalizeUrl(config.getHttpUrl()) + "/presign";
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<String>(objectMapper.writeValueAsString(body), headers),
                    String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(502, "storage gateway presign failed");
            }
            JsonNode json = objectMapper.readTree(response.getBody() == null ? "{}" : response.getBody());
            PresignedUrl presigned = new PresignedUrl();
            if (json.hasNonNull("url")) {
                presigned.setUrl(json.get("url").asText());
            } else if (json.hasNonNull("presignedUrl")) {
                presigned.setUrl(json.get("presignedUrl").asText());
            } else {
                throw new BusinessException(502, "storage gateway presign response invalid");
            }
            presigned.setExpiresInSeconds(ttlSeconds);
            return presigned;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(500, "storage gateway presign failed");
        }
    }

    public HealthCheckResult testConnection(String backend, StorageProfileConfig config) {
        if (!config.isHttpMode()) {
            return HealthCheckResult.skipped("set mode=HTTP and httpUrl");
        }
        try {
            HttpHeaders headers = buildHeaders(config);
            String url = normalizeUrl(config.getHttpUrl()) + "/health";
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<Void>(null, headers), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return HealthCheckResult.ok("gateway reachable");
            }
            return HealthCheckResult.failed("gateway returned " + response.getStatusCodeValue());
        } catch (Exception ex) {
            return HealthCheckResult.failed(ex.getMessage());
        }
    }

    private HttpHeaders buildHeaders(StorageProfileConfig config) {
        HttpHeaders headers = new HttpHeaders();
        String ak = config.resolvedAccessKey();
        if (StringUtils.hasText(ak)) {
            headers.set("X-Api-Key", ak);
        }
        String sk = config.resolvedSecretKey();
        if (StringUtils.hasText(sk)) {
            headers.set("X-Api-Secret", sk);
        }
        return headers;
    }

    private String normalizeUrl(String httpUrl) {
        String url = httpUrl.trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private byte[] readAll(InputStream inputStream) throws Exception {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int read;
        while ((read = inputStream.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    private String toHex(byte[] digest) {
        StringBuilder builder = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
