package com.fgroupboss.ai.psm.gateway;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseVO<Void>> handleBusinessException(BusinessException e) {
        return new ResponseEntity<ResponseVO<Void>>(ResponseVO.failure(e.getCode(), e.getMessage()), status(e.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVO<Void>> handleException(Exception e) {
        log.error("gateway unexpected error", e);
        return new ResponseEntity<ResponseVO<Void>>(ResponseVO.failure(500, "internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus status(int code) {
        HttpStatus status = HttpStatus.resolve(code);
        return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }
}
