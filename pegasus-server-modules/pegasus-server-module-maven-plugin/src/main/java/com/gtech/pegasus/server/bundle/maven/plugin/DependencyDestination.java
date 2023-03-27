package com.gtech.pegasus.server.bundle.maven.plugin;

import java.io.File;

public enum DependencyDestination {
	
	bin, binlibs, libs, deploy, conf, none;
	
	public String toString() {
		switch(this) {
		case bin:
			return "bin";
		case binlibs:
			return "bin"+File.separator+"libs";
		case conf:
			return "conf";
		case deploy:
			return "deploy";
		case libs:
			return "libs";
		default:
		case none:

		}
		return "";
	}

}
