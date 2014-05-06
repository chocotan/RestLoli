package io.loli.restloli.core.servlet.config;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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
            rootPath = filterRootPath(rootPath);
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
                    fullPath = decode(fullPath);
                    PathConfig pathConfig = new PathConfig(fullPath);
                    Map<String, String> params = findParamsByPath(tempP);
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

    private String filterRootPath(String path) {
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
     * 更新配置map
     * 
     * @param configMap
     * @return 更新后的map
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
                    path = filterRootPath(path);
                    String tempP = new String(path);
                    path = removeParam(path);
                    String fullPath = rootPath.concat(path);
                    fullPath = decode(fullPath);
                    PathConfig pathConfig = new PathConfig(fullPath);
                    Map<String, String> params = findParamsByPath(tempP);
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
     * @return 处理后的path
     */
    public String removeParam(String path) {

        Pattern p = Pattern.compile("\\{[^{]+\\}");
        Matcher m = p.matcher(path);
        while (m.find()) {
            String s = m.group();
            String[] ss = getParamRegex(s);
            path = path.replace(s, "(" + ss[1] + ")");
        }
        return path;
    }

    /**
     * 获取param的regex
     * 
     * @return 如果param不是 xxx:xxx的形式则匹配所有
     */
    private String[] getParamRegex(String param) {
        if (param.matches("\\{[a-zA-Z0-9]+:.+\\}")) {
            String[] ss = param.split(":");
            return new String[] { ss[0].substring(1),
                    ss[1].substring(0, ss[1].length() - 1) };
        } else {
            return new String[] { param.substring(1, param.length() - 1), ".*" };
        }
    }

    /**
     * 给path开头增加"/",结尾删除"/"
     * 
     * @return 更新后的path
     */
    private String filterPath(String path) {
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
     * 获取一个方法@Path注解中的参数以及它的regex
     * 
     * @return 参数map @Path("{test:.+}"): map.put("test",".+")
     */
    private Map<String, String> findParamsByPath(String path) {
        Pattern p = Pattern.compile("\\{[^{]+\\}");
        Matcher m = p.matcher(path);
        Map<String, String> map = new TreeMap<String, String>();
        while (m.find()) {
            String[] result = getParamRegex(m.group());
            map.put(result[0], result[1]);
        }
        return map;
    }

    /**
     * 判断一个String是否已经被编码过
     * 
     * @param oldurl
     * @return 如果被编码过则返回true 没有则返回false
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("unused")
    private boolean isEncoded(String oldurl)
            throws UnsupportedEncodingException {
        return URLDecoder.decode(oldurl, "UTF-8").equals(oldurl) ? false : true;
    }

    /**
     * 给一个String进行解码
     * 
     * @param oldurl
     *            需要编码的url
     * @return 编码后的url
     */
    private String decode(String oldurl) {
        // Add support for url encoding
        String finalvalue = null;
        try {
            // Use utf-8 for encoding
            finalvalue = URLDecoder.decode(oldurl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return finalvalue;
    }

    /**
     * 获取一个method的带@PathParam注解的参数信息
     * 
     * @param method
     * @return 配置map
     */
    private Map<String, Class<?>> generateArgs(Method method) {
        final Annotation[][] paramAnnotations = method
                .getParameterAnnotations();
        final Class<?>[] paramTypes = method.getParameterTypes();
        Map<String, Class<?>> map = new TreeMap<String, Class<?>>();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation a : paramAnnotations[i]) {
                if (a instanceof PathParam) {
                    String pathvalue = ((PathParam) a).value();
                    // Put the encoded string into map
                    map.put(pathvalue, paramTypes[i]);
                }
            }
        }
        return map;
    }
}