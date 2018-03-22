package me.tangledmazes.spthiel.main;

import me.tangledmazes.main.TangledMain;
import me.tangledmazes.main.commandsAPI.CommandParser;

public class TangledMain_sp {

	public void onEnable() {
		
		TangledMain.plugin.getCommand("help").setExecutor(new CommandParser());
	}
	
	public void onDisable() {
		
	}
	
}
