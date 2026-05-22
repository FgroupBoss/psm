package com.fgroupboss.ai.psm.common;

import java.io.Serializable;

public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public ResponseVO() {
    }

    private ResponseVO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<T>(0, "success", data);
    }

    public static <T> ResponseVO<T> failure(int code, String message) {
        return new ResponseVO<T>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

