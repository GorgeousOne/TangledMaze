package me.gorgeousone.tangledmaze;

import me.gorgeousone.cmdframework.handlers.CommandHandler;
import me.gorgeousone.tangledmaze.commands.*;
import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.listeners.BlockUpdateListener;
import me.gorgeousone.tangledmaze.listeners.PlayerQuitListener;
import me.gorgeousone.tangledmaze.listeners.PlayerWandInteractionListener;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.updatechecks.UpdateCheck;
import me.gorgeousone.tangledmaze.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TangledMain extends JavaPlugin {
	
	private ToolHandler toolHandler;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	
	@Override
	public void onEnable() {
		
		super.onEnable();
		checkForUpdates();
		
		mazeHandler = new MazeHandler(this);
		clipHandler = new ClipToolHandler(this, mazeHandler);
		toolHandler = new ToolHandler(this, clipHandler, mazeHandler);
		buildHandler = new BuildHandler(this, mazeHandler);
		
		loadConfig();
		loadMessages();
		
		Constants.loadMaterialLists(this);
		ConfigSettings.loadSettings(getConfig());
		
		registerListeners();
		registerCommands();
	}
	
	@Override
	public void onDisable() {
		mazeHandler.hideAllClues();
		clipHandler.hideAllClues();
	}
	
	public void reloadPlugin() {
		
		loadMessages();
		reloadConfig();
		ConfigSettings.loadSettings(getConfig());
	}
	
	private void registerListeners() {
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new PlayerWandInteractionListener(toolHandler, clipHandler, mazeHandler), this);
		manager.registerEvents(new PlayerQuitListener(toolHandler, mazeHandler, buildHandler), this);
		manager.registerEvents(new BlockUpdateListener(this, clipHandler, mazeHandler), this);
	}
	
	private void registerCommands() {
		
		MazeCommand mazeCommand = new MazeCommand();
		mazeCommand.addChild(new HelpCommand(mazeCommand));
		mazeCommand.addChild(new Reload(this, mazeCommand));
		mazeCommand.addChild(new GiveWand(mazeCommand));
		mazeCommand.addChild(new StartMaze(mazeCommand, clipHandler, mazeHandler));
		mazeCommand.addChild(new DiscardMaze(mazeCommand, toolHandler, mazeHandler));
		mazeCommand.addChild(new TeleportCommand(mazeCommand, mazeHandler));
		mazeCommand.addChild(new SelectTool(mazeCommand, clipHandler, toolHandler, mazeHandler));
		mazeCommand.addChild(new AddToMaze(mazeCommand, clipHandler, mazeHandler));
		mazeCommand.addChild(new CutFromMaze(mazeCommand, clipHandler, mazeHandler));
		mazeCommand.addChild(new UndoChange(mazeCommand, mazeHandler));
		mazeCommand.addChild(new SetDimension(mazeCommand, mazeHandler));
		mazeCommand.addChild(new BuildMaze(mazeCommand, toolHandler, mazeHandler, buildHandler));
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
		Messages.loadMessages(FileUtils.loadConfig("language", this));
	}
	
	private void checkForUpdates() {

		int resourceId = 59284;
		String websiteURL = "https://www.spigotmc.org/resources/tangled-maze-maze-generator-1-13.59284/";

		UpdateCheck.of(this).resourceId(resourceId).handleResponse((versionResponse, newVersion) -> {

			switch (versionResponse) {

				case FOUND_NEW:
					RawMessage updateMsg = new RawMessage();
					updateMsg.addText("[").color(Color.GREEN).append("TM").color(Color.LIGHT_GREEN).append("] ").color(Color.GREEN);
					updateMsg.lastText().append("Check out the new version of Tangled Maze: ").color(Color.YELLOW);
					updateMsg.addText("v" + newVersion).color(Color.GREEN).underlined(true).onClick(websiteURL, ClickAction.OPEN_URL).hoverText("click to open website");
					
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.isOp())
							updateMsg.sendTo(player);
					}
					
					getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Check out the new version v" + newVersion + " of Tangled Maze: " + websiteURL);
					break;

				case LATEST:
					getLogger().info("You are running the latest version of Tangled Maze :)");
					break;

				case UNAVAILABLE:
					getLogger().info("Unable to check for updates...");
			}
		}).check();
	}
}
