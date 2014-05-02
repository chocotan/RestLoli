package io.loli.restloli.test.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("hello")
public class Hello {

    @GET
    @Path("")
    public String hello(){
        return "hello";
    }
}
