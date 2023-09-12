package com.garganttua.server.modules.bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.garganttua.server.core.deployment.artefacts.GGServerApplicationConfiguration;
import com.garganttua.server.core.deployment.artefacts.GGServerApplicationManifest;
import com.garganttua.server.core.exceptions.GGServerApplicationDeploymentManagerException;
import com.garganttua.server.core.exceptions.GGServerApplicationEngineException;
import com.garganttua.server.core.exceptions.GGServerApplicationException;
import com.garganttua.server.core.execution.GGServerApplicationEngine;
import com.garganttua.server.core.execution.GGServerAssetManifest;
import com.garganttua.server.core.services.GGServerServiceCommandRight;
import com.garganttua.server.core.services.GGServerServiceException;
import com.garganttua.server.core.update.GGServerApplicationDeploymentManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGServer {

	private static Options options;
	public static GGServerApplicationEngine engine;
	
	public static GGServerFolders folders = new GGServerFolders();
	private static GGServerApplicationDeploymentManager deploy;
	
	public static GGServerAssetManifest assetManifest;
	private static String bannerFile;
	private static String launcherClass;
	
	public static void main(String[] args) throws GGServerException, GGServerException, GGServerApplicationEngineException, IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalStateException,IllegalAccessException, GGServerApplicationDeploymentManagerException, CloneNotSupportedException, InterruptedException, GGServerServiceException, GGServerApplicationException {
	
		String currentDirectory = System.getProperty("user.dir");
		log.info("Working directory " + currentDirectory);
		
		/*
		 * PARSING COMMAND LINE ARGUMENTS
		 */
		
		CommandLine cmd = GGServer.parseParams(args);
		
		if( cmd.hasOption("help") ) {
			GGServer.printHelp();
			return;
		}
		
		if( !cmd.hasOption("manifest") && !cmd.hasOption("g") ) {
			GGServer.printHelp();
			return;
		}
		
		if( cmd.hasOption("tmp") ) {
			GGServerFolders.tmp = cmd.getOptionValue("tmp");
		}
		
		if( cmd.hasOption("launcherClass") ) {
			GGServer.launcherClass = cmd.getOptionValue("launcherClass");
		}
		
		if( cmd.hasOption("bin") ) {
			GGServerFolders.bin = cmd.getOptionValue("bin");
		}
		
		if( cmd.hasOption("conf") ) {
			GGServerFolders.conf = cmd.getOptionValue("conf");
		}
		
		if( cmd.hasOption("lib") ) {
			GGServerFolders.lib = cmd.getOptionValue("lib");
		}
		
		if( cmd.hasOption("deploy") ) {
			GGServerFolders.deploy = cmd.getOptionValue("deploy");
		}
		
		if( cmd.hasOption("logs") ) {
			GGServerFolders.logs = cmd.getOptionValue("logs");
		}
		
		if( cmd.hasOption("logs") ) {
			GGServerFolders.logs = cmd.getOptionValue("logs");
		}
		
		if( cmd.hasOption("banner") ) {
			GGServer.bannerFile = cmd.getOptionValue("banner");
		}
		
		if( cmd.hasOption("confExt") ) {
			String[] confExtensions = cmd.getOptionValue("confExt").split(",");
			GGServerApplicationConfiguration.configurationFileExtensions = confExtensions;
		} else {
			String[] ext = {"properties"};
			GGServerApplicationConfiguration.configurationFileExtensions = ext;
		}
		
		/*
		 * PRINT BANNER 
		 */
		try {
			String content = Files.readString(Paths.get(GGServerFolders.conf+File.separator+GGServer.bannerFile), StandardCharsets.US_ASCII);
			log.info("\n"+content);
		} catch (Exception e) {
			
		}
		
		log.info("Creating Java execution context");
		
		/*
		 * JAVA EXECUTION CONTEXT MANAGEMENT
		 */
		GGServer.deploy = GGServerApplicationDeploymentManager.init(GGServerFolders.deploy, GGServerFolders.deploy);
		GGServer.engine = GGServerApplicationEngine.init(GGServer.deploy, GGServerFolders.tmp);

		/*
		 * READ APPLICATION MANIFEST 
		 */
		String manifestFile = cmd.getOptionValue("manifest");
		
		if( cmd.hasOption("g") ) {
			if( manifestFile != null && !manifestFile.isEmpty() ) {
				File f = new File(manifestFile);
				if( f.exists() ) {
					f.delete();
				}
			}
			GGServer.engine.readFolder(GGServerFolders.conf, true, false);
			GGServer.engine.readFolder(GGServerFolders.lib, true, false);
			GGServer.engine.setLauncherClass(GGServer.launcherClass);
			GGServer.engine.generateManifest(manifestFile);
			return;
		}

		log.info("Getting manifest: "+manifestFile);
		GGServerApplicationManifest applicationManifest = new GGServerApplicationManifest(manifestFile);

		GGServer.engine.addManifest(applicationManifest, true);
		GGServer.engine.readFolder(GGServerFolders.deploy, false, true);
		
		/*
		 * STARTING JAVA CONTEXT
		 */
		log.info("Starting Garganttua Server Engine");
		
		GGServer.engine.setArguments(List.of(args));
		GGServer.engine.start(GGServerServiceCommandRight.system);

		log.info("Garganttua Server Engine Started");

	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("garganttua-server", GGServer.options);
	}

	private static CommandLine parseParams(String[] args) throws GGServerException {

		GGServer.options = new Options();
		GGServer.options.addOption("help", false, "Print this message");
		GGServer.options.addOption("manifest", true, "Path to the manifest file");
		GGServer.options.addOption("static", false, "Start server as static, not based on manifest but based on content of server folders. Static mode is not compliant with auto update feature");
		GGServer.options.addOption("tmp", true, "Path to the temporary folder");
		GGServer.options.addOption("bin", true, "");
		GGServer.options.addOption("conf", true, "");
		GGServer.options.addOption("lib", true, "");
		GGServer.options.addOption("deploy", true, "");
		GGServer.options.addOption("logs", true, "");
		GGServer.options.addOption("banner", true, "");
		GGServer.options.addOption("confExt", true, "");
		GGServer.options.addOption("launcherClass", true, "");
		GGServer.options.addOption("g", false, "Generate in the temporary file a mnifest file based on files placed in the server directories (/bin, /conf, ...)");
		
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse(GGServer.options, args);

			return cmd;
		} catch (org.apache.commons.cli.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String applicationName;

	static {
		GGServer.applicationName = "Garganttua Server";
	}
	
}
