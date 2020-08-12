package com.yuxuan66.support.basic.http;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

public class RespEntity extends JSONObject {
    /**
     * 请求失败返回
     */
    public static final RespEntity FAIL = new RespEntity().setCode(HttpCode.FAIL).setMsg("服务器异常,请稍后重试...");
    /**
     * 请求成功返回
     */
    public static final RespEntity SUCCESS = new RespEntity().setCode(HttpCode.SUCCESS);

    private HttpCode code;
    private Object data;
    private String msg;
    public RespEntity() {
    }

    public RespEntity setCode(HttpCode code) {
        this.put("code", code.value());
        return this;
    }

    public int getCode() {
        return this.getIntValue("code");
    }

    public RespEntity setData(Object data) {
        this.put("data", data);
        return this;
    }

    public <T> T getData(Class<T> cls) {
        return this.getObject("data", cls);
    }

    public Object getData() {
        return this.get("data");
    }

    public RespEntity setMsg(String msg) {
        this.put("msg", msg);
        return this;
    }
    public String getMsg() {
        return this.getString("msg");
    }

    public static RespEntity resMsg(HttpCode code, String msg) {
        return new RespEntity().setCode(code).setMsg(msg);
    }

    public static RespEntity resMsg(HttpCode code, String msg, Object data) {
        return new RespEntity().setCode(code).setMsg(msg).setData(data);
    }

    public static RespEntity resMsg(HttpCode code, Object data) {
        return resMsg(code, "success", data);
    }

    public static RespEntity success(Object data) {
        return resMsg(HttpCode.SUCCESS, data);
    }

    public static RespEntity success() {
        return resMsg(HttpCode.SUCCESS, null,null);
    }


    public static RespEntity fail(String msg) {
        return resMsg(HttpCode.FAIL, msg);
    }

    public static RespEntity fail(String msg, Object... str) {
        return resMsg(HttpCode.FAIL, StrUtil.format(msg, str));
    }
}
