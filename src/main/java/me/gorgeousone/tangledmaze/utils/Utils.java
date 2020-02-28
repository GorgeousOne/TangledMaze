package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.clip.Clip;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Map;

public final class Utils {
	
	private Utils() {}
	
	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static YamlConfiguration loadConfig(String configName, JavaPlugin plugin) {
		
		File configFile = new File(plugin.getDataFolder() + File.separator + configName + ".yml");
		YamlConfiguration defConfig = loadDefaultConfig(configName, plugin);
		
		if (!configFile.exists()) {
			try {
				defConfig.save(configFile);
			} catch (IOException ignored) {
			}
		}
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		config.setDefaults(defConfig);
		config.options().copyDefaults(true);
		
		return config;
	}
	
	public static YamlConfiguration loadDefaultConfig(String configName, JavaPlugin plugin) {
		
		InputStream defConfigStream = plugin.getResource(configName + ".yml");
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
	}
	
	public static Map.Entry<Vec2, Vec2> calculateClipBounds(Clip clip) {
		
		Vec2 min = null;
		Vec2 max = null;
		
		for (Vec2 point : clip.getFill()) {
			
			if (min == null) {
				min = point.clone();
				max = point.clone();
				continue;
			}
			
			int x = point.getX();
			int z = point.getZ();
			
			if (x < min.getX())
				min.setX(x);
			else if (x > max.getX())
				max.setX(x);
			
			if (z < min.getZ())
				min.setZ(point.getZ());
			else if (z > max.getZ())
				max.setZ(z);
		}
		
		return new AbstractMap.SimpleEntry<>(min, max);
	}
}