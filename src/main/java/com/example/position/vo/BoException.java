package com.example.position.vo;

public class BoException extends RuntimeException{


    private Integer code;
    private String message;

    public BoException(){}

    public BoException(String message){
        this.message = message;
    }
    public BoException(Integer code){
        this.code = code;
    }

    public BoException(Integer code,String message){
        this.message=message;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
