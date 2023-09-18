package com.garganttua.server.modules.events.sources.file.json;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.garganttua.events.context.json.sources.file.GGEventsContextJsonFileSource;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.server.core.deployment.artefacts.GGServerApplicationConfiguration;
import com.garganttua.server.core.exceptions.GGServerApplicationException;
import com.garganttua.server.core.execution.IGGServerApplicationEngine;

@Configuration
public class GGServerModuleEventSourceFileJson {
	
	@Value("${com.garganttua.events.sources.file.json}")
	private String[] sources;
	
	@Autowired
	private IGGServerApplicationEngine engine;
	
	@Bean 
	public List<IGGEventsContextSource> getSources() throws GGServerApplicationException{
		List<IGGEventsContextSource> sources = new ArrayList<IGGEventsContextSource>();
		
//		String[] configurations = System.getProperty("spring.config.location").split(",");
		
		List<GGServerApplicationConfiguration> configurations = this.engine.getConfigurations();
		
		for( GGServerApplicationConfiguration configuration: configurations ) {
			if( configuration.getFileName().endsWith(".ggc") ) {
				sources.add(new GGEventsContextJsonFileSource(configuration.getPathStr()));
			}
		}
		
		for( String source: this.sources) {
			if( source.endsWith(".ggc") ) {
				sources.add(new GGEventsContextJsonFileSource(source));
			}
		}

		return sources;
	}
}
