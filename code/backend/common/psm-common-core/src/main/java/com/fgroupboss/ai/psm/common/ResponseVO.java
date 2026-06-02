package com.fgroupboss.ai.psm.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<T>(0, "success", data);
    }

    public static <T> ResponseVO<T> failure(int code, String message) {
        return new ResponseVO<T>(code, message, null);
    }

    public static <T> ResponseVO<T> success() {
        return new ResponseVO<T>(0, "success", null);
    }
}
