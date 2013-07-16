package io.loli.restloli.core.init;

/**
 * http请求的配置
 * @author choco
 *
 */
public class HttpTypeConfig {
	private HttpType httpType;


	public HttpTypeConfig(HttpType httpType){
		this.httpType= httpType;
	}

	public HttpType getHttpType() {
		return httpType;
	}

	public HttpTypeConfig setHttpType(HttpType httpType) {
		this.httpType = httpType;
		return this;
	}
}
