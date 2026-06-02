package com.fgroupboss.ai.psm.identity.file.storage;

import lombok.Data;

@Data
public class PresignedUrl {

    private String url;
    private int expiresInSeconds;
}
