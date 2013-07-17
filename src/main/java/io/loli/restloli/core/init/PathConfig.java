package io.loli.restloli.core.init;

import java.util.Map;
import java.util.Set;

/**
 * path的配置类
 * 
 * @author choco
 * 
 */
public class PathConfig {
    private String path;
    private Set<String> params;

    private Map<String,Class<?>> args;
    public PathConfig() {
    }

    public PathConfig(String path) {
        this.path = path;
    }

    public PathConfig(AnnotationConfig annotationConfig) {
        super();
    }

    public PathConfig setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Set<String> getParams() {
        return params;
    }

    public void setParams(Set<String> params) {
        this.params = params;
    }

    public Map<String, Class<?>> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Class<?>> args) {
        this.args = args;
    }
}
