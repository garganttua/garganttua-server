package com.garganttua.server.modules.console;

import java.io.IOException;

import org.apache.sshd.server.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.garganttua.server.core.services.GGServerServiceCommandRight;
import com.garganttua.server.core.services.GGServerServiceException;
import com.garganttua.server.core.services.GGServerServiceExceptionLabels;
import com.garganttua.server.core.services.GGServerServicePriority;
import com.garganttua.server.core.services.GGServerServiceStatus;
import com.garganttua.server.core.services.IGGServerService;

@Service
@ComponentScan("sshd.shell.springboot")
public class SshService implements IGGServerService {

	@Autowired
	private SshServer sshServer;
	
	@Override
	public void restart(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		right = GGServerServiceCommandRight.system;
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}

	@Override
	public void start(GGServerServiceCommandRight right) throws GGServerServiceException {
		if( right != GGServerServiceCommandRight.system)
			throw new GGServerServiceException(GGServerServiceExceptionLabels.UNAUTHORIZED);

		if( !this.sshServer.isStarted() ) {
			try {
				this.sshServer.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop(GGServerServiceCommandRight right) throws GGServerServiceException {
		if( right != GGServerServiceCommandRight.system)
			throw new GGServerServiceException(GGServerServiceExceptionLabels.UNAUTHORIZED);
		
		if( this.sshServer.isStarted() ) {
			try {
				this.sshServer.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void init(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		if( right != GGServerServiceCommandRight.system)
			throw new GGServerServiceException(GGServerServiceExceptionLabels.UNAUTHORIZED);

	}

	@Override
	public String getName() {
		return "GARGANTTUA_SERVER-ssh-server";
	}

	@Override
	public GGServerServiceStatus getStatus() {
		GGServerServiceStatus status = null;
		if (this.sshServer.isClosed()) {
			status = GGServerServiceStatus.stopped;
		}
		if (this.sshServer.isClosing()) {
			status = GGServerServiceStatus.stopping;
		}
		if (this.sshServer.isOpen()) {
			status = GGServerServiceStatus.running;
		}
		if (this.sshServer.isEmpty()) {
			status = GGServerServiceStatus.initialization_error;
		}
		if (this.sshServer.isStarted()) {
			status = GGServerServiceStatus.running;
		}

		return status;
	}

	@Override
	public GGServerServicePriority getPriority() {
		return GGServerServicePriority.system;
	}

	@Override
	public void flush(GGServerServiceCommandRight right) throws GGServerServiceException {
		if( right != GGServerServiceCommandRight.system)
			throw new GGServerServiceException(GGServerServiceExceptionLabels.UNAUTHORIZED);
	}

}
