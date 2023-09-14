package com.garganttua.server.modules.spring;

import java.util.Arrays;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.garganttua.server.core.execution.GGServerApplicationEngine;
import com.garganttua.server.core.execution.IGGServerApplicationEngine;
import com.garganttua.server.core.services.GGServerServiceCommandRight;
import com.garganttua.server.core.services.GGServerServiceException;
import com.garganttua.server.core.services.GGServerServicePriority;
import com.garganttua.server.core.services.GGServerServiceStatus;
import com.garganttua.server.core.services.IGGServerService;
import com.garganttua.server.modules.bootstrap.GGServer;
import com.garganttua.server.modules.bootstrap.GGServerFolders;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan({"com.garganttua","org.mongodb"})
@Configuration
@EnableAutoConfiguration
@Slf4j
public class GGServerSpringContext implements WebMvcConfigurer, IGGServerService {

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
	
	private static GGServerServiceStatus status = GGServerServiceStatus.flushed;
	
	public static void main(String[] args) throws GGServerServiceException {

		GGServerSpringContext.args = args;
		
		log.info("Garganttua Server Spring Context using "+Arrays.toString(args));
		
		GGServerSpringContext context = new GGServerSpringContext();
		
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
			if( arg.equals(GGServerApplicationEngine.GARGANTTUA_SERVER_ARGUMENT_CONFIGURATIONS) ) {
				configArg = true;
			}
		}
		
		String propertieFiles__;
		if( !propertieFiles.isEmpty() && propertieFiles.toString().endsWith(",") ) {
			propertieFiles__ = propertieFiles.toString().substring(0, propertieFiles.toString().length() - 1);
		} else {
			propertieFiles__ = propertieFiles.toString();
		}
		
		log.info("Using spring.config.location "+propertieFiles__);
		
		System.setProperty("spring.config.location", propertieFiles__);
	
		context.start(GGServerServiceCommandRight.system);
	}
	
	@Override
	public void stop(GGServerServiceCommandRight right) throws GGServerServiceException{
		log.info("Stoping Garganttua Server Spring Context");
		GGServerSpringContext.status = GGServerServiceStatus.stopping;

		ExitCodeGenerator gen = new ExitCodeGenerator() {
			
			@Override
			public int getExitCode() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		
		GGServerSpringContext.context.close();

		GGServerSpringContext.status = GGServerServiceStatus.stopped;
	}

	
	@Bean 
	@Qualifier(value="deployFolder")
	public String deployFolder() {
		return GGServerFolders.deploy;
	}
	
	@Bean
	@Qualifier(value="garganttuaServerEngine")
	public IGGServerApplicationEngine garganttuaServerEngine() {
		return GGServer.engine;
	}
	
	@Bean
	public SpringApplication application() {
		return GGServerSpringContext.application;
	}
	
	@Bean
	@Qualifier(value="arguments")
	public String[] getArguments() {
		return GGServerSpringContext.args;
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
		packages[0] = "com.garganttua";
		return packages;
	}

	@Override
	public void start(GGServerServiceCommandRight right) throws GGServerServiceException{
		log.info("Starting Garganttua Server Spring Context");
		GGServerSpringContext.status = GGServerServiceStatus.starting;
		GGServerSpringContext.application = new SpringApplication(GGServerSpringContext.class);
		GGServerSpringContext.application.setBannerMode(Banner.Mode.CONSOLE);
		
		GGServerSpringContext.context = GGServerSpringContext.application.run();

		GGServerSpringContext.status = GGServerServiceStatus.running;
	}

	@Override
	public void init(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException{
		GGServerSpringContext.status = GGServerServiceStatus.initializing;
		GGServerSpringContext.status = GGServerServiceStatus.initialized;
		
	}

	@Override
	public String getName() {
		return "garganttua-server-spring-context";
	}

	@Override
	public GGServerServiceStatus getStatus() {
		return GGServerSpringContext.status ;
	}

	@Override
	public GGServerServicePriority getPriority() {
		return GGServerServicePriority.system;
	}

	@Override
	public void flush(GGServerServiceCommandRight right) throws GGServerServiceException{
		GGServerSpringContext.status = GGServerServiceStatus.flushing;
		GGServerSpringContext.status = GGServerServiceStatus.flushed;
	}

	@Override
	public void restart(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}
	
}
