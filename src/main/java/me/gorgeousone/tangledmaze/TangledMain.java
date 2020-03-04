package me.gorgeousone.tangledmaze;

import me.gorgeousone.cmdframework.handlers.CommandHandler;
import me.gorgeousone.tangledmaze.commands.*;
import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.listeners.BlockUpdateListener;
import me.gorgeousone.tangledmaze.listeners.PlayerQuitListener;
import me.gorgeousone.tangledmaze.listeners.PlayerWandInteractionListener;
import me.gorgeousone.tangledmaze.updatechecks.UpdateCheck;
import me.gorgeousone.tangledmaze.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TangledMain extends JavaPlugin {
	
	private ToolHandler toolHandler;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	private Renderer renderer;
	
	@Override
	public void onDisable() {
		renderer.hideAllClues();
		super.onDisable();
	}
	
	@Override
	public void onEnable() {
		
		super.onEnable();
		
		checkForUpdates();
		
		renderer = new Renderer(this);
		buildHandler = new BuildHandler(this, renderer);
		
		mazeHandler = new MazeHandler(buildHandler, renderer);
		clipHandler = new ClipToolHandler(renderer);
		toolHandler = new ToolHandler(clipHandler, mazeHandler, renderer);
		
		renderer.setMazeHandler(mazeHandler);
		
		loadConfig();
		loadMessages();
		
		Constants.loadMaterialLists(this);
		ConfigSettings.loadSettings(getConfig());
		
		registerListeners();
		registerCommands();
	}
	
	public void reloadPlugin() {
		
		loadMessages();
		reloadConfig();
		ConfigSettings.loadSettings(getConfig());
	}
	
	private void registerListeners() {
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new PlayerWandInteractionListener(toolHandler, clipHandler, mazeHandler, renderer), this);
		manager.registerEvents(new PlayerQuitListener(toolHandler, mazeHandler), this);
		manager.registerEvents(new BlockUpdateListener(this, clipHandler, mazeHandler, renderer), this);
	}
	
	private void registerCommands() {
		
		MazeCommand mazeCommand = new MazeCommand();
		mazeCommand.addChild(new HelpCommand(mazeCommand));
		mazeCommand.addChild(new Reload(this, mazeCommand));
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
	
	private void loadMessages() {
		Messages.loadMessages(ConfigUtils.loadConfig("language", this));
	}
	
	private void checkForUpdates() {
		
		int resourceId = 59284;
		
		UpdateCheck.of(this).resourceId(resourceId).handleResponse((versionResponse, version) -> {
			
			switch (versionResponse) {
				
				case FOUND_NEW:
					Bukkit.broadcastMessage(Constants.prefix + "Check out the new version of TangledMaze: " + ChatColor.DARK_GREEN + version + ChatColor.YELLOW + "!");
					break;
				
				case LATEST:
					getLogger().info("You are running the latest version of TangledMaze :)");
					break;
				
				case UNAVAILABLE:
					getLogger().info("Unable to check for updates...");
			}
		}).
				
				check();
	}
}
