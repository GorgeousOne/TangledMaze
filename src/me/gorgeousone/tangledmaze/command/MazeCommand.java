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
	private String extraPermission;
	private String usage;

	private int argumentCount;
	private boolean requieresPlayer;
	
	public MazeCommand(String name, String usage, int argumentCount, boolean requieresPlayer, String extraPermission, String... aliases) {
		
		this.name = name.toLowerCase();
		this.aliases = createAliases(aliases);
		this.extraPermission = extraPermission;
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
		return extraPermission;
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
		
		if(extraPermission != null && !sender.hasPermission(extraPermission)) {
			Messages.ERROR_NO_BUILD_PERMISSION.sendTo(sender);
			return false;
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