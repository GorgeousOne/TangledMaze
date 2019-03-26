package me.gorgeousone.tangledmaze.core;

import java.io.File;

import me.gorgeousone.tangledmaze.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.gorgeousone.tangledmaze.command.AddToMaze;
import me.gorgeousone.tangledmaze.command.BuildMaze;
import me.gorgeousone.tangledmaze.command.CutFromMaze;
import me.gorgeousone.tangledmaze.command.DiscardMaze;
import me.gorgeousone.tangledmaze.command.GiveWand;
import me.gorgeousone.tangledmaze.command.HelpCommand;
import me.gorgeousone.tangledmaze.command.Reload;
import me.gorgeousone.tangledmaze.command.SelectTool;
import me.gorgeousone.tangledmaze.command.SetPathWidth;
import me.gorgeousone.tangledmaze.command.SetWallHeight;
import me.gorgeousone.tangledmaze.command.SetWallWidth;
import me.gorgeousone.tangledmaze.command.StartMaze;
import me.gorgeousone.tangledmaze.command.TpToMaze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.data.Settings;
import me.gorgeousone.tangledmaze.handler.MazeCommandHandler;
import me.gorgeousone.tangledmaze.listener.BlockUpdateListener;
import me.gorgeousone.tangledmaze.listener.PlayerListener;
import me.gorgeousone.tangledmaze.listener.ToolActionListener;

public class TangledMain extends JavaPlugin {

	private static TangledMain plugin;
	
	private MazeCommandHandler commandHandler;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		commandHandler = new MazeCommandHandler();
		
		loadConfig();

		Constants.loadConstants();
		Settings.loadSettings(getConfig());
		loadLanguage();
		
		registerListeners();
		registerCommands();
	}
	
	@Override
	public void onDisable() {
		
		Renderer.reload();
		super.onDisable();
	}
	
	public static TangledMain getInstance() {
		return plugin;
	}
	
	public void reloadPlugin() {
		
		reloadConfig();
		Settings.loadSettings(getConfig());
		loadLanguage();
	}
	
	private void registerCommands() {
		
		commandHandler.registerCommand(new Reload());
		commandHandler.registerCommand(new HelpCommand());
		commandHandler.registerCommand(new GiveWand());
		commandHandler.registerCommand(new StartMaze());
		commandHandler.registerCommand(new DiscardMaze());
		commandHandler.registerCommand(new SelectTool());
		commandHandler.registerCommand(new AddToMaze());
		commandHandler.registerCommand(new CutFromMaze());
		commandHandler.registerCommand(new SetPathWidth());
		commandHandler.registerCommand(new SetWallWidth());
		commandHandler.registerCommand(new SetWallHeight());
		commandHandler.registerCommand(new TpToMaze());
		commandHandler.registerCommand(new BuildMaze());
	}
	
	private void loadConfig() {
		
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void loadLanguage() {

		File langFolder =  new File(getDataFolder() + File.separator + "languages");
		File englishFile = new File(langFolder + File.separator + "english.yml");
		YamlConfiguration defEnglish = Utils.getDefaultConfig("english.yml");

		if(!englishFile.exists()) {
			Utils.saveConfig(defEnglish, englishFile);
		}

		YamlConfiguration langConfig;
		File langFile = new File(langFolder + File.separator + Settings.LANGUAGE + ".yml");

		if(langFile.exists()) {

			langConfig = YamlConfiguration.loadConfiguration(langFile);
			langConfig.setDefaults(defEnglish);
			langConfig.options().copyDefaults(true);
			
			Utils.saveConfig(langConfig, langFile);
			getLogger().info("Loaded " + Settings.LANGUAGE + " successfully.");

		}else {
			
			langConfig = defEnglish;
			getLogger().info("Unable to find language file: " + Settings.LANGUAGE + ".yml. Loading default english.");
		}

		Messages.loadMessages(langConfig);
	}

	private void registerListeners() {
		
		PluginManager manager = Bukkit.getPluginManager();
		
		manager.registerEvents(new ToolActionListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new BlockUpdateListener(), this);
		
		getCommand("tangledmaze").setExecutor(commandHandler);
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}
}
