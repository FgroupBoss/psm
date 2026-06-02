package com.fgroupboss.ai.psm.common.feign;

import lombok.Getter;
import lombok.ToString;

/**
 * 远程服务调用异常 — 携带下游服务返回的错误码和消息。
 */
@Getter
@ToString(callSuper = true)
public class RemoteServiceException extends RuntimeException {

    private final int httpStatus;

    public RemoteServiceException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public RemoteServiceException(int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
