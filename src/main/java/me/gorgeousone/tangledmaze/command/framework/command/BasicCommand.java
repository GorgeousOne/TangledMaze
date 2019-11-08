package me.gorgeousone.tangledmaze.command.framework.command;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

/**
 * This is the beginning of a lot of unnecessary code.
 * I mean it is kind of useful because I can create child commands, aliases, number inputs and tab lists on the go but
 * yeah it feels somehow unnecessary that this "api" is like at least 10 pages extra code.
 * But it's more soft coded so it's also cool in a way.
 */
public abstract class BasicCommand {
	
	private String name;
	private String permission;

	private Set<String> aliases;
	private ParentCommand parent;
	
	protected BasicCommand(String name, String permission) {
		this(name, permission, null);
	}

	protected BasicCommand(String name, String permission, ParentCommand parent) {
		
		this.name = name;
		this.permission = permission;
		this.parent = parent;
		
		aliases = new HashSet<>();
		aliases.add(name);
	}

	public String getName() {
		return name;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean matches(String alias) {
		return aliases.contains(alias);
	}

	protected void addAlias(String alias) {
		aliases.add(alias);
	}
	
	public boolean isChild() {
		return parent != null;
	}
	
	public ParentCommand getParent() {
		return parent;
	}

	protected abstract boolean onExecute(CommandSender sender, String[] arguments);

	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players can execute this command.");
			return false;
		}
		
		if(permission != null && !sender.hasPermission(getPermission())) {
			sender.sendMessage(ChatColor.RED + "You do not have the permission for this command.");
			return false;
		}

		return onExecute(sender, arguments);
	}
	
	public List<String> getTabList(String[] arguments) {
		return new LinkedList<>();
	}

	public String getUsage() {
		
		if(isChild())
			return getParent().getParentUsage() + " " + getName();
		else
			return "/" + getName();
	}

	public void sendUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
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
}