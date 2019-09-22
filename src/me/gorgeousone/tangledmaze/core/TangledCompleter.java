package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.MazeCommand;
import me.gorgeousone.tangledmaze.data.Constants;

public class TangledCompleter implements TabCompleter {
	
	private List<MazeCommand> commands; 
	private List<String> selectionTypes;
	
	public TangledCompleter(List<MazeCommand> commands) {
		
		this.commands = commands;
		
		//TODO find complicated solution to soft code this list
		selectionTypes = new ArrayList<>(Arrays.asList(
				"rect",
				"circle",
				"brush",
				"exit"));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!cmd.getName().equalsIgnoreCase("tangledmaze") || !(sender instanceof Player))
			return null;
		
		Player player = (Player) sender;
		
		if(!player.hasPermission(Constants.BUILD_PERM))
			return null;
		
		List<String> options = new ArrayList<>();
		
		switch (args.length) {
		case 1:
			
			for(MazeCommand command : commands) {

				if(!command.getName().startsWith(args[0].toLowerCase()))
					continue;

				if(command.getExtraPermission() == null || player.hasPermission(command.getExtraPermission()))
					options.add(command.getName());
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