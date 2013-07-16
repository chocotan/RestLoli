package io.loli.restloli.core;

import static org.junit.Assert.assertEquals;
import io.loli.restloli.core.init.PathHandler;

import javax.ws.rs.Path;

import org.junit.Test;

@Path("/test")
public class PathHandlerTest {
	@Path("/testHandler")
	@Test
	public void testHandler() {
		System.out.println(PathHandler.handle(LoliClassLoader.getClasses("io.loli.restloli.core").iterator().next()).entrySet().iterator()
				.next().getKey().getPathConfig().getPath());
		assertEquals("/test/testHandler",
				PathHandler.handle(LoliClassLoader.getClasses("io.loli.restloli.core").iterator().next()).entrySet().iterator()
						.next().getKey().getPathConfig().getPath());
	}
}
