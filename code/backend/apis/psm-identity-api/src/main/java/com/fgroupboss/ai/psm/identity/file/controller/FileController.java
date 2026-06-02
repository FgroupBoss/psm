package com.fgroupboss.ai.psm.identity.file.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.common.UserContext;
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
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；租户与用户 ID 从登录上下文解析。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final FileStorageProfileService fileStorageProfileService;

    /**
     * 服务健康检查。
     * <p>HTTP GET {@code /api/files/health}</p>
     *
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/health")
    public ResponseVO<FileHealthVO> health() {
        return ResponseVO.success(fileService.health());
    }

    /**
     * 列出支持的存储后端及配置字段说明（管理端建 profile 用）。
     * <p>HTTP GET {@code /api/files/backends}</p>
     *
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/backends")
    public ResponseVO<List<FileBackendSchemaVO>> backends() {
        return ResponseVO.success(fileStorageProfileService.listBackendSchemas());
    }

    /**
     * 查询租户存储配置档列表。
     * <p>HTTP GET {@code /api/files/profiles}</p>
     *
     * @param loginContext 当前登录租户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/profiles")
    public ResponseVO<List<FileStorageProfileVO>> listProfiles(@LoginContext UserContext loginContext) {
        return ResponseVO.success(fileStorageProfileService.list(loginContext.getTenantId()));
    }

    /**
     * 查询租户默认且启用的存储配置档。
     * <p>HTTP GET {@code /api/files/profiles/default}</p>
     *
     * @param loginContext 当前登录租户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/profiles/default")
    public ResponseVO<FileStorageProfileVO> defaultProfile(@LoginContext UserContext loginContext) {
        return ResponseVO.success(fileStorageProfileService.getDefault(loginContext.getTenantId()));
    }

    /**
     * 新增或更新存储配置档（按 tenantId + profileCode 幂等）。
     * <p>HTTP POST {@code /api/files/profiles}</p>
     *
     * @param loginContext 当前登录租户
     * @param request      请求体
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/profiles")
    public ResponseVO<FileStorageProfileVO> saveProfile(@LoginContext UserContext loginContext,
                                                       @Valid @RequestBody FileStorageProfileRequest request) {
        request.setTenantId(loginContext.getTenantId());
        return ResponseVO.success(fileStorageProfileService.save(request));
    }

    /**
     * 对指定配置档做连通性探针（不写业务文件元数据）。
     * <p>HTTP POST {@code /api/files/profiles/{id}/test}</p>
     *
     * @param id           配置档主键 ID
     * @param loginContext 当前登录租户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping("/profiles/{id}/test")
    public ResponseVO<HealthCheckResult> testProfile(@PathVariable Long id,
                                                     @LoginContext UserContext loginContext) {
        return ResponseVO.success(fileStorageProfileService.test(loginContext.getTenantId(), id));
    }

    /**
     * 上传文件并写入元数据。
     * <p>HTTP POST {@code /api/files/upload}（multipart）</p>
     *
     * @param loginContext       当前登录租户与用户
     * @param file               文件流
     * @param bizType            业务类型（可选）
     * @param bizId              业务主键（可选）
     * @param storageProfileCode 指定配置档编码，空则使用租户默认档
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseVO<FileObjectVO> upload(@LoginContext UserContext loginContext,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(required = false) String bizType,
                                           @RequestParam(required = false) Long bizId,
                                           @RequestParam(required = false) String storageProfileCode) {
        return ResponseVO.success(fileService.upload(
                loginContext.getTenantId(),
                file,
                bizType,
                bizId,
                UserContextResolver.operator(loginContext, "system"),
                storageProfileCode));
    }

    /**
     * 查询文件元数据（不含字节流）。
     * <p>HTTP GET {@code /api/files/{id}}</p>
     *
     * @param id           文件主键 ID
     * @param loginContext 当前登录租户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}")
    public ResponseVO<FileObjectVO> detail(@PathVariable Long id, @LoginContext UserContext loginContext) {
        return ResponseVO.success(fileService.get(loginContext.getTenantId(), id));
    }

    /**
     * 获取下载预签名 URL（对象存储 / HTTP 网关）。
     * <p>HTTP GET {@code /api/files/{id}/presign}</p>
     *
     * @param id           文件主键 ID
     * @param loginContext 当前登录租户
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/{id}/presign")
    public ResponseVO<FilePresignVO> presign(@PathVariable Long id, @LoginContext UserContext loginContext) {
        return ResponseVO.success(fileService.presignDownload(loginContext.getTenantId(), id));
    }

    /**
     * 下载文件；云存储在 AUTO 模式下返回 302 至预签名地址，否则流式输出。
     * <p>HTTP GET {@code /api/files/{id}/download}</p>
     *
     * @param id           文件主键 ID
     * @param loginContext 当前登录租户
     * @return 文件流或重定向响应
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id, @LoginContext UserContext loginContext) {
        Long tenantId = loginContext.getTenantId();
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

    /**
     * 内联预览文件（Content-Disposition: inline）；行为与下载接口的重定向策略一致。
     * <p>HTTP GET {@code /api/files/{id}/preview}</p>
     *
     * @param id           文件主键 ID
     * @param loginContext 当前登录租户
     * @return 文件流或重定向响应
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<?> preview(@PathVariable Long id, @LoginContext UserContext loginContext) {
        Long tenantId = loginContext.getTenantId();
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
