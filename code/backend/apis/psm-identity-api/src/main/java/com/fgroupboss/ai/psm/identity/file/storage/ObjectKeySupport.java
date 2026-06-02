package com.fgroupboss.ai.psm.identity.file.storage;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public final class ObjectKeySupport {

    private ObjectKeySupport() {
    }

    public static String buildObjectKey(Long tenantId, String fileName) {
        String ym = new SimpleDateFormat("yyyyMM").format(new Date());
        String safe = sanitizeFileName(fileName);
        return tenantId + "/" + ym + "/" + UUID.randomUUID().toString().replace("-", "") + "_" + safe;
    }

    public static String sanitizeFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "upload.bin";
        }
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
