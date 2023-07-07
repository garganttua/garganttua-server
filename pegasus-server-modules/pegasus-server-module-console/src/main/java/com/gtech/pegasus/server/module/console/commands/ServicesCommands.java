package com.gtech.pegasus.server.module.console.commands;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.garganttua.server.core.exceptions.PGApplicationException;
import com.garganttua.server.core.services.IPGService;
import com.garganttua.server.core.services.IPGServicesManager;
import com.garganttua.server.core.services.PGServiceCommandRight;
import com.garganttua.server.core.services.PGServiceException;
import com.gtech.pegasus.server.module.console.tables.TableUtils;

import sshd.shell.springboot.autoconfiguration.SshdShellCommand;

@Component
@SshdShellCommand(value = "services", description = "Commands for managing services")
public class ServicesCommands {

	@Autowired
	private IPGServicesManager manager;
	
	@Autowired
	@Qualifier(value="arguments")
	public String[] arguments;
	
	@SshdShellCommand(value = "list", description = "")
    public String getServicesList(String arg) throws IOException, PGApplicationException {
		
		String[] headers = {"Name", "Priority", "Status"};
		List<IPGService> services = this.manager.getServices();
		
		String[][] rows = new String[services.size()][];
		
		int i = 0;
		for(IPGService service: services){
		
			rows[i] = new String[] {service.getName(), 
					service.getPriority()==null?"unknown":service.getPriority().toString(), 
					service.getStatus()==null?"unknown":service.getStatus().toString()};
					
			i++;		
		};

		String rend = TableUtils.renderTable(headers, rows);

        return rend;
    }
	
	@SshdShellCommand(value = "start", description = "")
    public String startService(String arg) throws IOException, PGApplicationException {
		
		List<IPGService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IPGService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.start(PGServiceCommandRight.user);
				} catch (PGServiceException e) {
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
	
	@SshdShellCommand(value = "restart", description = "")
    public String restartService(String arg) throws IOException, PGApplicationException {
		
		List<IPGService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IPGService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.restart(PGServiceCommandRight.user, this.arguments);
				} catch (PGServiceException e) {
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
	
	@SshdShellCommand(value = "stop", description = "")
    public String stopService(String arg) throws IOException, PGApplicationException {
		
		List<IPGService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IPGService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.stop(PGServiceCommandRight.user);
				} catch (PGServiceException e) {
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
	
	@SshdShellCommand(value = "init", description = "")
    public String initService(String arg) throws IOException, PGApplicationException {
		
		List<IPGService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IPGService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.init(PGServiceCommandRight.user, this.arguments);
				} catch (PGServiceException e) {
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
	
	@SshdShellCommand(value = "flush", description = "")
    public String flushService(String arg) throws IOException, PGApplicationException {
		
		List<IPGService> services = this.manager.getServices();
		String ret = "OK";
		boolean found = false; 
		
		for(IPGService service: services){
		
			if( arg.equals(service.getName()) ) {
				try {
					service.flush(PGServiceCommandRight.user);
				} catch (PGServiceException e) {
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
