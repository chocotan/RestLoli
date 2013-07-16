package io.loli.restloli.core.init;

/**
 * 方法注解的配置信息
 * @author choco
 *
 */
public class AnnotationConfig {
	public AnnotationConfig(){
	}
	/**
	 * path配置
	 */
	private PathConfig pathConfig;
	/**
	 * httpType配置
	 */
	private HttpTypeConfig httpTypeConfig;
	//TODO 其他配置待添加
	
	public PathConfig getPathConfig() {
		return pathConfig;
	}
	public AnnotationConfig setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
		return this;
	}
	public HttpTypeConfig getHttpTypeConfig() {
		return httpTypeConfig;
	}
	public AnnotationConfig setHttpTypeConfig(HttpTypeConfig httpTypeConfig) {
		this.httpTypeConfig = httpTypeConfig;
		return this;
	}
}