package com.gtech.pegasus.server.module.spring;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gtech.pegasus.core.execution.IPGApplicationEngine;
import com.gtech.pegasus.core.execution.PGApplicationEngine;
import com.gtech.pegasus.core.services.IPGService;
import com.gtech.pegasus.core.services.PGServiceCommandRight;
import com.gtech.pegasus.core.services.PGServiceException;
import com.gtech.pegasus.core.services.PGServicePriority;
import com.gtech.pegasus.core.services.PGServiceStatus;
import com.gtech.pegasus.server.bootstrap.PGServer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan({"com.gtech"})
@Configuration
@Slf4j
public class PGServerSpringContext implements WebMvcConfigurer, IPGService {

	private static SpringApplication application;
	
	private static String[] args;

	private static ConfigurableApplicationContext context;
	
	private int threadPoolSize = 100;
	private int maxThreadPoolSize = 200;
	private long threadPoolKeepAliveTime = 100;
	private TimeUnit threadPoolKeepAliveTimeUnit = TimeUnit.SECONDS;
	private BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	
	private static PGServiceStatus status = PGServiceStatus.flushed;
	
	public static void main(String[] args) throws PGServiceException {

		PGServerSpringContext.args = args;
		PGServerSpringContext context = new PGServerSpringContext();
		
		StringBuilder propertieFiles = new StringBuilder();
		
		TomcatURLStreamHandlerFactory.disable();
		
		boolean configArg = false;
		for( String arg: args ) {
			if( configArg ) {
				String[] splits = arg.split(",");
				
				for( String configuration: splits ) {
					if( configuration.endsWith("properties")) {
						propertieFiles.append(configuration);
						propertieFiles.append(",");
					}
				}
				break;
			}
			if( arg.equals(PGApplicationEngine.PEGASUS_ARGUMENT_CONFIGURATIONS) ) {
				configArg = true;
			}
		}
		
		String propertieFiles__;
		if( !propertieFiles.isEmpty() && propertieFiles.toString().endsWith(",") ) {
			propertieFiles__ = propertieFiles.toString().substring(0, propertieFiles.toString().length() - 1);
		} else {
			propertieFiles__ = propertieFiles.toString();
		}
		
		System.setProperty("spring.config.location", propertieFiles__);
	
		context.start(PGServiceCommandRight.system);
	}
	
	@Override
	public void stop(PGServiceCommandRight right) throws PGServiceException{
		log.info("Stoping Pegasus Spring Context");
		PGServerSpringContext.status = PGServiceStatus.stopping;

		ExitCodeGenerator gen = new ExitCodeGenerator() {
			
			@Override
			public int getExitCode() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		
		PGServerSpringContext.context.close();

		PGServerSpringContext.status = PGServiceStatus.stopped;
	}

	
	@Bean 
	@Qualifier(value="deployFolder")
	public String deployFolder() {
		return PGServer.folders.deploy;
	}
	
	@Bean
	@Qualifier(value="pegasusEngine")
	public IPGApplicationEngine pegasusEngine() {
		return PGServer.engine;
	}
	
	@Bean
	public SpringApplication application() {
		return PGServerSpringContext.application;
	}
	
	@Bean
	@Qualifier(value="arguments")
	public String[] getArguments() {
		return PGServerSpringContext.args;
	}
	
	@Bean
	@Qualifier(value="ExecutorService")
	public ExecutorService executorService() {
		this.executorService = new ThreadPoolExecutor(this.threadPoolSize/2, this.maxThreadPoolSize/2, this.threadPoolKeepAliveTime, this.threadPoolKeepAliveTimeUnit, this.workQueue);
		return this.executorService;
	}
	
	@Bean
	@Qualifier(value="ScheduledExecutorService")
	public ScheduledExecutorService scheduledExecutorService() {
		this.scheduledExecutorService = Executors.newScheduledThreadPool(this.threadPoolSize/2);
		return this.scheduledExecutorService;
	}
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		WebMvcConfigurer.super.configurePathMatch(configurer);
		configurer.addPathPrefix("api", HandlerTypePredicate.forAnnotation(RestController.class));
	}
	
	/*
	 * TO BE REMOVED !!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Bean
	@Qualifier(value="packages")
	public String[] getpackages() {
		String[] packages = new String[1];
		packages[0] = "com.gtech";
		return packages;
	}

	@Override
	public void start(PGServiceCommandRight right) throws PGServiceException{
		log.info("Starting Pegasus Spring Context");
		PGServerSpringContext.status = PGServiceStatus.starting;
		PGServerSpringContext.application = new SpringApplication(PGServerSpringContext.class);
		PGServerSpringContext.application.setBannerMode(Banner.Mode.OFF);
		
		PGServerSpringContext.context = PGServerSpringContext.application.run();

		PGServerSpringContext.application.setBannerMode(Banner.Mode.CONSOLE);
		PGServerSpringContext.status = PGServiceStatus.running;
	}

	@Override
	public void init(PGServiceCommandRight right, String[] arguments) throws PGServiceException{
		PGServerSpringContext.status = PGServiceStatus.initializing;
		PGServerSpringContext.status = PGServiceStatus.initialized;
		
	}

	@Override
	public String getName() {
		return "pegasus-spring-context";
	}

	@Override
	public PGServiceStatus getStatus() {
		return PGServerSpringContext.status ;
	}

	@Override
	public PGServicePriority getPriority() {
		return PGServicePriority.system;
	}

	@Override
	public void flush(PGServiceCommandRight right) throws PGServiceException{
		PGServerSpringContext.status = PGServiceStatus.flushing;
		PGServerSpringContext.status = PGServiceStatus.flushed;
	}

	@Override
	public void restart(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}
	
}
