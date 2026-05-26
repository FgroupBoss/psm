package com.fgroupboss.ai.psm.majorhazard.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 重大危险源服务接口异常统一转换。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        return ResponseVO.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError error = e.getBindingResult().getFieldError();
        String message = error == null ? "request is invalid" : error.getDefaultMessage();
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseVO<Void> handleDataAccessException(DataAccessException e) {
        log.error("major hazard database operation failed", e);
        return ResponseVO.failure(500, "database operation failed");
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("major hazard unexpected error", e);
        return ResponseVO.failure(500, "internal server error");
    }
}
