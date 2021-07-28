package com.yuxuan66.support.basic.http;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author Sir丶雨轩
 * @date 2020/9/21
 */
public class PageEntity extends JSONObject {

    private HttpCode code;

    private Object data;
    private String msg;

    public static PageEntity success(IPage<?> page) {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setCode(HttpCode.SUCCESS.value());

        JSONObject data = new JSONObject();
        data.put("total", page.getTotal());
        data.put("list", page.getRecords());
        pageEntity.setData(data);
        return pageEntity;
    }

    public static PageEntity success(List<?> page) {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setCode(HttpCode.SUCCESS.value());
        JSONObject data = new JSONObject();
        data.put("total", page.size());
        data.put("list", page);
        pageEntity.setData(data);
        return pageEntity;
    }


    public int getCode() {
        return this.getIntValue("code");
    }


    public PageEntity setCode(int code) {
        this.put("code", code);
        return this;
    }


    public PageEntity setMsg(String msg) {
        this.put("msg", msg);
        return this;
    }

    public String getMsg() {
        return this.getString("msg");
    }

    public PageEntity setData(Object data) {
        this.put("data", data);
        return this;
    }

    public <T> T getData(Class<T> cls) {
        return this.getObject("data", cls);
    }

    public Object getData() {
        return this.get("data");
    }
}
