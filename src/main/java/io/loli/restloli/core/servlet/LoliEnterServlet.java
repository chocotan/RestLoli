package io.loli.restloli.core.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 核心Servlet类, 需要在web.xml中配置 容器启动-servlet加载-执行init方法-加载class-获读取注解
 * 客户端发出请求-拦截请求-和注解信息比较-相同则执行相应的方法-返回视图
 * 
 * @author choco
 * 
 */
public class LoliEnterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private InitConfig initConfig;
	private RequestHandler requestHandler;

	public LoliEnterServlet() {
		super();
	}

	/**
	 * Servlet加载时执行
	 */
	@Override
	public void init(ServletConfig config) {
		if (initConfig == null) {
			initConfig = new InitConfig();
		}
		initConfig.loadConfig(config);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (requestHandler == null) {
			requestHandler = new RequestHandler(this.initConfig);
		}
		requestHandler.service(request, response);
	}
}
