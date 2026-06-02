package com.fgroupboss.ai.psm.identity.file.config;

/**
 * 文件存储后端编码，与租户配置档 storage_backend 一致。
 */
public final class StorageBackend {

    public static final String LOCAL = "LOCAL";
    public static final String MINIO = "MINIO";
    public static final String S3_COMPAT = "S3_COMPAT";
    public static final String ALIYUN_OSS = "ALIYUN_OSS";
    public static final String TENCENT_COS = "TENCENT_COS";
    public static final String HUAWEI_OBS = "HUAWEI_OBS";
    public static final String QINIU_KODO = "QINIU_KODO";
    public static final String BAIDU_BOS = "BAIDU_BOS";

    private StorageBackend() {
    }

    public static boolean usesS3Api(String backend) {
        return MINIO.equals(backend)
                || S3_COMPAT.equals(backend)
                || ALIYUN_OSS.equals(backend)
                || TENCENT_COS.equals(backend)
                || HUAWEI_OBS.equals(backend)
                || QINIU_KODO.equals(backend)
                || BAIDU_BOS.equals(backend);
    }
}
