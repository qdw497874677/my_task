package com.qdw.task.domain.common;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class StringUtils {

    public static String getString(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    public static <T> List<T> parseArray(Object o, Class<T> s) {
        if (o == null) {
            return null;
        }
        return JSONObject.parseArray(JSONObject.toJSONString(o), s);
    }

}
