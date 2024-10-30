package com.wjp.usercenter.common;

import lombok.Data;

/**
 * 错误码
 * @author wjp
 */

public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    /**
     * 请求参数错误
     */
    PARAMS_ERROR(4000, "请求参数出错",""),
    /**
     * 请求参数为空
     */
    NULL_ERROR(4001, "请求参数为空",""),
    /**
     * 用户未登录
     */
    NOT_LOGIN(40100, "用户未登录", ""),
    /**
     * 无权限
     */
    NO_AUTH(40101, "无权限", ""),

    SYSTEM_ERROR(50000, "系统内部异常", "");

    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String message;
    /**
     * 错误描述(详情)
     */
    private final String description;

    /**
     * 构造方法
     * @param code
     * @param message
     * @param description
     */

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
