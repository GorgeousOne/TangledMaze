package me.gorgeousone.tangledmaze;

import me.gorgeousone.tangledmaze.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.gorgeousone.tangledmaze.command.*;
import me.gorgeousone.tangledmaze.command.framework.handler.CommandHandler;
import me.gorgeousone.tangledmaze.data.*;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.listener.*;

import java.util.HashMap;
import java.util.Map;

public class TangledMain extends JavaPlugin {

	private static TangledMain plugin;

	@Override
	public void onEnable() {

		super.onEnable();
		plugin = this;
		
		loadConfig();
		loadLanguage();
		
		Constants.loadConstants();
		ConfigSettings.loadSettings(getConfig());
		
		registerListeners();
		registerCommands();

		Map<String, String> map = new HashMap<>();
		map.put("loc", "1234");
		map.put("height", "255");
		getConfig().set("test-map", map);
		saveConfig();
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

		loadLanguage();
		reloadConfig();
		ConfigSettings.loadSettings(getConfig());
	}
	
	private void registerListeners() {
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new WandListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new BlockUpdateListener(), this);
	}

	private void registerCommands() {
		
		MazeCommand mazeCommand = new MazeCommand();
		mazeCommand.addChild(new HelpCommand(mazeCommand));
		mazeCommand.addChild(new Reload(mazeCommand));
		mazeCommand.addChild(new GiveWand(mazeCommand));
		mazeCommand.addChild(new StartMaze(mazeCommand));
		mazeCommand.addChild(new DiscardMaze(mazeCommand));
		mazeCommand.addChild(new TpToMaze(mazeCommand));
		mazeCommand.addChild(new SelectTool(mazeCommand));
		mazeCommand.addChild(new AddToMaze(mazeCommand));
		mazeCommand.addChild(new CutFromMaze(mazeCommand));
		mazeCommand.addChild(new UndoCommand(mazeCommand));
		mazeCommand.addChild(new SetDimension(mazeCommand));
		mazeCommand.addChild(new BuildCommand(mazeCommand));
		mazeCommand.addChild(new UnbuildMaze(mazeCommand));

		CommandHandler cmdHandler = new CommandHandler(this);
		cmdHandler.registerCommand(mazeCommand);
	}
	
	private void loadConfig() {

		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void loadLanguage() {
		Messages.loadMessages(Utils.loadConfig("language"));
	}
}
