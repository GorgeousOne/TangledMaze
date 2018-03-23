package me.tangledmazes.gorgeousone.main;

import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmazes.main.IMain;
import me.tangledmazes.main.TangledMain;
import me.tangledmazes.spthiel.main.InteractListener;

public class TangledMain_go implements IMain{

	private JavaPlugin plugin;
	private InteractListener interactListener;
	
	@Override
	public void onLoad(TangledMain plugin) {
	}

	@Override
	public void onEnable(TangledMain plugin) {
		plugin = TangledMain.plugin;
		plugin.getServer().getPluginManager().registerEvents(interactListener = new InteractListener(), plugin);
	}

	@Override
	public void onDisable(TangledMain plugin) {
		interactListener.reset();
	}

}
