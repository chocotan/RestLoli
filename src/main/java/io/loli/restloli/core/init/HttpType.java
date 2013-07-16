package io.loli.restloli.core.init;

import javax.ws.rs.HttpMethod;

/**
 * http请求种类
 * 
 * @author choco
 * 
 */
public enum HttpType {
    GET(HttpMethod.GET), POST(HttpMethod.POST), PUT(HttpMethod.PUT), DELETE(
            HttpMethod.DELETE);
    private String des;

    private HttpType(String string) {
        des = string;
    }

    public String toString() {
        return des;
    }
}
