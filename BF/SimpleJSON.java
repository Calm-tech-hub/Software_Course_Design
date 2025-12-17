import java.util.*;

/**
 * 简易JSON序列化工具（无需外部依赖）
 */
public class SimpleJSON {
    
    /**
     * 将对象转换为JSON字符串
     */
    public static String toJSON(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escape((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map) return mapToJSON((Map<?, ?>) obj);
        if (obj instanceof List) return listToJSON((List<?>) obj);
        return "\"" + obj.toString() + "\"";
    }
    
    /**
     * Map转JSON
     */
    private static String mapToJSON(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            sb.append(toJSON(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * List转JSON
     */
    private static String listToJSON(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(",");
            sb.append(toJSON(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * 转义特殊字符
     */
    private static String escape(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 从JSON字符串解析为Map（简易版本）
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseJSON(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }
        
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return result;
        
        // 简化解析（仅支持基本键值对）
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("^\"|\"$", "");
                String value = kv[1].trim();
                
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    result.put(key, value.substring(1, value.length() - 1));
                } else if (value.equals("true") || value.equals("false")) {
                    result.put(key, Boolean.parseBoolean(value));
                } else {
                    try {
                        if (value.contains(".")) {
                            result.put(key, Double.parseDouble(value));
                        } else {
                            result.put(key, Integer.parseInt(value));
                        }
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                }
            }
        }
        return result;
    }
}
