package me.tangledmazes.main;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmazes.spthiel.main.InteractListener;
import me.tangledmazes.spthiel.main.TangledMain_sp;

public class TangledMain extends JavaPlugin {

	public static ArrayList<IMain> mains = new ArrayList<>();
	public static TangledMain plugin;
	
	public static void addMain(IMain... main) {
		mains.addAll(Arrays.asList(main));
	}
	
	@Override
	public void onLoad() {
		plugin = this;
		addMain(new TangledMain_sp());
		
		mains.forEach(main -> main.onLoad(this));
	}
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new InteractListener(), this);
		mains.forEach(main -> main.onEnable(this));
		
		this.getServer().getPluginManager().registerEvents(new InteractListener(), this);
	}
	
	@Override
	public void onDisable() {
		mains.forEach(main -> main.onDisable(this));
	
	}

}
