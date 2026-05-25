package com.fgroupboss.ai.psm.gateway.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler {

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
