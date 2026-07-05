package online.mushu.server.Common.Util;

import jakarta.annotation.Resource;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * =======
 * =======
 */
public class JSONUtils {

    @Resource
    ObjectMapper objectMapper;

//    将一个对象转换成JSON格式字符串
    public static String toJSON(Object obj) {
        return new ObjectMapper().writeValueAsString(obj);
    }

//    JSON将字符串转换成一个对象
    public static <T> T fromJSON(String json, Class<T> clazz) {
        return new ObjectMapper().readValue(json, clazz);
    }

//    获取请求体中的JSON数据
    public static String getJson(BufferedReader br) throws IOException {

        if (br == null) throw new NullPointerException("br is null");
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine() )!= null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
