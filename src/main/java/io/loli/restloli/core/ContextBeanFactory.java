package io.loli.restloli.core;

import io.loli.restloli.spring.BeanFactory;

public class ContextBeanFactory {

    private final static String[] FACTORYS = { "io.loli.restloli.spring.SpringBeanFactory" };
    private static BeanFactory factory = null;
    static {
        for (int i = 0; i < FACTORYS.length; i++) {
            try {
                factory = (BeanFactory) (Class.forName(FACTORYS[i])
                        .newInstance());
            } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException e) {
                continue;
            }
            break;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        T instance = null;
        if (factory == null) {
            factory = new BeanFactory() {
            };
        }
        try {
            instance = (T) factory.getInstance(clazz);
        } catch (Exception e) {
            System.err.println("Failed to load class named "
                    + clazz.getCanonicalName() + ": " + e.getMessage());
        }
        return instance;
    }

}
