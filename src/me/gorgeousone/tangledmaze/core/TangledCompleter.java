package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TangledCompleter implements TabCompleter {
	
	private ArrayList<String> buildPermCmds, selectionTypes;
	
	public TangledCompleter() {
		
		buildPermCmds = new ArrayList<>(Arrays.asList(
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
				"rectangle",
				"ellipse",
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
		
		argumentswitch:
		switch (args.length) {
		case 1:
			if(args[0].equals("")) {
				options.addAll(buildPermCmds);
				
				if(p.hasPermission(Constants.mazeTpPerm))
					options.add("teleport");
				
			}else {
				for(String command : buildPermCmds)
					if(command.startsWith(args[0].toLowerCase())) {
						options.add(command);
						break argumentswitch;
					}
				
				if("teleport".startsWith(args[0].toLowerCase()) && p.hasPermission(Constants.mazeTpPerm))
					options.add("teleport");
			}
			break;
			
		case 2:
			if(args[0].equalsIgnoreCase("select"))
				options.addAll(selectionTypes);
			break;
		default:
			break;
		}
		return options;
	}

}