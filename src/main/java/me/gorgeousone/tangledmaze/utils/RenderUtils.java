package me.gorgeousone.tangledmaze.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;

public final class RenderUtils {
	
	private RenderUtils() {}
	
	public static void sendBlockDelayed(Player player, Location point, Material mat, JavaPlugin plugin) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.sendBlockChange(point, mat.createBlockData());
			}
		}.runTaskLater(plugin, 2);
	}
	
	public static void sendBlocks(Player player, Map<Material, Collection<Location>> points) {
		
		for (Material material : points.keySet()) {
			BlockData matData = material.createBlockData();
			
			for (Location point : points.get(material))
				player.sendBlockChange(point, matData);
		}
	}
	
	public static void sendBlocksDelayed(Player player, Map<Material, Collection<Location>> points, JavaPlugin plugin) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				for (Material material : points.keySet()) {
					BlockData matData = material.createBlockData();
					
					for (Location point : points.get(material))
						player.sendBlockChange(point, matData);
				}
			}
		}.runTaskLater(plugin, 2);
	}
}
