package io.loli.restloli.core.servlet.response;

import io.loli.restloli.core.servlet.LoliConfig;
import io.loli.restloli.core.servlet.config.AnnotationConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

    public Object[] generateMethodParams(HttpServletRequest request,
            AnnotationConfig config, String paramValue) {
        List<Object> list = new ArrayList<Object>();
        if (config.getPathConfig().getArgs() == null) {
            return new Object[] {};
        }

        for (Entry<String, Class<?>> entry : config.getPathConfig().getArgs()
                .entrySet()) {
            Class<?> clazz = entry.getValue();
            String paramName = entry.getKey();
            if (config.getPathConfig().getParams()
                    .contains("{" + paramName + "}")) {
                try {
                    if (clazz.newInstance() instanceof String) {
                        list.add(paramValue);
                    } else {
                        list.add(Double.valueOf(paramValue));
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return list.toArray();
    }

    private String getMethodParam(String pathInfo, String httpMethod) {
        for (Entry<AnnotationConfig, Method> entry : configMap.entrySet()) {
            AnnotationConfig config = entry.getKey();
            Matcher m = Pattern.compile(config.getPathConfig().getPath())
                    .matcher(pathInfo);
            if (m.find()
                    && config.getHttpTypeConfig().getHttpType().toString()
                            .equals(httpMethod)) {
                this.currentRequestConfigEntry = entry;
                if (!config.getPathConfig().getPath()
                        .contains("([a-zA-Z0-9]+)")) {
                    return m.group();
                }
                return m.group(1);
            } else {
                continue;
            }
        }
        return null;
    }

    private String getPathInfo(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
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
        String paramValue = this.getMethodParam(pathInfo, httpMethod);
        is404 = paramValue == null;
        Entry<AnnotationConfig, Method> entry = this.currentRequestConfigEntry;
        is404 = entry == null;
        if (!is404) {
            Method method = entry.getValue();

            Object responseObj = invokeMethod(method,
                    this.generateMethodParams(request, entry.getKey(),
                            paramValue));
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
