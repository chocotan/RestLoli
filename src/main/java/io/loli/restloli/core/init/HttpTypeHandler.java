package io.loli.restloli.core.init;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * 获取GET,POST等注解并写入配置map
 * 
 * @author choco
 * 
 */
public class HttpTypeHandler {
    public static Map<? extends AnnotationConfig, ? extends Method> handle(
            Map<AnnotationConfig, Method> map) {
        for (Entry<AnnotationConfig, Method> entry : map.entrySet()) {
            Method method = entry.getValue();
            AnnotationConfig config = entry.getKey();
            if (method.isAnnotationPresent(GET.class))
                config.setHttpTypeConfig(new HttpTypeConfig(HttpType.GET));
            else if (method.isAnnotationPresent(POST.class))
                config.setHttpTypeConfig(new HttpTypeConfig(HttpType.POST));
            else if (method.isAnnotationPresent(PUT.class))
                config.setHttpTypeConfig(new HttpTypeConfig(HttpType.PUT));
            else if (method.isAnnotationPresent(DELETE.class))
                config.setHttpTypeConfig(new HttpTypeConfig(HttpType.DELETE));
            else
                map.remove(config);
        }
        return map;
    }

}
