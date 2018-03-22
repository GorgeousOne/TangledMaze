package me.tangledmazes.spthiel.main;

import me.tangledmazes.main.IMain;
import me.tangledmazes.main.TangledMain;
import me.tangledmazes.spthiel.commands.CommandListener;

public class TangledMain_sp implements IMain{

	public void onEnable(TangledMain plugin) {
		plugin.getCommand("tangledmaze").setExecutor(new CommandListener());
		
	}
	
	public void onDisable(TangledMain plugin) {
		
	}
	
	public void onLoad(TangledMain plugin) {
	
	}
	
}
