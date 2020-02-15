package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Utils {
	
	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static YamlConfiguration loadDefaultConfig(String configName) {
		InputStream defConfigStream = TangledMain.getInstance().getResource(configName + ".yml");
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
	}
	
	public static YamlConfiguration loadConfig(String configName) {
		
		File configFile = new File(TangledMain.getInstance().getDataFolder() + File.separator + configName + ".yml");
		YamlConfiguration defConfig = loadDefaultConfig(configName);
		
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