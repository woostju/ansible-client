
package com.github.woostju.ansible.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class JsonUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        //去掉默认的时间戳格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //设置为中国上海时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        //空值不序列化
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        //反序列化时，属性不存在的兼容处理
        objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //序列化时，日期的统一格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //单引号处理
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 
     * @param entity
     * @param clazz
     * @return
     *  convert object from entity to class<T>
     */
    public static <T> T toObject(Object entity, Class<T> clazz) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(entity), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * @param entity
     * @param clazz
     * @return
     *  convert object from ObjectNode
     */
    public static ObjectNode toObjectNode(Object entity) {
           return toObject(entity, ObjectNode.class);
    }
    
    
    
    /**
     * 
     * @param json
     * @param clazz
     * @return
     * 
     * convert string to object
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * @param entity
     * @return
     * 
     * create json String from object
     */
    public static <T> String toJsonString(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param json
     * @param clazz
     * @return
     * 
     * convert string to array
     */
    public static <T> List<T> toArray(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> List<T> toArray(Object object, Class<T> clazz) {
        try {
            return objectMapper.readValue(toJsonString(object), objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <K,V> Map<K,V> toHashMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <K,V> Map<K,V> toHashMap(Object object, Class<K> keyClass, Class<V> valueClass) {
        try {
            return objectMapper.readValue(toJsonString(object), objectMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    

    public static <T> Map<?, ?> bean2Map(Object bean) {
        try {
            return (Map<?, ?>) objectMapper.convertValue(bean, Map.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static <T> T map2Bean(Map<?, ?> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }



	public static boolean isJSONValid(String content) {
        try {
            objectMapper.readTree(content);
            return true;
        } catch (IOException e) {
            return false;
        }
	}

}