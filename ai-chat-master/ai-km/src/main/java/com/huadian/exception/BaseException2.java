package com.huadian.exception;

public class BaseException2 extends RuntimeException {

    private String code;//状态码
    private Object[] arguments;
    private Object extraInfo;

    public BaseException2(String code) {
        super(code);
        this.code = code;
    }

    public BaseException2(String code, Object extraInfo) {
        super(code);
        this.code = code;
        this.extraInfo = extraInfo;
    }

    public BaseException2(String code, Object[] arguments) {
        super(code);
        this.code = code;
        this.arguments = arguments;
    }

    public BaseException2(String code, Object[] arguments, Object extraInfo) {
        super(code);
        this.code = code;
        this.arguments = arguments;
        this.extraInfo = extraInfo;
    }

    public BaseException2(String code, Throwable cause) {
        super(code, cause);
        this.code = code;
    }

    public BaseException2(String code, Throwable cause, Object extraInfo) {
        super(code, cause);
        this.code = code;
        this.extraInfo = extraInfo;
    }

    public BaseException2(String code, Throwable cause, Object[] arguments) {
        super(code, cause);
        this.code = code;
        this.arguments = arguments;
        this.extraInfo = null;
    }

    public BaseException2(String code, Throwable cause, Object[] arguments, Object extraInfo) {
        super(code, cause);
        this.code = code;
        this.arguments = arguments;
        this.extraInfo = extraInfo;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArguements() {
        return arguments;
    }

    public Object getExtraInfo() {
        return extraInfo;
    }
}
