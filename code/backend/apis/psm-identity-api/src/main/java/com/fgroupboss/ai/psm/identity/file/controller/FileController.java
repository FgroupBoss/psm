package com.fgroupboss.ai.psm.identity.file.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContextHeaders;
import com.fgroupboss.ai.psm.common.UserContextResolver;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileHealthVO;
import com.fgroupboss.ai.psm.identity.file.model.vo.FileObjectVO;
import com.fgroupboss.ai.psm.identity.file.service.FileService;
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
 * File 模块 HTTP API。
 * <p>基础路径：{@code /api/files}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/files/health}</p>
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<FileHealthVO> health() {
        return ResponseVO.success(fileService.health());
    }

    /**
     * 新增upload或触发upload相关动作。
     * <p>HTTP POST {@code /api/files/upload}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param tenantId 租户 ID，多租户隔离必填
     * @param file file 参数
     * @param bizType 业务类型
     * @param bizId 业务实体 ID
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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

    /**
     * 查询作业票详情。
     * <p>HTTP GET {@code /api/files/{id}}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<FileObjectVO> detail(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseVO.success(fileService.get(tenantId, id));
    }

    /**
     * 查询download。
     * <p>HTTP GET {@code /api/files/{id}/download}</p>
     * <p>所有查询与变更均按租户隔离。</p>
     * @param id 资源主键 ID
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 操作结果，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
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
