package com.github.chencn.yamlandprops.yaml2props;

import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xqchen
 */
public class Yaml2Props {

    Map<String, Map<String, Object>> config;

    public Yaml2Props(String contents) {
        Yaml yaml = new Yaml();
        this.config = (Map<String, Map<String, Object>>) yaml.loadAs(contents, TreeMap.class);
    }

    public static Yaml2Props fromContent(String content) {
        return new Yaml2Props(content);
    }

    public String convert() {
        return toProperties(this.config);
    }

    private static String toProperties(final Map<String, Map<String, Object>> config) {
        StringBuilder sb = new StringBuilder();
        for (final String key : config.keySet()) {
            toString(sb, key, config.get(key));
        }
        return sb.toString();
    }

    private static void toString(StringBuilder sb, String key, Object o) {
        if (o instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) o;
            for (final String mapKey : map.keySet()) {
                Object mapValue = map.get(mapKey);
                String childKey = key + "." + mapKey;
                if (mapValue instanceof Map) {
                    toString(sb, childKey, mapValue);
                } else if (mapValue instanceof List) {
                    List<Object> listValue = (List<Object>) mapValue;
                    for (int i = 0; i < listValue.size(); i++) {
                        listValueToString(sb, childKey + ".[" + i + "]", listValue.get(i));
                    }
                } else {
                    sb.append(String.format("%s=%s%n", childKey, mapValue));
                }
            }
        } else {
            sb.append(String.format("%s=%s%n", key, o));
        }
    }

    private static void listValueToString(StringBuilder sb, String key, Object o) {
        if (o instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) o;
            if (map.size() == 1) {
                map.forEach((childKey, value) -> sb.append(String.format("%s=%s:%s%n", key, childKey, value)));
                return;
            }
        }
        // TODO: Figure out how we should format this
        toString(sb, key, o);
    }
}
