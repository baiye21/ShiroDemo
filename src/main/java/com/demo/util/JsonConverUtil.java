package com.demo.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: Json工具类
*/
public class JsonConverUtil {
    public class JsonConvertUtil {
        /**
         * JSON 转 Object
         */
        public <T> T jsonToObject(String pojo, Class<T> clazz) {
            return JSONObject.parseObject(pojo, clazz);
        }

        /**
         * Object 转 JSON
         */
        public <T> String objectToJson(T t) {
            return JSONObject.toJSONString(t);
        }

        /**
         * json 转 List<T>
         */
        public <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
            List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
            return ts;
        }

        /**
         * List<T> 转 json 保存到数据库
         */
        public <T> String listToJson(List<T> ts) {
            String jsons = JSON.toJSONString(ts);
            return jsons;
        }

    }

}
