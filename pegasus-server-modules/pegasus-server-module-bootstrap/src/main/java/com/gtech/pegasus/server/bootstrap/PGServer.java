package com.gtech.pegasus.server.bootstrap;

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

import com.gtech.pegasus.core.deployment.artefacts.PGApplicationConfiguration;
import com.gtech.pegasus.core.deployment.artefacts.PGApplicationManifest;
import com.gtech.pegasus.core.exceptions.PGApplicationDeploymentManagerException;
import com.gtech.pegasus.core.exceptions.PGApplicationEngineException;
import com.gtech.pegasus.core.exceptions.PGApplicationException;
import com.gtech.pegasus.core.execution.PGApplicationEngine;
import com.gtech.pegasus.core.execution.PGAssetManifest;
import com.gtech.pegasus.core.services.PGServiceCommandRight;
import com.gtech.pegasus.core.services.PGServiceException;
import com.gtech.pegasus.core.update.PGApplicationDeploymentManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PGServer {

	private static Options options;
	public static PGApplicationEngine engine;
	
	public static PGServerFolders folders = new PGServerFolders();
	private static PGApplicationManifest manifest;
	
	private static PGApplicationDeploymentManager deploy;
	
	public static PGAssetManifest assetManifest;
	private static String bannerFile;
	private static String launcherClass;
	
	public static void main(String[] args) throws PGServerException, PGApplicationException, PGApplicationEngineException, IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, PGApplicationDeploymentManagerException, CloneNotSupportedException, InterruptedException, PGServiceException {
	
		/*
		 * PARSING COMMAND LINE ARGUMENTS
		 */
		
		CommandLine cmd = PGServer.parseParams(args);
		
		if( cmd.hasOption("help") ) {
			PGServer.printHelp();
			return;
		}
		
		if( !cmd.hasOption("manifest") && !cmd.hasOption("g") ) {
			PGServer.printHelp();
			return;
		}
		
		if( cmd.hasOption("tmp") ) {
			PGServer.folders.tmp = cmd.getOptionValue("tmp");
		}
		
		if( cmd.hasOption("launcherClass") ) {
			PGServer.launcherClass = cmd.getOptionValue("launcherClass");
		}
		
		if( cmd.hasOption("bin") ) {
			PGServer.folders.bin = cmd.getOptionValue("bin");
		}
		
		if( cmd.hasOption("conf") ) {
			PGServer.folders.conf = cmd.getOptionValue("conf");
		}
		
		if( cmd.hasOption("lib") ) {
			PGServer.folders.lib = cmd.getOptionValue("lib");
		}
		
		if( cmd.hasOption("deploy") ) {
			PGServer.folders.deploy = cmd.getOptionValue("deploy");
		}
		
		if( cmd.hasOption("logs") ) {
			PGServer.folders.logs = cmd.getOptionValue("logs");
		}
		
		if( cmd.hasOption("logs") ) {
			PGServer.folders.logs = cmd.getOptionValue("logs");
		}
		
		if( cmd.hasOption("banner") ) {
			PGServer.bannerFile = cmd.getOptionValue("banner");
		}
		
		if( cmd.hasOption("confExt") ) {
			String[] confExtensions = cmd.getOptionValue("confExt").split(",");
			PGApplicationConfiguration.configurationFileExtensions = confExtensions;
		} else {
			String[] ext = {"properties"};
			PGApplicationConfiguration.configurationFileExtensions = ext;
		}
		
		/*
		 * PRINT BANNER 
		 */
		try {
			String content = Files.readString(Paths.get(PGServer.folders.conf+File.separator+PGServer.bannerFile), StandardCharsets.US_ASCII);
			log.info("\n"+content);
		} catch (Exception e) {
			
		}

		log.info("Creating Java execution context");
		
		/*
		 * JAVA EXECUTION CONTEXT MANAGEMENT
		 */
		PGServer.deploy = PGApplicationDeploymentManager.init(PGServer.folders.deploy, PGServer.folders.deploy);
		PGServer.engine = PGApplicationEngine.init(PGServer.deploy, PGServer.folders.tmp);

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
			PGServer.engine.readFolder(PGServer.folders.conf, true, false);
			PGServer.engine.readFolder(PGServer.folders.lib, true, false);
			PGServer.engine.setLauncherClass(PGServer.launcherClass);
			PGServer.engine.generateManifest(manifestFile);
			return;
		}

		log.info("Getting manifest: "+manifestFile);
		PGApplicationManifest applicationManifest = new PGApplicationManifest(manifestFile);

		PGServer.engine.addManifest(applicationManifest, true);
		PGServer.engine.readFolder(PGServer.folders.deploy, false, true);
		
		/*
		 * STARTING JAVA CONTEXT
		 */
		log.info("Starting Pegasus Engine");
		
		PGServer.engine.setArguments(List.of(args));
		PGServer.engine.start(PGServiceCommandRight.system);

		log.info("Pegasus Engine Started");

	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("pegasus-server", PGServer.options);
	}

	private static CommandLine parseParams(String[] args) throws PGServerException {

		PGServer.options = new Options();
		PGServer.options.addOption("help", false, "Print this message");
		PGServer.options.addOption("manifest", true, "Path to the manifest file");
		PGServer.options.addOption("static", false, "Start server as static, not based on manifest but based on content of server folders. Static mode is not compliant with auto update feature");
		PGServer.options.addOption("tmp", true, "Path to the temporary folder");
		PGServer.options.addOption("bin", true, "");
		PGServer.options.addOption("conf", true, "");
		PGServer.options.addOption("lib", true, "");
		PGServer.options.addOption("deploy", true, "");
		PGServer.options.addOption("logs", true, "");
		PGServer.options.addOption("banner", true, "");
		PGServer.options.addOption("confExt", true, "");
		PGServer.options.addOption("launcherClass", true, "");
		PGServer.options.addOption("g", false, "Generate in the temporary file a mnifest file based on files placed in the server directories (/bin, /conf, ...)");
		
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse(PGServer.options, args);

			return cmd;
		} catch (org.apache.commons.cli.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String applicationName;

	static {
		PGServer.applicationName = "Pegasus Server";
	}
	
}
