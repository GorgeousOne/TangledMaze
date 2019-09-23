package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public abstract class MazeCommand {
	
	private String name;
	private List<String> aliases;
	private String permission;
	private String usage;

	private int argumentCount;
	private boolean requieresPlayer;
	
	public MazeCommand(String name, String usage, int argumentCount, boolean requieresPlayer, String permission, String... aliases) {
		
		this.name = name.toLowerCase();
		this.aliases = createAliases(aliases);
		this.permission = permission;
		this.usage = usage;
		this.argumentCount = argumentCount;
		this.requieresPlayer = requieresPlayer;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUsage() {
		return usage;
	}

	public int getArgumentCount() {
		return argumentCount;
	}

	public boolean isPlayerRequiered() {
		return requieresPlayer;
	}

	public String getExtraPermission() {
		return permission;
	}

	public List<String> getAliases() {
		return aliases;
	}
	
	public boolean isCommand(String name) {
		return (getName().equals(name) || getAliases().contains(name.toLowerCase()));
	}
	
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(requieresPlayer && !(sender instanceof Player))
			return false;
		
		if(permission != null && !sender.hasPermission(permission)) {
			Messages.ERROR_NO_BUILD_PERMISSION.sendTo(sender);
			return false;
		}
		
		if(arguments.length < argumentCount) {
			sender.sendMessage(ChatColor.RED + "Usage: " + usage);
			return false;
		}
		
		return true;
	}
	
	protected Maze getStartedMaze(Player player, boolean withExits, boolean notConstructed) {
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isStarted()) {
			
			Messages.ERROR_MAZE_NOT_STARTED.sendTo(player);
			player.sendMessage("/tangledmaze start");
			return null;
		}
		
		if(withExits && !maze.hasExits()) {
			
			Messages.ERROR_NO_MAZE_EXIT_SET.sendTo(player);
			player.sendMessage("/tangledmaze select exit");
			return null;
		}
		
		if(notConstructed && maze.isConstructed()) {
			
			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
			return null;
		}
		
		return maze;
	}
	
	protected ClippingTool getCompletedClipboard(Player player) {
		
		if(!ToolHandler.hasClipboard(player) || !ToolHandler.getClipboard(player).isStarted()) {
			
			Messages.ERROR_CLIPBOARD_NOT_STARTED.sendTo(player);
			player.sendMessage("/tangledmaze wand");
			return null;
		}

		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			Messages.ERROR_CLIPBOARD_NOT_COMPLETED.sendTo(player);
			return null;
		}
		
		return clipboard;
	}
	
	protected List<String> createAliases(String[] aliases) {
		
		List<String> allAliases = new ArrayList<>();
		
		for(String alias : aliases) {
			allAliases.add(alias.toLowerCase());
		}
		
		return allAliases;
	}
}