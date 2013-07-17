package io.loli.restloli.core.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * 从class中读取path配置并写入配置map
 * 
 * @author choco
 * 
 */
public class PathHandler {
    /**
     * 读取单个类获取配置
     * 
     * @param clazz
     * @return 配置map
     */
    public static Map<? extends AnnotationConfig, ? extends Method> handle(
            Class<?> clazz) {
        String rootPath = clazz.getAnnotation(Path.class).value();
        Map<AnnotationConfig, Method> map = new HashMap<AnnotationConfig, Method>();
        if (rootPath == null) {
            throw new NullPointerException("path不能为空");
        } else {
            rootPath = filterPath(rootPath);
            for (Method method : clazz.getDeclaredMethods()) {
                String path = null;
                if (method.getAnnotations().length > 0) {
                    try {
                        path = method.getAnnotation(Path.class).value();
                    } catch (Exception e) {
                        continue;
                    }
                    String tempP = new String(path);
                    path = filterPath(path);
                    path = removeParam(path);
                    String fullPath = rootPath.concat(path);
                    PathConfig pathConfig = new PathConfig(fullPath);
                    Set<String> params = findParamsByPath(tempP);
                    pathConfig.setParams(params);
                    if (params.size() > 0){
                        pathConfig.setArgs(generateArgs(method));
                    }
                    map.put(new AnnotationConfig()
                            .setPathConfig(pathConfig), method);
                }
            }
        }
        return map;
    }

    /**
     * 更新配置map
     * 
     * @param configMap
     * @return
     */
    public static Map<? extends AnnotationConfig, ? extends Method> handle(
            Map<AnnotationConfig, Method> configMap) {
        for (Entry<AnnotationConfig, Method> entry : configMap.entrySet()) {
            Method method = entry.getValue();
            Class<?> clazz = method.getDeclaringClass();
            String rootPath = clazz.getAnnotation(Path.class).value();
            if (rootPath == null) {
                throw new NullPointerException("path不能为空");
            } else {
                rootPath = filterPath(rootPath);

                String path = method.getAnnotation(Path.class).value();

                if (path == null) {
                    configMap.put(new AnnotationConfig()
                            .setPathConfig(new PathConfig(path)), method);
                } else if (method.getAnnotations().length > 0) {
                    path = filterPath(path);
                    String tempP = new String(path);
                    path = removeParam(path);
                    String fullPath = rootPath.concat(path);
                    PathConfig pathConfig = new PathConfig(fullPath);
                    Set<String> params = findParamsByPath(tempP);
                    pathConfig.setParams(params);
                    if (params.size() > 0)
                        pathConfig.setArgs(generateArgs(method));
                    AnnotationConfig config = entry.getKey();

                    configMap.remove(config);
                    configMap.put(
                            new AnnotationConfig().setPathConfig(pathConfig),
                            method);
                }
            }
        }
        return configMap;
    }

    /**
     * 去除带{}的参数并用正则表达式替代
     * 
     * @param path
     * @return
     */
    public static String removeParam(String path) {
        return path.replaceAll("\\{[a-zA-Z0-9]+\\}", "([a-zA-Z0-9]+)");
    }

    /**
     * 给path开头增加"/",结尾删除"/"
     * 
     * @param path
     * @return 更新后的path
     */
    private static String filterPath(String path) {
        if (path.length() == 0)
            return path;
        if (path.charAt(0) != '/') {
            path = "/".concat(path);
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 获取一个方法@Path注解中的参数
     * 
     * @param path
     * @return
     */
    private static Set<String> findParamsByPath(String path) {
        Pattern p = Pattern.compile("\\{.*\\}");
        Matcher m = p.matcher(path);
        Set<String> set = new TreeSet<String>();
        while (m.find()) {
            String param = m.group();
            set.add(param);
        }
        return set;
    }

    /**
     * 获取一个method的带@PathParam注解的参数信息
     * 
     * @param method
     * @return
     */
    private static Map<String, Class<?>> generateArgs(Method method) {
        final Annotation[][] paramAnnotations = method
                .getParameterAnnotations();
        final Class<?>[] paramTypes = method.getParameterTypes();
        Map<String, Class<?>> map = new TreeMap<String, Class<?>>();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation a : paramAnnotations[i]) {
                if (a instanceof PathParam) {
                    map.put(((PathParam) a).value(), paramTypes[i]);
                }
            }
        }
        return map;
    }
}