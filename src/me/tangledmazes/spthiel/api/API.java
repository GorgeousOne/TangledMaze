package me.tangledmazes.spthiel.api;


import org.bukkit.plugin.java.JavaPlugin;


public class API extends JavaPlugin{
	
	public static API api;
	
	public void onEnable(){
		
		api = this;
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		
	}
	
}
