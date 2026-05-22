package com.fgroupboss.ai.psm.masterdata.interfaces;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.common.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        return ResponseVO.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseVO<Void> handleDataAccessException(DataAccessException e) {
        log.error("master data database operation failed", e);
        return ResponseVO.failure(500, "database operation failed");
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e) {
        log.error("master data unexpected error", e);
        return ResponseVO.failure(500, "internal server error");
    }
}
