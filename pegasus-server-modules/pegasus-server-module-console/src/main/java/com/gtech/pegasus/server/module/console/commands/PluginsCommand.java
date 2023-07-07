package com.gtech.pegasus.server.module.console.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.garganttua.server.core.deployment.artefacts.PGApplicationPlugin;
import com.garganttua.server.core.deployment.artefacts.PGApplicationPluginInfos;
import com.garganttua.server.core.exceptions.PGApplicationDeploymentManagerException;
import com.garganttua.server.core.exceptions.PGApplicationException;
import com.garganttua.server.core.execution.IPGApplicationEngine;
import com.gtech.pegasus.server.module.console.tables.TableUtils;

import sshd.shell.springboot.autoconfiguration.SshdShellCommand;

@Component
@SshdShellCommand(value = "plugins", description = "Commands for managing plugins")
public class PluginsCommand {

	@Autowired
	private IPGApplicationEngine engine;
	
	@Autowired
	@Qualifier(value="deployFolder")
	private String deployFolder; 
	
	@SshdShellCommand(value = "list", description = "")
    public String getPluginsList(String arg) throws IOException, PGApplicationException {
		
		String[] headers = {"Name", "Version", "Status"};
		
		List<PGApplicationPlugin> plugins = this.engine.getPlugins();
		String[][] rows = new String[plugins.size()][];
		int i = 0;
		for(PGApplicationPlugin plugin: plugins){
			
			PGApplicationPluginInfos infos = plugin.getInfos(new File(this.deployFolder));

			rows[i] = new String[] {infos.getPluginTitle(), infos.getPluginVersion(), plugin.getStatus(new File(this.deployFolder), new File(this.deployFolder)).toString()};
					
			i++;		
			
		};
		
		String rend = TableUtils.renderTable(headers, rows);

        return rend;
    }
	
	
	@SshdShellCommand(value = "infos", description = "Get plugin infos contained in infos.ped file")
    public String getPluginInfos(String arg) throws IOException, PGApplicationException {
		
		boolean found = false;
		List<PGApplicationPlugin> plugins = this.engine.getPlugins();
		String[][] rows = new String[7][];
		String rend = null;
		
		for(PGApplicationPlugin plugin: plugins) {
			PGApplicationPluginInfos infos = plugin.getInfos(new File(this.deployFolder));
			
			if( arg.equals(infos.getPluginTitle()) ) {
				
				
				rows[0] = new String[] {"Descriptor-Version", infos.getDescriptionVersion()};
				rows[1] = new String[] {"Created-By", infos.getCreatedBy()};
				rows[2] = new String[] {"Issuer", infos.getIssuer()};
				rows[3] = new String[] {"Plugin-Title", infos.getPluginTitle()};
				rows[4] = new String[] {"Plugin-Version", infos.getPluginVersion()};
				rows[5] = new String[] {"Required-Plugins", infos.getRequiredPlugins()};
				rows[6] = new String[] {"Required-Libs", infos.getRequiredPlugins()};
				
				
				rend = TableUtils.renderTable(null, rows);
				found = true;
				break;
			}
		}
		
		
		if( !found ) {
			rend = "Plugin "+arg+" not found !";
		}
        return rend;
    }
	
	@SshdShellCommand(value = "undeploy", description = "Undeploy a plugin")
    public String undeploy(String arg) throws IOException, PGApplicationException, PGApplicationDeploymentManagerException {
		
		boolean found = false;
		List<PGApplicationPlugin> plugins = this.engine.getPlugins();
		String[][] rows = new String[7][];
		String rend = null;
		
		for(PGApplicationPlugin plugin: plugins) {
			PGApplicationPluginInfos infos = plugin.getInfos(new File(this.deployFolder));
			
			if( arg.equals(infos.getPluginTitle()) ) {
	
				this.engine.undeploy(plugin);
				
				found = true;
				break;
			}
		}
		
		
		if( !found ) {
			rend = "Plugin "+arg+" not found !";
		}
        return rend;
    }

}
