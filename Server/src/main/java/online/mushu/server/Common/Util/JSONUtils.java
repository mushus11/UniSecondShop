package online.mushu.server.Common.Util;

import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * =======
 * =======
 */
public class JSONUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

//    将一个对象转换成JSON格式字符串
    public static String toJSON(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

//    JSON将字符串转换成一个对象
    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
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
