package io.loli.restloli.core.init;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 加载类并读取注解配置
 * 
 * @author choco
 * 
 */
public class AnnotationTypeHandler {
    private Map<AnnotationConfig, Method> map = new HashMap<AnnotationConfig, Method>();

    public Map<AnnotationConfig, Method> handle(Set<Class<?>> classes) {
        handlePath(classes);
        handleHttpType(map);
        // TODO 慢慢增加所有handler
        return map;
    }

    private void handlePath(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            map.putAll(PathHandler.handle(clazz));
        }
    }

    private void handleHttpType(Map<AnnotationConfig, Method> map) {
        HttpTypeHandler.handle(map);
    }
}
