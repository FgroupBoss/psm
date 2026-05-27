package com.fgroupboss.ai.psm.mobile.client;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.mobile.model.vo.FileUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 调用 psm-file-service 上传附件。
 */
@Slf4j
@Component
public class FileClient {

    private static final ParameterizedTypeReference<ResponseVO<Map<String, Object>>> FILE_TYPE =
            new ParameterizedTypeReference<ResponseVO<Map<String, Object>>>() {
            };

    private final RestTemplate restTemplate;
    private final String fileServiceUrl;

    public FileClient(RestTemplate restTemplate,
                      @Value("${psm.file-service-url:http://localhost:18092}") String fileServiceUrl) {
        this.restTemplate = restTemplate;
        this.fileServiceUrl = trim(fileServiceUrl);
    }

    public FileUploadVO upload(Long tenantId, MultipartFile file, String bizType, Long bizId, HttpHeaders contextHeaders) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "file is required");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            if (contextHeaders != null) {
                contextHeaders.forEach((key, values) -> headers.put(key, values));
            }
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
            body.add("tenantId", tenantId);
            if (StringUtils.hasText(bizType)) {
                body.add("bizType", bizType);
            }
            if (bizId != null) {
                body.add("bizId", bizId);
            }
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "upload.bin";
                }
            });
            ResponseEntity<ResponseVO<Map<String, Object>>> response = restTemplate.exchange(
                    fileServiceUrl + "/api/files/upload",
                    HttpMethod.POST,
                    new HttpEntity<MultiValueMap<String, Object>>(body, headers),
                    FILE_TYPE);
            ResponseVO<Map<String, Object>> payload = response.getBody();
            if (payload == null || payload.getCode() != 0 || payload.getData() == null) {
                throw new BusinessException(502, payload == null ? "file service empty response" : payload.getMessage());
            }
            return toVo(payload.getData());
        } catch (HttpStatusCodeException ex) {
            log.warn("file upload upstream failed status={}", ex.getStatusCode());
            throw new BusinessException(502, "file service unavailable");
        } catch (RestClientException ex) {
            log.error("file upload failed tenantId={}", tenantId, ex);
            throw new BusinessException(502, "file service unavailable");
        } catch (Exception ex) {
            log.error("file upload failed tenantId={}", tenantId, ex);
            throw new BusinessException(500, "file upload failed");
        }
    }

    private FileUploadVO toVo(Map<String, Object> data) {
        FileUploadVO vo = new FileUploadVO();
        Object id = data.get("id");
        vo.setFileId(id == null ? null : String.valueOf(id));
        vo.setFileName(stringValue(data.get("fileName")));
        vo.setContentType(stringValue(data.get("contentType")));
        Object size = data.get("sizeBytes");
        vo.setSizeBytes(size instanceof Number ? ((Number) size).longValue() : 0L);
        vo.setSha256(stringValue(data.get("sha256")));
        vo.setUrl(stringValue(data.get("downloadUrl")));
        return vo;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
