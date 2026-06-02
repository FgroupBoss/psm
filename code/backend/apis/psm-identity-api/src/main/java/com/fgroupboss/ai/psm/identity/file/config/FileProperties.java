package com.fgroupboss.ai.psm.identity.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件模块全局配置：默认后端、本地上传根目录、大小/类型限制与下载模式。
 */
@Data
@ConfigurationProperties(prefix = "psm.file")
public class FileProperties {

    private String defaultBackend = StorageBackend.LOCAL;
    private String storageRoot = "./data/psm-files";
    private long maxSizeBytes = 20L * 1024 * 1024;
    private String allowedContentTypes = "image/jpeg,image/png,image/webp,application/pdf";
    /** AUTO：云存储预签名，LOCAL/MINIO 内网走 PROXY */
    private String downloadMode = "AUTO";
    private int presignTtlSeconds = 300;

    private PlatformMinio minio = new PlatformMinio();

    @Data
    public static class PlatformMinio {
        private boolean enabled = false;
        private String endpoint = "";
        private String accessKey = "";
        private String secretKey = "";
        private String bucket = "";
        private String region = "";
        private boolean secure = true;
    }
}
