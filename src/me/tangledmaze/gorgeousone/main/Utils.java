package me.tangledmaze.gorgeousone.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.main.TangledMain;

public class Utils {

	public static Location getNearestSurface(Location loc) {
		Location iter = loc.clone();
		
		if(loc.getBlock().getType() == Material.AIR) {
			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);
				
				if(iter.getBlock().getType() != Material.AIR)
					return iter;
			}
			iter.setY(loc.getY());
		
		}else {
			while(iter.getY() <= 255) {
				iter.add(0, 1, 0);
				
				if(iter.getBlock().getType() == Material.AIR) {
					iter.add(0, -1, 0);
					return iter;
				}
			}
		}
		return loc;
	}
	
	public static void sendBlockLater(Player p, Location loc, Material m) {
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				p.sendBlockChange(loc, m, (byte) 0);
			}
		}.runTask(TangledMain.plugin);
	}
}
