package features;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private static Map<String, Object> context = new HashMap<>();

    private TestContext() {

    }

    public static void add(String key, Object value) {
        context.put(key, value);
    }

    public static Object get(String key, Object defaultValue) {
        if (!context.containsKey(key)) {
            context.put(key, defaultValue);
        }
        return context.get(key);
    }

    public static Object get(String key) {
        return context.get(key);
    }

    public static void remove(String key) {
        context.remove(key);
    }

    public static void removeObject(String key, Object value) {
        context.remove(key, value);
    }

    public static void clear() {
        context.clear();
    }

}
