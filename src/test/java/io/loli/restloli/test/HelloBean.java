package io.loli.restloli.test;

import javax.inject.Named;

@Named
public class HelloBean {
    public String hello(){
        return "hello";
    }
}
