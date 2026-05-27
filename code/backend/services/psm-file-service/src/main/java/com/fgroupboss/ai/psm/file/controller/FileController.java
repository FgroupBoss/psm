package com.fgroupboss.ai.psm.file.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.file.model.vo.FileObjectVO;
import com.fgroupboss.ai.psm.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件中心接口：上传、元数据查询、下载。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @GetMapping("/health")
    public ResponseVO<FileHealthVO> health() {
        return ResponseVO.success(fileService.health());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseVO<FileObjectVO> upload(@RequestParam Long tenantId,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(required = false) String bizType,
                                           @RequestParam(required = false) Long bizId,
                                           @RequestHeader(value = UserContextHeaders.USER_ID, required = false) String userId,
                                           @RequestHeader(value = UserContextHeaders.USERNAME, required = false) String username,
                                           @RequestHeader(value = "X-Operator", defaultValue = "system") String operator) {
        return ResponseVO.success(fileService.upload(tenantId, file, bizType, bizId,
                UserContextResolver.operator(userId, username, operator)));
    }

    @GetMapping("/{id}")
    public ResponseVO<FileObjectVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(fileService.get(tenantId, id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, @RequestParam Long tenantId) {
        FileObjectVO meta = fileService.get(tenantId, id);
        Resource resource = fileService.loadAsResource(tenantId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(resource);
    }
}
