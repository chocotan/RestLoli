package io.loli.restloli.core.servlet.response;

import io.loli.restloli.core.servlet.LoliConfig;
import io.loli.restloli.core.servlet.config.AnnotationConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理request的类
 * 
 * @author choco
 * 
 */
public class ResponseGenerator {
    private Map<AnnotationConfig, Method> configMap;
    private Entry<AnnotationConfig, Method> currentRequestConfigEntry;
    private boolean is404 = false;

    public ResponseGenerator(LoliConfig initConfig) {
        this.configMap = initConfig.getConfigMap();
    }

    private Object[] generateMethodParams(HttpServletRequest request,
            AnnotationConfig config, String[] params) {
        List<Object> list = new ArrayList<Object>();
        if (config.getPathConfig().getArgs() == null) {
            return new Object[] {};
        }

        int index = 0;
        for (Entry<String, Class<?>> entry : config.getPathConfig().getArgs()
                .entrySet()) {
            Class<?> clazz = entry.getValue();
            String paramName = entry.getKey();
            Iterator<String> itr = config.getPathConfig().getParams()
                    .iterator();
            while (itr.hasNext()) {
                if (itr.next().contains("{" + paramName + "}")) {
                    String paramValue = params[index];
                    Object obj = null;
                    if (clazz == String.class) {
                        obj = paramValue;
                    } else if (clazz == int.class || clazz == Integer.class) {
                        obj = Integer.parseInt(paramValue);
                    }
                    list.add(obj);
                }
            }
            /*
             * if (config.getPathConfig().getParams() .contains("{" + paramName
             * + "}")) { String paramValue = params[index]; Object obj = null;
             * if (clazz == String.class) { obj = paramValue; } else if (clazz
             * == int.class || clazz == Integer.class) { obj =
             * Integer.parseInt(paramValue); } list.add(obj); }
             */
            index++;
        }
        return list.toArray();
    }

    private String[] getMethodParam(String pathInfo, String httpMethod) {
        List<String> params = new ArrayList<String>();
        for (Entry<AnnotationConfig, Method> entry : configMap.entrySet()) {
            AnnotationConfig config = entry.getKey();
            Matcher m = Pattern.compile(config.getPathConfig().getPath())
                    .matcher(pathInfo);
            if (!config.getPathConfig().getPath().contains("([a-zA-Z0-9]+)")) {
                // params.add(config.getPathConfig().getPath());
                if (m.matches()) {
                    this.currentRequestConfigEntry = entry;
                    return params.toArray(new String[params.size()]);
                } else {
                    continue;
                }
                // m.matches()会对m.find()的结果有影响
            } else if (m.find()) {
                for (int i = 1; i <= m.groupCount()
                        && config.getHttpTypeConfig().getHttpType().toString()
                                .equals(httpMethod); i++) {
                    this.currentRequestConfigEntry = entry;
                    params.add(m.group(i));
                }
                return params.toArray(new String[params.size()]);

            } else {
                continue;
            }

        }
        return null;
    }

    private String getPathInfo(HttpServletRequest request) {
        // Get the encoded url
        String pathInfo = request.getRequestURI();
        if (pathInfo.endsWith("/")) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        return pathInfo;
    }

    private Object invokeMethod(Method method, Object[] args) {
        Object responseObj = null;
        try {
            responseObj = method.invoke(method.getDeclaringClass()
                    .newInstance(), args);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return responseObj;
    }

    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = getPathInfo(request);
        String httpMethod = request.getMethod();
        // if request url matches method url
        String[] params = this.getMethodParam(pathInfo, httpMethod);
        is404 = params == null;
        Entry<AnnotationConfig, Method> entry = this.currentRequestConfigEntry;
        is404 = entry == null;
        if (!is404) {
            Method method = entry.getValue();
            Object[] objs = this.generateMethodParams(request, entry.getKey(),
                    params);
            Object responseObj = invokeMethod(method, objs);
            this.doResponse(request, response, responseObj);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void doResponse(HttpServletRequest request,
            HttpServletResponse response, Object responseObj)
            throws IOException, ServletException {
        if (responseObj instanceof String) {
            response.setContentType("text/plain");
            response.getOutputStream().print((String) responseObj);
        }

        if (is404) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}