package me.gorgeousone.tangledmaze.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Utils {
	
	private Utils() {}
	
	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static YamlConfiguration loadDefaultConfig(String configName, JavaPlugin plugin) {
		
		InputStream defConfigStream = plugin.getResource(configName + ".yml");
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
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
}