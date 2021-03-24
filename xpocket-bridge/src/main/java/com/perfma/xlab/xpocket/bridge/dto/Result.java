package com.perfma.xlab.xpocket.bridge.dto;

import java.io.Serializable;

/**
 * @author xinxian
 * @create 2021-03-24 15:28
 **/
public class Result<T> implements Serializable {

    /**
     * 是否成功
     */
    private boolean success = true;

    /**
     * 数据对象
     */
    private T object;

    /**
     * 提示信息
     */
    private String message;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Result buildFail(String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static Result buildFail(String message, Object o) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        result.setObject(o);
        return result;
    }

    public static Result build(boolean success) {
        Result result = new Result();
        result.setSuccess(success);
        return result;
    }

    public static Result buildSuccess(Object object) {
        Result result = new Result();
        result.setSuccess(true);
        result.setObject(object);
        return result;
    }

    public static Result buildSuccess(Object object, String message) {
        Result result = new Result();
        result.setSuccess(true);
        result.setObject(object);
        result.setMessage(message);
        return result;
    }

    public static Result buildSuccess() {
        Result result = new Result();
        result.setSuccess(true);
        return result;
    }
}
