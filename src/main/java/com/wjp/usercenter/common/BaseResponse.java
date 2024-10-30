package com.wjp.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回类
 * @param <T>
 * @author wjp
 */
// 支持序列化
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int code;
    // 泛型 因为不知道data是什么类型
    private T data;
    private String message;
    private String description;

    public BaseResponse() {
    }


    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = "";
    }

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }


    /**
     * 根据错误码生成返回对象
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.data = null;
        this.message = errorCode.getMessage();
        this.description = errorCode.getDescription();

    }
}
