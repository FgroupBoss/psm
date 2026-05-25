package com.fgroupboss.ai.psm.audit.interfaces;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseVO<Void>> handleBusinessException(BusinessException e) {
        HttpStatus status = e.getCode() >= 400 && e.getCode() < 600
                ? HttpStatus.valueOf(e.getCode())
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ResponseVO.failure(e.getCode(), e.getMessage()));
    }
}
