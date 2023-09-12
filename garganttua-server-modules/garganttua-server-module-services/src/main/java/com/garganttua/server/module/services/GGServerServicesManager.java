package com.garganttua.server.module.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.garganttua.server.core.execution.IGGServerApplicationEngine;
import com.garganttua.server.core.execution.IGGServerApplicationEngineShutdownHandler;
import com.garganttua.server.core.services.IGGServerService;
import com.garganttua.server.core.services.IGGServerServicesManager;
import com.garganttua.server.core.services.GGServerServiceCommandRight;
import com.garganttua.server.core.services.GGServerServiceException;
import com.garganttua.server.core.services.GGServerServiceExceptionLabels;
import com.garganttua.server.core.services.GGServerServicePriority;
import com.garganttua.server.core.services.GGServerServiceStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GGServerServicesManager implements IGGServerServicesManager, IGGServerService, IGGServerApplicationEngineShutdownHandler {

	@Getter
	@Autowired
	private List<IGGServerService> services;

	private List<IGGServerService> systemPriority;
	private List<IGGServerService> highPriority;
	private List<IGGServerService> medPriority;
	private List<IGGServerService> lowPriority;

	private GGServerServiceStatus status = GGServerServiceStatus.flushed;
	
	@Autowired
	@Qualifier(value="arguments")
	public String[] arguments;
	
	@Autowired
	@Qualifier(value="garganttuaServerEngine")
	private IGGServerApplicationEngine garganttuaserverEngine;

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) throws GGServerServiceException {
		log.info("Starting Garganttua Server Services");
		this.garganttuaserverEngine.registerShutdownHandler(this);
		this.init(GGServerServiceCommandRight.system, this.arguments);
		this.start(GGServerServiceCommandRight.system);
	}

	@Override
	public void start(GGServerServiceCommandRight right) throws GGServerServiceException {
		this.status = GGServerServiceStatus.starting;
		log.info("Garganttua Server Services Manager Starting");

		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[system] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[high] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[medium] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[low] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = GGServerServiceStatus.running;
	}

	@Override
	public void stop(GGServerServiceCommandRight right) throws GGServerServiceException {
		this.status = GGServerServiceStatus.stopping;
		log.info("Garganttua Server Services Manager Stopping");
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[low] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[medium] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[high] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[system] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = GGServerServiceStatus.stopped;

	}

	@Override
	public void flush(GGServerServiceCommandRight right) throws GGServerServiceException {
		this.status = GGServerServiceStatus.flushing;
		log.info("Garganttua Server Services Manager Flushing");
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[low] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[medium] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[high] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (GGServerServiceException e) {
					log.warn("[system] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = GGServerServiceStatus.flushed;

	}

	@Override
	public void init(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		this.status = GGServerServiceStatus.initializing;
		log.info("Garganttua Server Services Manager Initialization");
		this.systemPriority = new ArrayList<IGGServerService>();
		this.highPriority = new ArrayList<IGGServerService>();
		this.medPriority = new ArrayList<IGGServerService>();
		this.lowPriority = new ArrayList<IGGServerService>();
		
		this.services.add(this);
		
		this.services.forEach(service -> {
			if( service != this) {
				GGServerServicePriority priority = service.getPriority();
				if (priority != null) {
					switch (priority) {
					case system:
						this.systemPriority.add(service);
						break;
					case high:
						this.highPriority.add(service);
						break;
					default:
					case low:
						this.lowPriority.add(service);
						break;
					case medium:
						this.medPriority.add(service);
						break;
					}
					;
				} else {
					this.lowPriority.add(service);
				}
			}
		});

		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (GGServerServiceException e) {
					log.warn("[system] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (GGServerServiceException e) {
					log.warn("[high] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (GGServerServiceException e) {
					log.warn("[medium] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (GGServerServiceException e) {
					log.warn("[low] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = GGServerServiceStatus.initialized;
	}

	@Override
	public String getName() {
		return "garganttua-server-services-manager";
	}

	@Override
	public GGServerServiceStatus getStatus() {
		return this.status ;
	}

	@Override
	public GGServerServicePriority getPriority() {
		return GGServerServicePriority.system;
	}

	@Override
	public void restart(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}

	public void flushService(IGGServerService service, GGServerServiceCommandRight right) throws GGServerServiceException {
		if (service.getStatus() == GGServerServiceStatus.stopped) {
			service.flush(right);
		} else {
			log.debug("Service status is "+service.getStatus());
			throw new GGServerServiceException(GGServerServiceExceptionLabels.SERVICE_NOT_STOPPED);
		}
	}

	private void initService(IGGServerService service, GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		if (service.getStatus() == GGServerServiceStatus.flushed) {
			service.init(right, arguments==null?this.arguments:arguments);
		} else {
			log.debug("Service status is "+service.getStatus());
			throw new GGServerServiceException(GGServerServiceExceptionLabels.SERVICE_NOT_FLUSHED);
		}
	}

	private void startService(IGGServerService service, GGServerServiceCommandRight right) throws GGServerServiceException {
		if (service.getStatus() == GGServerServiceStatus.initialized) {
			service.start(right);
		} else {
			log.debug("Service status is "+service.getStatus());
			throw new GGServerServiceException(GGServerServiceExceptionLabels.SERVICE_NOT_INITIALIZED);
		}
	}

	private void stopService(IGGServerService service, GGServerServiceCommandRight right) throws GGServerServiceException {
		if (service.getStatus() == GGServerServiceStatus.running) {
			service.stop(right);
		} else {
			log.debug("Service status is "+service.getStatus());
			throw new GGServerServiceException(GGServerServiceExceptionLabels.SERVICE_NOT_RUNNING);
		}
	}

	@Override
	public void handleShutdown() {
		try {
			this.stop(GGServerServiceCommandRight.system);
		} catch (GGServerServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
