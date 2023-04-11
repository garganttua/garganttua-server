package com.gtech.pegasus.server.module.console;

import java.io.IOException;

import org.apache.sshd.server.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.gtech.pegasus.core.services.IPGService;
import com.gtech.pegasus.core.services.PGServiceCommandRight;
import com.gtech.pegasus.core.services.PGServiceException;
import com.gtech.pegasus.core.services.PGServiceExceptionLabels;
import com.gtech.pegasus.core.services.PGServicePriority;
import com.gtech.pegasus.core.services.PGServiceStatus;

@Service
@ComponentScan("sshd.shell.springboot")
public class SshService implements IPGService {

	@Autowired
	private SshServer sshServer;
	
	@Override
	public void restart(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		right = PGServiceCommandRight.system;
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}

	@Override
	public void start(PGServiceCommandRight right) throws PGServiceException {
		if( right != PGServiceCommandRight.system)
			throw new PGServiceException(PGServiceExceptionLabels.UNAUTHORIZED);

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
	public void stop(PGServiceCommandRight right) throws PGServiceException {
		if( right != PGServiceCommandRight.system)
			throw new PGServiceException(PGServiceExceptionLabels.UNAUTHORIZED);
		
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
	public void init(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		if( right != PGServiceCommandRight.system)
			throw new PGServiceException(PGServiceExceptionLabels.UNAUTHORIZED);

	}

	@Override
	public String getName() {
		return "pegasus-ssh-server";
	}

	@Override
	public PGServiceStatus getStatus() {
		PGServiceStatus status = null;
		if (this.sshServer.isClosed()) {
			status = PGServiceStatus.stopped;
		}
		if (this.sshServer.isClosing()) {
			status = PGServiceStatus.stopping;
		}
		if (this.sshServer.isOpen()) {
			status = PGServiceStatus.running;
		}
		if (this.sshServer.isEmpty()) {
			status = PGServiceStatus.initialization_error;
		}
		if (this.sshServer.isStarted()) {
			status = PGServiceStatus.running;
		}

		return status;
	}

	@Override
	public PGServicePriority getPriority() {
		return PGServicePriority.system;
	}

	@Override
	public void flush(PGServiceCommandRight right) throws PGServiceException {
		if( right != PGServiceCommandRight.system)
			throw new PGServiceException(PGServiceExceptionLabels.UNAUTHORIZED);
	}

}
