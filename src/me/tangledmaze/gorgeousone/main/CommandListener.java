package me.tangledmaze.gorgeousone.main;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.commands.*;
import me.tangledmaze.gorgeousone.utils.Constants;

public class CommandListener implements CommandExecutor {
	
	private SelectTool selectCommand;
	private StartMaze startCommand;
	private SetMazeHeight heigthCommand;
	private AddMaze addCommand;
	private SubtractMaze subtCommand;
	private DeselectAll deselectCommand;
	private BuildMaze buildCommand;
	
	public CommandListener() {
		selectCommand = new SelectTool();
		startCommand = new StartMaze();
		heigthCommand = new SetMazeHeight();
		addCommand = new AddMaze();
		subtCommand = new SubtractMaze();
		deselectCommand = new DeselectAll();
		buildCommand = new BuildMaze();
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		
		if (commandSender instanceof ConsoleCommandSender)
			return true;
		
		Player p = (Player) commandSender;
		
		if (!command.getName().equalsIgnoreCase("tangledmaze"))
			return true;
		
		if (args.length < 1) {
			sendHelp(p, 0);
			return false;
		}
		
		switch (args[0].toLowerCase()) {
			case "wand":
				if(p.hasPermission(Constants.wandPerm))
					p.getInventory().addItem(TangledMain.getWand());
				else
					p.sendMessage(Constants.insufficientPerms);
				break;
				
			case "select":
				if(args.length >= 2)
					selectCommand.execute(p, args[1]);
				else
					sendHelp(p, 2);
				break;
				
			case "start":
				startCommand.execute(p);
				break;
			
			case "height":
				if(args.length >= 2)
					heigthCommand.execute(p, args[1]);
				else
					sendHelp(p, 2);
				break;
				
			case "add":
			case "merge":
				addCommand.execute(p);
				break;
				
			case "cut":
			case "subtract":
				subtCommand.execute(p);
				break;
				
			case "deselect":
				deselectCommand.execute(p);
				break;

			case "build":
				ArrayList<String> materials = new ArrayList<>();
				
				if(args.length >= 2)
					for (int i = 1; i < args.length; i++)
						materials.add(args[i]);
				
				buildCommand.execute(p, materials);
				break;
			
			case "undo":
				break;
				
			case "help":
			case "h":
			case "?":
			default:
				if (args.length > 2) {
					try {
						int page = Integer.parseInt(args[2]);
						sendHelp(p, page);
					} catch (NumberFormatException e) {
						sendHelp(p, 1);
						break;
					}
				}
				sendHelp(p, 1);
				break;
		}
			
		return true;
	}
	
	
//	private void hasAnyPermission(Permission... perms) {
//	}
//	
//	private void hasAllPermissions(Permission... perms) {
//	}
	
	private void sendHelp(Player p, int page) {
		p.sendMessage("Help");
	}

}
