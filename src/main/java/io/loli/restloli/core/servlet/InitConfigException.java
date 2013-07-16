package io.loli.restloli.core.servlet;

public class InitConfigException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InitConfigException() {
		super();
	}

	public InitConfigException(String errorInfo) {
		super(errorInfo);
	}
}
