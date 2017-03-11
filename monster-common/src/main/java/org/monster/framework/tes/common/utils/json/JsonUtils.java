package org.monster.framework.tes.common.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.monster.framework.tes.common.utils.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Project :       Monster-frameWork
 * Author:         XIE-HONGFEI
 * Company:        hongfei tld.
 * Created Date:   2017/2/22 0022
 * Copyright @ 2017 Company hongfei tld. – Confidential and Proprietary
 *
 * History:
 * ------------------------------------------------------------------------------
 *       Date       |      Author      |   Change Description
 * 2017/2/22 0022   |      谢洪飞       |   初版做成
 */

public class JsonUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValue(String content) {
        try {
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Maps.newHashMap();
    }

    /**
     * 解析非标准json格式
     * NOTE:Key未被双引号("")包裹
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValueByAllowUnquotedFieldNames(String content) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, Boolean.TRUE);
        try {
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Maps.newHashMap();
    }

    public static List readListValue(String content) {
        try {
            return objectMapper.readValue(content, List.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Lists.newArrayList();
    }

    public static <T> T parseObject(String jsonStr, Class<T> clazz) throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//兼容pojo增加字段的版本升级
        return objectMapper.readValue(jsonStr, clazz);
    }

    public static void main(String[] args) {
        Map<String, Object> data = Maps.newHashMap();
        Map<String, Object> childMap = Maps.newHashMap();
        childMap.put("insuranceId", "100000052010000009");
        childMap.put("policyUrl", "www.demo.policyUrl/query-demo/12345");
        data.put("respData", childMap);
        data.put("respCode", 10);

        System.out.println(JsonUtils.writeValueAsString(data));
        Map<String, Object> childMap2 = (Map<String, Object>) data.get("respData");
        System.out.println(childMap2.get("insuranceId"));
    }
}