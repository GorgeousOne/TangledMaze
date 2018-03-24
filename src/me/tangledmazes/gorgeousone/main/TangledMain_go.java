package me.tangledmazes.gorgeousone.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmazes.main.IMain;
import me.tangledmazes.main.TangledMain;
import me.tangledmazes.spthiel.main.SelectionHandler;

public class TangledMain_go implements IMain{

	@SuppressWarnings("unused")
	private JavaPlugin plugin;
	private SelectionHandler handler;
	
	@Override
	public void onLoad(TangledMain plugin) {
	}

	@Override
	public void onEnable(TangledMain plugin) {
		plugin = TangledMain.plugin;
		plugin.getServer().getPluginManager().registerEvents(handler = new SelectionHandler(), plugin);
	}

	@Override
	public void onDisable(TangledMain plugin) {
		handler.reload();
	}
	
	public SelectionHandler getSelectionHandler() {
		return handler;
	}
	
	//TODO put this in a utils class?
	public static Location getNearestSurface(Location loc) {
		Location iter = loc.clone();
		
		if(loc.getBlock().getType() == Material.AIR) {
			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);
				
				if(iter.getBlock().getType() != Material.AIR)
					return iter;
			}
			iter.setY(loc.getY());
		}
		
		while(iter.getY() <= 255) {
			iter.add(0, 1, 0);
			
			if(iter.getBlock().getType() == Material.AIR) {
				iter.add(0, -1, 0);
				return iter;
			}
		}
		return null;
	}
}