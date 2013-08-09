package io.loli.restloli.core.servlet.config;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
public class PathWrapper implements ConfigWrapper {
    /**
     * 读取单个类获取配置
     * 
     * @param clazz
     * @return 配置map
     */
    public Map<? extends AnnotationConfig, ? extends Method> wrap(Class<?> clazz) {
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
                    if (params.size() > 0) {
                        pathConfig.setArgs(generateArgs(method));
                    }
                    map.put(new AnnotationConfig().setPathConfig(pathConfig),
                            method);
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
    @Override
    public Map<? extends AnnotationConfig, ? extends Method> wrap(
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
     * 判断一个String是否已经被编码过
     * 
     * @param oldurl
     * @return 如果被编码过则返回true 没有则返回false
     * @throws UnsupportedEncodingException
     */
    private static boolean isEncoded(String oldurl)
            throws UnsupportedEncodingException {
        return URLDecoder.decode(oldurl, "UTF-8").equals(oldurl) ? false : true;
    }

    /**
     * 给一个String进行编码
     * 
     * @param oldurl
     *            需要编码的url
     * @return 编码后的url
     */
    private static String encode(String oldurl) {
        try {
            if (isEncoded(oldurl)) {
                return oldurl;
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // Add support for url encoding
        String finalvalue = null;
        try {
            // Use utf-8 for encoding
            finalvalue = URLEncoder.encode(oldurl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            finalvalue = oldurl;
        }
        return finalvalue;
    }

    /**
     * 获取一个method的带@PathParam注解的参数信息
     * 
     * @param method
     * @return 配置map
     */
    private static Map<String, Class<?>> generateArgs(Method method) {
        final Annotation[][] paramAnnotations = method
                .getParameterAnnotations();
        final Class<?>[] paramTypes = method.getParameterTypes();
        Map<String, Class<?>> map = new TreeMap<String, Class<?>>();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation a : paramAnnotations[i]) {
                if (a instanceof PathParam) {
                    // Add support for url encoding
                    String pathvalue = ((PathParam) a).value();
                    String finalvalue = encode(pathvalue);
                    // Put the encoded string into map
                    map.put(finalvalue, paramTypes[i]);
                }
            }
        }
        return map;
    }
}