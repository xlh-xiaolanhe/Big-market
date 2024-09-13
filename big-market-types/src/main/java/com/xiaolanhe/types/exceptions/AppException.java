package com.xiaolanhe.types.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *@author: xiaolanhe
 *@createDate: 2024/9/13 19:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException{
    /** 异常码 */
    private String code;

    /** 异常信息 */
    private String info;

    public AppException(String code) {
        this.code = code;
    }

    public AppException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public AppException(String code, String message) {
        this.code = code;
        this.info = message;
    }

    public AppException(String code, String message, Throwable cause) {
        this.code = code;
        this.info = message;
        super.initCause(cause);
    }

    @Override
    public String toString() {
        return "cn.bugstack.x.api.types.exception.XApiException{" +
                "code='" + code + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
