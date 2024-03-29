package com.garganttua.server.modules.console.commands;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.garganttua.server.core.exceptions.GGServerApplicationException;
import com.garganttua.server.core.services.GGServerServiceCommandRight;
import com.garganttua.server.core.services.GGServerServiceException;
import com.garganttua.server.core.services.IGGServerService;
import com.garganttua.server.core.services.IGGServerServicesManager;
import com.garganttua.server.modules.console.tables.TableUtils;

@ShellComponent
public class ServicesCommands {

	@Autowired
	private IGGServerServicesManager manager;
	
	@Autowired
	@Qualifier(value="arguments")
	public String[] arguments;
	
	@ShellMethod("Get services list")
    public String getServicesList() throws IOException, GGServerApplicationException {
		
		String[] headers = {"Name", "Priority", "Status"};
		List<IGGServerService> services = this.manager.getServices();
		
		String[][] rows = new String[services.size()][];
		
		int i = 0;
		for(IGGServerService service: services){
		
			rows[i] = new String[] {service.getName(), 
					service.getPriority()==null?"unknown":service.getPriority().toString(), 
					service.getStatus()==null?"unknown":service.getStatus().toString()};
					
			i++;		
		};

		String rend = TableUtils.renderTable(headers, rows);

        return rend;
    }
	
	@ShellMethod("Start a service")
    public String startService(String arg) throws IOException, GGServerApplicationException {
		
		List<IGGServerService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IGGServerService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.start(GGServerServiceCommandRight.user);
				} catch (GGServerServiceException e) {
					ret = e.getMessage();
				}
				found = true;
				break;
			}
		
		};
		if( !found ) {
			ret = "service not found";
		}
        return ret;
    }
	
	@ShellMethod("Restart a service")
    public String restartService(String arg) throws IOException, GGServerApplicationException {
		
		List<IGGServerService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IGGServerService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.restart(GGServerServiceCommandRight.user, this.arguments);
				} catch (GGServerServiceException e) {
					ret = e.getMessage();
				}
				found = true;
				break;
			}
		};
		if( !found ) {
			ret = "service not found";
		}
        return ret;
    }
	
	@ShellMethod("Stop a service")
    public String stopService(String arg) throws IOException, GGServerApplicationException {
		
		List<IGGServerService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IGGServerService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.stop(GGServerServiceCommandRight.user);
				} catch (GGServerServiceException e) {
					ret = e.getMessage();
				}
				found = true;
				break;
			}
		};
		if( !found ) {
			ret = "service not found";
		}
        return ret;
    }
	
	@ShellMethod("Init a service")
    public String initService(String arg) throws IOException, GGServerApplicationException {
		
		List<IGGServerService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IGGServerService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.init(GGServerServiceCommandRight.user, this.arguments);
				} catch (GGServerServiceException e) {
					ret = e.getMessage();
				}
				found = true;
				break;
			}
		};
		if( !found ) {
			ret = "service not found";
		}
        return ret;
    }
	
	@ShellMethod("Flush a service")
    public String flushService(String arg) throws IOException, GGServerApplicationException {
		
		List<IGGServerService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IGGServerService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.flush(GGServerServiceCommandRight.user);
				} catch (GGServerServiceException e) {
					ret = e.getMessage();
				}
				found = true;
				break;
			}
		};
		if( !found ) {
			ret = "service not found";
		}
        return ret;
    }
	
}
