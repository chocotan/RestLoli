package io.loli.restloli.core.servlet.config;

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
public class ConfigLoader {
    private Map<AnnotationConfig, Method> map = new HashMap<AnnotationConfig, Method>();

    public ConfigLoader(Set<Class<?>> classes) {
        init(classes);
    }

    public Map<AnnotationConfig, Method> load() {
        return new HttpTypeWrapper().wrap(map);
    }

    private void init(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            map.putAll(new PathWrapper().wrap(clazz));
        }
    }
}
