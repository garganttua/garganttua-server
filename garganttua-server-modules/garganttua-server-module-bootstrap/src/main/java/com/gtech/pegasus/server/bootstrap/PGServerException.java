package com.gtech.pegasus.server.bootstrap;

import lombok.Data;

@Data
public class PGServerException extends Exception {

	private Exception e;

	public PGServerException(Exception e) {
		this.e = e;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6686134327629762528L;

}
