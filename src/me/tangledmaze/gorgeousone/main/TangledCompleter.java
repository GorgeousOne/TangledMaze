package me.tangledmaze.gorgeousone.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.utils.Constants;

public class TangledCompleter implements TabCompleter {
	
	private ArrayList<String> subCmds, selectionTypes;
	public TangledCompleter() {
		subCmds = new ArrayList<>(Arrays.asList(
				"wand",
				"select",
				"start",
				"height",
				"add",
				"cut",
				"discard",
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
				options.addAll(subCmds);
				
				if(p.hasPermission(Constants.mazeTpPerm))
					options.add("teleport");
				
			}else {
				for(String command : subCmds)
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