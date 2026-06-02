package com.fgroupboss.ai.psm.identity.file.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.identity.file.model.dto.FileStorageProfileRequest;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileBackendSchemaVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileObjectVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FilePresignVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileStorageProfileVO;
import com.fgroupboss.ai.psm.identity.file.service.FileService;
import com.fgroupboss.ai.psm.identity.file.service.FileStorageProfileService;
import com.fgroupboss.ai.psm.identity.file.storage.HealthCheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * File 模块 HTTP API。
 * <p>基础路径：{@code /api/files}</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final FileStorageProfileService fileStorageProfileService;

    @GetMapping("/health")
    public ResponseVO<FileHealthVO> health() {
        return ResponseVO.success(fileService.health());
    }

    @GetMapping("/backends")
    public ResponseVO<List<FileBackendSchemaVO>> backends() {
        return ResponseVO.success(fileStorageProfileService.listBackendSchemas());
    }

    @GetMapping("/profiles")
    public ResponseVO<List<FileStorageProfileVO>> listProfiles(@RequestParam Long tenantId) {
        return ResponseVO.success(fileStorageProfileService.list(tenantId));
    }

    @GetMapping("/profiles/default")
    public ResponseVO<FileStorageProfileVO> defaultProfile(@RequestParam Long tenantId) {
        return ResponseVO.success(fileStorageProfileService.getDefault(tenantId));
    }

    @PostMapping("/profiles")
    public ResponseVO<FileStorageProfileVO> saveProfile(@Valid @RequestBody FileStorageProfileRequest request) {
        return ResponseVO.success(fileStorageProfileService.save(request));
    }

    @PostMapping("/profiles/{id}/test")
    public ResponseVO<HealthCheckResult> testProfile(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(fileStorageProfileService.test(tenantId, id));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseVO<FileObjectVO> upload(@RequestParam Long tenantId,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(required = false) String bizType,
                                           @RequestParam(required = false) Long bizId,
                                           @RequestParam(required = false) String storageProfileCode,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(fileService.upload(tenantId, file, bizType, bizId,
                UserContextResolver.operator(userId, username, operator), storageProfileCode));
    }

    @GetMapping("/{id}")
    public ResponseVO<FileObjectVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(fileService.get(tenantId, id));
    }

    @GetMapping("/{id}/presign")
    public ResponseVO<FilePresignVO> presign(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(fileService.presignDownload(tenantId, id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id, @RequestParam Long tenantId) {
        if (fileService.shouldRedirectToPresign(tenantId, id)) {
            FilePresignVO presign = fileService.presignDownload(tenantId, id);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(presign.getUrl())).build();
        }
        FileObjectVO meta = fileService.get(tenantId, id);
        Resource resource = fileService.loadAsResource(tenantId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(resource);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> preview(@PathVariable Long id, @RequestParam Long tenantId) {
        if (fileService.shouldRedirectToPresign(tenantId, id)) {
            FilePresignVO presign = fileService.presignDownload(tenantId, id);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(presign.getUrl())).build();
        }
        FileObjectVO meta = fileService.get(tenantId, id);
        Resource resource = fileService.loadAsResource(tenantId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(resource);
    }
}
