package com.garganttua.server.modules.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spring.IGGEventsEngineSpringBean;
import com.garganttua.server.core.services.GGServerServiceCommandRight;
import com.garganttua.server.core.services.GGServerServiceException;
import com.garganttua.server.core.services.GGServerServicePriority;
import com.garganttua.server.core.services.GGServerServiceStatus;
import com.garganttua.server.core.services.IGGServerService;

@Service 
public class GGServerModuleEventsService implements IGGServerService {
	
	@Autowired
	private IGGEventsEngineSpringBean engine;
	
	private GGServerServiceStatus status = GGServerServiceStatus.flushed;

	@Override
	public void restart(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}

	@Override
	public void start(GGServerServiceCommandRight right) throws GGServerServiceException {
		this.status = GGServerServiceStatus.starting;
		try {
			this.engine.start();
		} catch (GGEventsException e) {
			this.status = GGServerServiceStatus.starting_error;
			throw new GGServerServiceException(e);
		}
		this.status = GGServerServiceStatus.running;
	}

	@Override
	public void stop(GGServerServiceCommandRight right) throws GGServerServiceException {
		this.status = GGServerServiceStatus.stopping;
		try {
			this.engine.stop();
		} catch (GGEventsException e) {
			this.status = GGServerServiceStatus.stopping_error;
			throw new GGServerServiceException(e);
		}
		this.status = GGServerServiceStatus.stopped;
	}

	@Override
	public void init(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		this.status = GGServerServiceStatus.initializing;
		try {
			this.engine.init();
		} catch (GGEventsException e) {
			this.status = GGServerServiceStatus.initialization_error;
			throw new GGServerServiceException(e);
		}
		this.status = GGServerServiceStatus.initialized;
	}

	@Override
	public void flush(GGServerServiceCommandRight right) throws GGServerServiceException {
		this.status = GGServerServiceStatus.flushing;
		try {
			this.engine.flush();
		} catch (GGEventsException e) {
			this.status = GGServerServiceStatus.initialization_error;
			throw new GGServerServiceException(e);
		}
		this.status = GGServerServiceStatus.flushed;
	}

	@Override
	public String getName() {
		return "garganttua-server-events-service";
	}

	@Override
	public GGServerServiceStatus getStatus() {
		return this.status;
	}

	@Override
	public GGServerServicePriority getPriority() {
		return GGServerServicePriority.high;
	}

}
