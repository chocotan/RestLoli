package io.loli.restloli.core.servlet.config;

import java.lang.reflect.Method;
import java.util.Map;

public interface ConfigWrapper {
    Map<? extends AnnotationConfig, ? extends Method> wrap(
            Map<AnnotationConfig, Method> config);
}
