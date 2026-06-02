package com.fgroupboss.ai.psm.identity.file.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.identity.file.config.FileProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilePolicySupport {

    private final FileProperties fileProperties;

    public void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "file is required");
        }
        if (file.getSize() > fileProperties.getMaxSizeBytes()) {
            throw new BusinessException(400, "file exceeds max size " + fileProperties.getMaxSizeBytes());
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)) {
            return;
        }
        Set<String> allowed = parseAllowed();
        if (!allowed.isEmpty() && !allowed.contains(contentType.trim().toLowerCase())) {
            throw new BusinessException(400, "content type not allowed: " + contentType);
        }
    }

    private Set<String> parseAllowed() {
        Set<String> set = new HashSet<String>();
        if (!StringUtils.hasText(fileProperties.getAllowedContentTypes())) {
            return set;
        }
        for (String part : fileProperties.getAllowedContentTypes().split(",")) {
            if (StringUtils.hasText(part)) {
                set.add(part.trim().toLowerCase());
            }
        }
        return set;
    }
}
