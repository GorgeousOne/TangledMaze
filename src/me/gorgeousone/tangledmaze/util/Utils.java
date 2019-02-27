package me.gorgeousone.tangledmaze.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import me.gorgeousone.tangledmaze.core.TangledMain;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class Utils {

	public static boolean isLikeGround(Material m) {
		return m.isSolid() && !Constants.NOT_SOLIDS.contains(m);
	}
	
	public static boolean canBeOverbuild(Material m) {
		return !m.isSolid() || Constants.REPLACEABLE_SOLIDS.contains(m);
	}

	public static MazePoint nearestSurface(Location loc) {
	
		MazePoint iter = new MazePoint(loc);
		
		if(isLikeGround(iter.getBlock().getType())) {
		
			while(iter.getY() <= 255) {
				iter.add(0, 1, 0);
				
				if(!isLikeGround(iter.getBlock().getType())) {
					iter.add(0, -1, 0);
					return iter;
				}
			}
		
		}else {
			
			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);
				
				if(isLikeGround(iter.getBlock().getType())) {
					return iter;
				}
			}
		}
		
		return new MazePoint(loc);
	}
	
	public static int getMaxHeight(ArrayList<MazePoint> points) {
		
		int min = 0;
		
		for(MazePoint point : points) {
			if(point.getBlockY() > min) {
				min = point.getBlockY();
			}
		}
		
		return min;
	}

	public static YamlConfiguration getDefaultConfig(String fileName) {
		
		InputStream defConfigStream = TangledMain.getPlugin().getResource(fileName);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
	}

	public static void saveConfig(FileConfiguration config, File file) {
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}