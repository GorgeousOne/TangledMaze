package me.gorgeousone.tangledmaze.core;

import java.io.File;

import me.gorgeousone.tangledmaze.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.gorgeousone.tangledmaze.command.*;
import me.gorgeousone.tangledmaze.data.*;
import me.gorgeousone.tangledmaze.handler.CommandHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.listener.*;

public class TangledMain extends JavaPlugin {

	private static TangledMain plugin;
	
	private CommandHandler commandHandler;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		
		loadConfig();
		loadLanguage();

		Constants.loadConstants();
		Settings.loadSettings(getConfig());
		
		registerListeners();
		registerCommands();

		getCommand("tangledmaze").setExecutor(commandHandler);
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter(commandHandler.getCommands()));
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
		
		commandHandler = new CommandHandler();

		commandHandler.registerCommand(new HelpCommand());
		commandHandler.registerCommand(new Reload());
		commandHandler.registerCommand(new GiveWand());
		commandHandler.registerCommand(new StartMaze());
		commandHandler.registerCommand(new DiscardMaze());
		commandHandler.registerCommand(new SelectTool());
		commandHandler.registerCommand(new AddToMaze());
		commandHandler.registerCommand(new CutFromMaze());
		commandHandler.registerCommand(new SetWallWidth());
		commandHandler.registerCommand(new SetWallHeight());
		commandHandler.registerCommand(new SetPathWidth());
		commandHandler.registerCommand(new SetPathLength());
		commandHandler.registerCommand(new TpToMaze());
		commandHandler.registerCommand(new BuildCommand());
		commandHandler.registerCommand(new UnbuildMaze());
		commandHandler.registerCommand(new Undo());
	}
	
	private void loadConfig() {
		
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void loadLanguage() {

		File langFile = new File(getDataFolder() + File.separator + "language.yml");
		YamlConfiguration defLangConfig = Utils.getDefaultConfig("language.yml");

		if(!langFile.exists())
			Utils.saveConfig(defLangConfig, langFile);

		YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
		
		langConfig.setDefaults(defLangConfig);
		langConfig.options().copyDefaults(true);
		
		Utils.saveConfig(langConfig, langFile);
		Messages.loadMessages(langConfig);
	}

	private void registerListeners() {
		
		PluginManager manager = Bukkit.getPluginManager();
		
		manager.registerEvents(new WandListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new BlockUpdateListener(), this);
	}
}
