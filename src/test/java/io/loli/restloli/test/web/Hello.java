package io.loli.restloli.test.web;

import io.loli.restloli.test.HelloBean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Named
@Path("hello")
public class Hello {
    @Inject
    private HelloBean helloBean;

    @GET
    @Path("")
    public String hello() {
        return helloBean.hello();
    }

}
