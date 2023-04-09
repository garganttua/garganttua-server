package com.gtech.pegasus.server.module.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.gtech.pegasus.core.execution.IPGApplicationEngine;
import com.gtech.pegasus.core.execution.IPGApplicationEngineShutdownHandler;
import com.gtech.pegasus.core.services.IPGService;
import com.gtech.pegasus.core.services.IPGServicesManager;
import com.gtech.pegasus.core.services.PGServiceCommandRight;
import com.gtech.pegasus.core.services.PGServiceException;
import com.gtech.pegasus.core.services.PGServiceExceptionLabels;
import com.gtech.pegasus.core.services.PGServicePriority;
import com.gtech.pegasus.core.services.PGServiceStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PGServicesManager implements IPGServicesManager, IPGService, IPGApplicationEngineShutdownHandler {

	@Getter
	@Autowired
	private List<IPGService> services;

	private List<IPGService> systemPriority;
	private List<IPGService> highPriority;
	private List<IPGService> medPriority;
	private List<IPGService> lowPriority;

	private PGServiceStatus status = PGServiceStatus.flushed;
	
	@Autowired
	@Qualifier(value="arguments")
	public String[] arguments;
	
	@Autowired
	@Qualifier(value="pegasusEngine")
	private IPGApplicationEngine pegasusEngine;

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) throws PGServiceException {
		log.info("Starting Pegasus Services");
		this.pegasusEngine.registerShutdownHandler(this);
		this.init(PGServiceCommandRight.system, this.arguments);
		this.start(PGServiceCommandRight.system);

	}

	@Override
	public void start(PGServiceCommandRight right) throws PGServiceException {
		this.status = PGServiceStatus.starting;
		log.info("Pegasus Services Manager Starting");

		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (PGServiceException e) {
					log.warn("[system] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (PGServiceException e) {
					log.warn("[high] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (PGServiceException e) {
					log.warn("[medium] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] starting service " + service.getName());
				try {
					this.startService(service, right);
				} catch (PGServiceException e) {
					log.warn("[low] starting service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = PGServiceStatus.running;
	}

	@Override
	public void stop(PGServiceCommandRight right) throws PGServiceException {
		this.status = PGServiceStatus.stopping;
		log.info("Pegasus Services Manager Stopping");
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (PGServiceException e) {
					log.warn("[low] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (PGServiceException e) {
					log.warn("[medium] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (PGServiceException e) {
					log.warn("[high] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] stopping service " + service.getName());
				try {
					this.stopService(service, right);
				} catch (PGServiceException e) {
					log.warn("[system] stopping service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = PGServiceStatus.stopped;

	}

	@Override
	public void flush(PGServiceCommandRight right) throws PGServiceException {
		this.status = PGServiceStatus.flushing;
		log.info("Pegasus Services Manager Flushing");
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (PGServiceException e) {
					log.warn("[low] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (PGServiceException e) {
					log.warn("[medium] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (PGServiceException e) {
					log.warn("[high] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] flushing service " + service.getName());
				try {
					this.flushService(service, right);
				} catch (PGServiceException e) {
					log.warn("[system] flushing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = PGServiceStatus.flushed;

	}

	@Override
	public void init(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		this.status = PGServiceStatus.initializing;
		log.info("Pegasus Services Manager Initialization");
		this.systemPriority = new ArrayList<IPGService>();
		this.highPriority = new ArrayList<IPGService>();
		this.medPriority = new ArrayList<IPGService>();
		this.lowPriority = new ArrayList<IPGService>();
		
		this.services.add(this);
		
		this.services.forEach(service -> {
			if( service != this) {
				PGServicePriority priority = service.getPriority();
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

		log.info("Pegasus Services Manager Initialization");
		this.systemPriority.forEach(service -> {
			if( service != this) {
				log.info("[system] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (PGServiceException e) {
					log.warn("[system] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.highPriority.forEach(service -> {
			if( service != this) {
				log.info("[high] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (PGServiceException e) {
					log.warn("[high] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.medPriority.forEach(service -> {
			if( service != this) {
				log.info("[medium] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (PGServiceException e) {
					log.warn("[medium] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.lowPriority.forEach(service -> {
			if( service != this) {
				log.info("[low] initializing service " + service.getName());
				try {
					this.initService(service, right, arguments==null?this.arguments:arguments);
				} catch (PGServiceException e) {
					log.warn("[low] initializing service " + service.getName() + " : " + e.getMessage());
				}
			}
		});
		this.status = PGServiceStatus.initialized;
	}

	@Override
	public String getName() {
		return "pegasus-services-manager";
	}

	@Override
	public PGServiceStatus getStatus() {
		return this.status ;
	}

	@Override
	public PGServicePriority getPriority() {
		return PGServicePriority.system;
	}

	@Override
	public void restart(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}

	public void flushService(IPGService service, PGServiceCommandRight right) throws PGServiceException {
		if (service.getStatus() == PGServiceStatus.stopped) {
			service.flush(right);
		} else {
			throw new PGServiceException(PGServiceExceptionLabels.SERVICE_NOT_STOPPED);
		}
	}

	private void initService(IPGService service, PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		if (service.getStatus() == PGServiceStatus.flushed) {
			service.init(right, arguments==null?this.arguments:arguments);
		} else {
			throw new PGServiceException(PGServiceExceptionLabels.SERVICE_NOT_FLUSHED);
		}
	}

	private void startService(IPGService service, PGServiceCommandRight right) throws PGServiceException {
		if (service.getStatus() == PGServiceStatus.initialized) {
			service.start(right);
		} else {
			throw new PGServiceException(PGServiceExceptionLabels.SERVICE_NOT_INITIALIZED);
		}
	}

	private void stopService(IPGService service, PGServiceCommandRight right) throws PGServiceException {
		if (service.getStatus() == PGServiceStatus.running) {
			service.stop(right);
		} else {
			throw new PGServiceException(PGServiceExceptionLabels.SERVICE_NOT_RUNNING);
		}
	}

	@Override
	public void handleShutdown() {
		try {
			this.stop(PGServiceCommandRight.system);
		} catch (PGServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
