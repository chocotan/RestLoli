package io.loli.restloli.test;

import io.loli.restloli.core.servlet.LoliServlet;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyBaseTest {
    protected static ApacheHttpClient4 client;
    protected static DefaultApacheHttpClient4Config config;

    protected String output;
    protected final static String GET = "GET";
    protected final static String POST = "POST";
    protected WebResource webResource;
    protected static Map<String, String> paramMap;
    protected String host = "http://localhost:8080";
    protected static ObjectMapper mapper;
    private static HttpServer server;

    @BeforeClass
    public static void start() throws IOException {
        server = new HttpServer();
        NetworkListener listener = new NetworkListener("grizzly2", "localhost",
                8080);
        server.addListener(listener);
        WebappContext ctx = new WebappContext("ctx", "");
        final ServletRegistration reg = ctx.addServlet("loli",
                new LoliServlet());
        reg.addMapping("/*");
        reg.setInitParameter("io.loli.restloli.core.init.PackageName",
                "io.loli.restloli.test.web");
        // 不加这个则会在每个name前面加上@
        reg.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
                "true");
        ctx.addContextInitParameter("contextConfigLocation",
                "classpath:applicationContext.xml");
        ctx.addListener("io.loli.restloli.spring.SpringContextLoaderListener");
        ctx.addListener("org.springframework.web.context.request.RequestContextListener");
        ctx.deploy(server);
        server.start();
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }

    @Before
    public void setUp() {
        client = ApacheHttpClient4.create();
    }

    protected void connect(String url, String method, String mediaType,
            Map<String, String> paramMap) {
        url = host + url;
        webResource = client.resource(url);
        if (method.equals(GET)) {
            output = webResource.accept(mediaType).get(String.class);
        } else {
            MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
            paramMap.entrySet().stream().forEach((entry) -> {
                queryParams.add(entry.getKey(), entry.getValue());
            });
            output = webResource
                    .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .accept(mediaType).post(String.class, queryParams);
        }
    }

    protected void connect(String url) {
        url = host + url;
        webResource = client.resource(url);
        output = webResource.get(String.class);
    }
}