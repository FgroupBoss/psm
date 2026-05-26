package com.fgroupboss.ai.psm.configrule.controller;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 系统配置服务异常处理。
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
        String message = e.getBindingResult().getFieldErrors().isEmpty()
                ? "invalid request"
                : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseVO.failure(400, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("config rule service unexpected error", e);
        return ResponseVO.failure(500, "internal server error");
    }
}
