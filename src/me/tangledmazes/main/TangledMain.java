package me.tangledmazes.main;

import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmazes.spthiel.main.InteractListener;

public class TangledMain extends JavaPlugin {

	public static TangledMain plugin;
	
	@Override
	public void onLoad() {
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new InteractListener(), this);
	}
	
	@Override
	public void onDisable() {
	
	}

}
