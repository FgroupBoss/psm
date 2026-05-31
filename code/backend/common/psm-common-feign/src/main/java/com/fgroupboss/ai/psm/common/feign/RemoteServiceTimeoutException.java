package com.fgroupboss.ai.psm.common.feign;

/**
 * 远程服务调用超时异常。
 */
public class RemoteServiceTimeoutException extends RemoteServiceException {

    private final String targetService;

    public RemoteServiceTimeoutException(String targetService, String message) {
        super(504, message);
        this.targetService = targetService;
    }

    public String getTargetService() { return targetService; }

    @Override
    public String toString() {
        return "RemoteServiceTimeoutException{targetService=" + targetService + ", message=" + getMessage() + '}';
    }
}
