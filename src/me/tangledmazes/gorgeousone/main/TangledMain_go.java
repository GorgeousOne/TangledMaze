package me.tangledmazes.gorgeousone.main;

import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmazes.main.IMain;
import me.tangledmazes.main.TangledMain;
import me.tangledmazes.spthiel.main.SelecionHandler;

public class TangledMain_go implements IMain{

	@SuppressWarnings("unused")
	private JavaPlugin plugin;
	
	@Override
	public void onLoad(TangledMain plugin) {
	}

	@Override
	public void onEnable(TangledMain plugin) {
		plugin = TangledMain.plugin;
		plugin.getServer().getPluginManager().registerEvents(new SelecionHandler(), plugin);
	}

	@Override
	public void onDisable(TangledMain plugin) {
	}
}