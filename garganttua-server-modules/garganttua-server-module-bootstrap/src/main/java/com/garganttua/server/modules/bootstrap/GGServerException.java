package com.garganttua.server.modules.bootstrap;

import lombok.Data;

@Data
public class GGServerException extends Exception {

	private Exception e;

	public GGServerException(Exception e) {
		this.e = e;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6686134327629762528L;

}
