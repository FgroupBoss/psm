package com.fgroupboss.ai.psm.common.feign;

import lombok.Getter;
import lombok.ToString;

/**
 * 远程服务调用超时异常。
 */
@Getter
@ToString(callSuper = true)
public class RemoteServiceTimeoutException extends RemoteServiceException {

    private final String targetService;

    public RemoteServiceTimeoutException(String targetService, String message) {
        super(504, message);
        this.targetService = targetService;
    }
}
