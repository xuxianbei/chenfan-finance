package com.chenfan.finance.enums;

/**
 * @Author Wen.Xiao
 * @Description  常用数字枚举，防止魔法值
 * @Date 2020/12/4  14:53
 * @Version 1.0
 */
public enum HttpStateEnum {

    /*
    *
     * @Author eleven.xiao
     * @Description //OK
     * @Date
     * @Param
     * @return
     **/
    OK(200,"OK"),
    /*
     *
     * @Author eleven.xiao
     * @Description //BAD_REQUEST
     * @Date
     * @Param
     * @return
     **/
    BAD_REQUEST(400,"Bad Request"),
    /*
     *
     * @Author eleven.xiao
     * @Description //SERVER_ERROR
     * @Date
     * @Param
     * @return
     **/
    SERVER_ERROR(500,"Internal Server Error"),



    ;


    private int code;
    private String msg;

    HttpStateEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
