package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.data.Messages;

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
	
	public List<String> getAliases() {
		return aliases;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public int getArgumentCount() {
		return argumentCount;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public boolean isPlayerRequiered() {
		return requieresPlayer;
	}
	
	public boolean isCommand(String name) {
		return (getName().equals(name) || getAliases().contains(name.toLowerCase()));
	}
	
	public boolean execute(CommandSender sender, String[] arguments) {
		
		//TODO localize messages
		if(requieresPlayer && !(sender instanceof Player)) {
			return false;
		}
		
		if(permission != null && !sender.hasPermission(permission)) {
			Messages.ERROR_NO_BUILD_PERMISSION.send(sender);
		}
		
		if(arguments.length < argumentCount) {
			sender.sendMessage(ChatColor.RED + "Usage: " + usage);
			return false;
		}
		
		return true;
	}
	
	private List<String> createAliases(String[] aliases) {
		
		List<String> allAliases = new ArrayList<>();
		
		for(String alias : aliases) {
			allAliases.add(alias.toLowerCase());
		}
		
		return allAliases;
	}
}
