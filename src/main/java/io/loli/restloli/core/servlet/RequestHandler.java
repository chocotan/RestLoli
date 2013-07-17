package io.loli.restloli.core.servlet;

import io.loli.restloli.core.init.AnnotationConfig;

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
public class RequestHandler {
    private Map<AnnotationConfig, Method> configMap;

    public RequestHandler(InitConfig initConfig) {
        this.configMap = initConfig.getConfigMap();
    }

    public Object[] methodParamGenerate(HttpServletRequest request,
            AnnotationConfig config, String paramValue) {
        List<Object> list = new ArrayList<Object>();
        if (config.getPathConfig().getArgs() == null) {
            return new Object[] {};
        }

        for (Entry<String, Class<?>> entry : config.getPathConfig().getArgs()
                .entrySet()) {
            Class<?> clazz = entry.getValue();
            String paramName = entry.getKey();
            if (config.getPathConfig().getParams().contains("{" + paramName
                    + "}")) {
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

    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo.endsWith("/")) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }
        String httpMethod = request.getMethod();
        // if request url matches method url
        boolean flag = false;
        for (Entry<AnnotationConfig, Method> entry : configMap.entrySet()) {
            AnnotationConfig config = entry.getKey();
            Method method = entry.getValue();
            // TODO Response处理
            Matcher m = Pattern.compile(config.getPathConfig().getPath())
                    .matcher(pathInfo);
            if (m.find()
                    && config.getHttpTypeConfig().getHttpType().toString()
                            .equals(httpMethod)) {
                if (!flag) {
                    flag = true;
                }
                String paramValue = m.group(1);
                Object responseObj = null;
                try {
                    Object[] args = methodParamGenerate(request, config,
                            paramValue);
                    responseObj = method.invoke(method.getDeclaringClass()
                            .newInstance(), args);
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
                if (responseObj instanceof String) {
                    response.setContentType("text/plain");
                    response.getOutputStream().print((String) responseObj);
                }
            } else {
                continue;
            }
        }
        if (!flag) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            ;
        }
    }

}
