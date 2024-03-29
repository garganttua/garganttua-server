package com.garganttua.server.modules.console.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.garganttua.server.core.deployment.artefacts.GGServerApplicationPlugin;
import com.garganttua.server.core.deployment.artefacts.GGServerApplicationPluginInfos;
import com.garganttua.server.core.exceptions.GGServerApplicationDeploymentManagerException;
import com.garganttua.server.core.exceptions.GGServerApplicationException;
import com.garganttua.server.core.execution.IGGServerApplicationEngine;
import com.garganttua.server.modules.console.tables.TableUtils;

@ShellComponent
public class PluginsCommand {

	@Autowired
	private IGGServerApplicationEngine engine;
	
	@Autowired
	@Qualifier(value="deployFolder")
	private String deployFolder; 

	@ShellMethod("Get plugins list")
    public String getPluginsList() throws IOException, GGServerApplicationException {
		
		String[] headers = {"Name", "Version", "Status"};
		
		List<GGServerApplicationPlugin> plugins = this.engine.getPlugins();
		String[][] rows = new String[plugins.size()][];
		int i = 0;
		for(GGServerApplicationPlugin plugin: plugins){
			
			GGServerApplicationPluginInfos infos = plugin.getInfos(new File(this.deployFolder));

			rows[i] = new String[] {infos.getPluginTitle(), infos.getPluginVersion(), plugin.getStatus(new File(this.deployFolder), new File(this.deployFolder)).toString()};
					
			i++;		
			
		};
		
		String rend = TableUtils.renderTable(headers, rows);

        return rend;
    }
	
	
	@ShellMethod("Get plugin infos")
    public String getPluginInfos(String arg) throws IOException, GGServerApplicationException {
		
		boolean found = false;
		List<GGServerApplicationPlugin> plugins = this.engine.getPlugins();
		String[][] rows = new String[7][];
		String rend = null;
		
		for(GGServerApplicationPlugin plugin: plugins) {
			GGServerApplicationPluginInfos infos = plugin.getInfos(new File(this.deployFolder));
			
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
	
	@ShellMethod("Undeploy a plugin")
    public String undeploy(String arg) throws IOException, GGServerApplicationException, GGServerApplicationDeploymentManagerException {
		
		boolean found = false;
		List<GGServerApplicationPlugin> plugins = this.engine.getPlugins();
		String[][] rows = new String[7][];
		String rend = null;
		
		for(GGServerApplicationPlugin plugin: plugins) {
			GGServerApplicationPluginInfos infos = plugin.getInfos(new File(this.deployFolder));
			
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
