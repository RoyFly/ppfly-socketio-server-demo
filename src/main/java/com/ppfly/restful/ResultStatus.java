package com.ppfly.restful;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultStatus {
    SUCCESS(100, "成功"), ERROR(-1100, "失败");

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回结果描述
     */
    private String message;


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
}
