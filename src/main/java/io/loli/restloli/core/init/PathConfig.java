package io.loli.restloli.core.init;

/**
 * path的配置类
 * @author choco
 *
 */
public class PathConfig {
	private String path;

	public PathConfig() {
	}

	public PathConfig(String path) {
		this.path = path;
	}

	public PathConfig(AnnotationConfig annotationConfig) {
		super();
	}

	public PathConfig setPath(String path) {
		this.path = path;
		return this;
	}

	public String getPath() {
		return path;
	}
}
