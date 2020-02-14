package me.gorgeousone.tangledmaze;

import me.gorgeousone.tangledmaze.commands.AddToMaze;
import me.gorgeousone.tangledmaze.commands.BuildCommand;
import me.gorgeousone.tangledmaze.commands.CutFromMaze;
import me.gorgeousone.tangledmaze.commands.DiscardMaze;
import me.gorgeousone.tangledmaze.commands.GiveWand;
import me.gorgeousone.tangledmaze.commands.HelpCommand;
import me.gorgeousone.tangledmaze.commands.MazeCommand;
import me.gorgeousone.tangledmaze.commands.Reload;
import me.gorgeousone.tangledmaze.commands.SelectTool;
import me.gorgeousone.tangledmaze.commands.SetDimension;
import me.gorgeousone.tangledmaze.commands.StartMaze;
import me.gorgeousone.tangledmaze.commands.TeleportCommand;
import me.gorgeousone.tangledmaze.commands.UnbuildMaze;
import me.gorgeousone.tangledmaze.commands.UndoCommand;
import me.gorgeousone.cmdframework.handlers.CommandHandler;
import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.listeners.BlockUpdateListener;
import me.gorgeousone.tangledmaze.listeners.PlayerWandInteractionListener;
import me.gorgeousone.tangledmaze.listeners.PlayerQuitListener;
import me.gorgeousone.tangledmaze.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TangledMain extends JavaPlugin {

	//TODO make cliphandler instance private
	public static ClipToolHandler clipHandler;
	private static TangledMain plugin;
	private ToolHandler toolHandler;
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	private Renderer renderer;

	public static TangledMain getInstance() {
		return plugin;
	}

	@Override
	public void onEnable() {

		super.onEnable();
		plugin = this;

		renderer = new Renderer();
		buildHandler = new BuildHandler(renderer);
		
		mazeHandler = new MazeHandler(buildHandler, renderer);
		clipHandler = new ClipToolHandler(renderer);
		toolHandler = new ToolHandler(clipHandler, mazeHandler, renderer);

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
		renderer.hideAllClues();
		super.onDisable();
	}

	public void reloadPlugin() {

		loadLanguage();
		reloadConfig();
		ConfigSettings.loadSettings(getConfig());
	}

	private void registerListeners() {

		PluginManager manager = Bukkit.getPluginManager();
		//TODO pass clipcklistener proper instance when created
		manager.registerEvents(new PlayerWandInteractionListener(toolHandler, clipHandler, mazeHandler, renderer), this);
		manager.registerEvents(new PlayerQuitListener(toolHandler, mazeHandler), this);
		manager.registerEvents(new BlockUpdateListener(clipHandler, mazeHandler, renderer), this);
	}

	private void registerCommands() {

		MazeCommand mazeCommand = new MazeCommand();
		mazeCommand.addChild(new HelpCommand(mazeCommand));
		mazeCommand.addChild(new Reload(mazeCommand));
		mazeCommand.addChild(new GiveWand(mazeCommand));
		mazeCommand.addChild(new StartMaze(mazeCommand, clipHandler, mazeHandler));
		mazeCommand.addChild(new DiscardMaze(mazeCommand, toolHandler, mazeHandler));
		mazeCommand.addChild(new TeleportCommand(mazeCommand, mazeHandler, renderer));
		mazeCommand.addChild(new SelectTool(mazeCommand, clipHandler, toolHandler, mazeHandler, renderer));
		mazeCommand.addChild(new AddToMaze(mazeCommand, clipHandler, mazeHandler));
		mazeCommand.addChild(new CutFromMaze(mazeCommand, clipHandler, mazeHandler));
		mazeCommand.addChild(new UndoCommand(mazeCommand, mazeHandler));
		mazeCommand.addChild(new SetDimension(mazeCommand, mazeHandler));
		mazeCommand.addChild(new BuildCommand(mazeCommand, toolHandler, mazeHandler, buildHandler));
		mazeCommand.addChild(new UnbuildMaze(mazeCommand, mazeHandler, buildHandler));

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
