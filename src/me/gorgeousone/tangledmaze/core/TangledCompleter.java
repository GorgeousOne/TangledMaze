package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.util.Constants;

public class TangledCompleter implements TabCompleter {
	
	private ArrayList<String> commandNames, selectionTypes;
	
	public TangledCompleter() {
		
		commandNames = new ArrayList<>(Arrays.asList(
				"wand",
				"start",
				"discard",
				"select",
				"add",
				"cut",
				"pathwidth",
				"wallheight",
				"wallwidth",
				"build"));
		
		selectionTypes = new ArrayList<>(Arrays.asList(
				"rect",
				"circle",
				"brush",
				"exit"));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player))
			return null;
		
		if(!cmd.getName().equalsIgnoreCase("tangledmaze"))
			return null;

		Player p = (Player) sender;
		
		if(!p.hasPermission("tm.build"))
			return null;
		
		ArrayList<String> options = new ArrayList<>();
		
		switch (args.length) {
		case 1:
			
			if(args[0].equals("")) {
				
				options.addAll(commandNames);
				
				if(p.hasPermission(Constants.MAZE_TP_PERM))
					options.add("teleport");
				
			}else {
				
				for(String command : commandNames) {
					
					if(command.startsWith(args[0].toLowerCase())) {
						options.add(command);
						return options;
					}
				}
				
				if("teleport".startsWith(args[0].toLowerCase()) && p.hasPermission(Constants.MAZE_TP_PERM)) {
					options.add("teleport");
				}
			}
			
			break;
			
		case 2:
			
			if(args[0].equalsIgnoreCase("select")) {
				options.addAll(selectionTypes);
				break;
			}
			
		default:
			break;
		}
		
		return options;
	}
}