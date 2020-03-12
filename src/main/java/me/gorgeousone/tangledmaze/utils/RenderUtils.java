package me.gorgeousone.tangledmaze.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public final class RenderUtils {
	
	private RenderUtils() {}
	
	public static void sendBlocksDelayed(Player player, Collection<Location> blocks, Material mat, JavaPlugin plugin) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				for(Location block : blocks)
					player.sendBlockChange(block, mat.createBlockData());
			}
		}.runTaskLater(plugin, 2);
	}
	
	public static void sendBlockDelayed(Player player, Location block, Material mat, JavaPlugin plugin) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.sendBlockChange(block, mat.createBlockData());
			}
		}.runTaskLater(plugin, 2);
	}
}
