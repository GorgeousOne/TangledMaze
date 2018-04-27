package me.tangledmaze.gorgeousone.main;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.commands.*;
import me.tangledmaze.gorgeousone.rawmessage.ClickAction;
import me.tangledmaze.gorgeousone.rawmessage.Color;
import me.tangledmaze.gorgeousone.rawmessage.RawMessage;
import me.tangledmaze.gorgeousone.utils.Constants;

public class CommandListener implements CommandExecutor {
	
	private SelectTool selectCommand;
	private StartMaze startCommand;
	private SetMazeHeight heigthCommand;
	private AddMaze addCommand;
	private SubtractMaze subtCommand;
	private DeselectAll deselectCommand;
	private BuildMaze buildCommand;
	
	//contents of the first help page
	private RawMessage[] pageLinks;

	public CommandListener() {
		selectCommand   = new SelectTool();
		startCommand    = new StartMaze();
		heigthCommand   = new SetMazeHeight();
		addCommand      = new AddMaze();
		subtCommand     = new SubtractMaze();
		deselectCommand = new DeselectAll();
		buildCommand    = new BuildMaze();
		
		pageLinks = new RawMessage[7];
		
		for(int i = 0; i < 7; i++) {
			pageLinks[i] = new RawMessage();
			pageLinks[i].add(" " + (i+2) + " ").color(Color.GREEN);
		}
		
		pageLinks[0].add("start").                                  color(Color.LIGHT_GREEN).click("/tm help 2", ClickAction.RUN);
		pageLinks[1].add("undo").                                   color(Color.LIGHT_GREEN).click("/tm help 3", ClickAction.RUN);
		pageLinks[2].add("discard").                                color(Color.LIGHT_GREEN).click("/tm help 4", ClickAction.RUN);
		pageLinks[3].add("select <tool type>").                     color(Color.LIGHT_GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[4].add("add/cut").                                color(Color.LIGHT_GREEN).click("/tm help 6", ClickAction.RUN);
		pageLinks[5].add("height <integer>").                       color(Color.LIGHT_GREEN).click("/tm help 7", ClickAction.RUN);
		pageLinks[6].add("build <block type 1> ... <block type n>").color(Color.LIGHT_GREEN).click("/tm help 8", ClickAction.RUN);
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		if (commandSender instanceof ConsoleCommandSender)
			return true;
		
		Player p = (Player) commandSender;

		if (!command.getName().equalsIgnoreCase("tangledmaze"))
			return true;

		if (args.length < 1) {
			sendCommandHelp(p, 0);
			return false;
		}
		
		switch (args[0].toLowerCase()) {
			case "wand":
				if(p.hasPermission(Constants.wandPerm)) {
					p.getInventory().addItem(TangledMain.getWand());
					p.sendMessage(Constants.prefix + "Selection wand added to your inventory.");
				}else
					p.sendMessage(Constants.insufficientPerms);
				break;
				
			case "start":
				startCommand.execute(p);
				break;
				
			case "undo":
				break;
				
			case "discard":
				deselectCommand.execute(p);
				break;

			case "select":
				if(args.length >= 2)
					selectCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 2);
				break;
				
			case "add":
			case "merge":
				addCommand.execute(p);
				break;
				
			case "cut":
			case "subtract":
				subtCommand.execute(p);
				break;
				
			case "height":
				if(args.length >= 2)
					heigthCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 2);
				break;
				
			case "build":
				ArrayList<String> materials = new ArrayList<>();
				
				if(args.length >= 2)
					for (int i = 1; i < args.length; i++)
						materials.add(args[i]);
				
				buildCommand.execute(p, materials);
				break;
				
			case "help":
			case "h":
			case "?":
			default:
				
				if(args.length >= 2) {
					try {
						int page = Integer.parseInt(args[1]);
						sendCommandHelp(p, page);
						return true;
						
					} catch (NumberFormatException e) {
						p.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + args[1] + "/cheese cake");
						p.sendMessage(ChatColor.YELLOW + "Do you really think page " + ChatColor.DARK_GREEN + args[1] + ChatColor.YELLOW + " could exist?");
						return true;
					}
				}
				sendCommandHelp(p, 1);
				break;
		}
			
		return true;
	}
	
	private void sendCommandHelp(Player p, int page) {
		if(page < 0 || page > 8)
			page = 1;
		
		p.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + page + "/8");
		p.sendMessage("");
		
		switch (page) {
		//commands list
		case 1:
			p.sendMessage(ChatColor.YELLOW + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : pageLinks)
				pageLink.send(p);
			
			break;

		//start
		case 2:
			p.sendMessage(ChatColor.YELLOW + "Start Command");
			p.sendMessage(ChatColor.GREEN + "When you are done creating a selection use this command to create the raw ground plot of your maze.");
			p.sendMessage(ChatColor.GREEN + "To edit it further you can use the add/cut command and the brush tool.");
			break;
		//undo
		case 3:
			p.sendMessage(ChatColor.YELLOW + "Undo Command");
			p.sendMessage(ChatColor.GREEN + "The undo command isnt implemented yet. who knows how it wil work"); //TODO
			break;
		//discard
		case 4:
			p.sendMessage(ChatColor.YELLOW + "Discard Command");
			p.sendMessage(ChatColor.GREEN + "If you have created a ground plot for a maze you don't want to work with anymore use this command to delete it.");
			p.sendMessage(ChatColor.GREEN + "It will also remove any existing selection of yours.");
			break;
		//select
		case 5:
			p.sendMessage(ChatColor.YELLOW + "Select Command");
			p.sendMessage(ChatColor.GREEN + "With this command you can choose which tool you want to use for editing your maze's ground plot");
			p.sendMessage(ChatColor.GREEN + "You have the choice between the following tools:");
			p.sendMessage("");
			
			p.sendMessage(ChatColor.DARK_GREEN + "rectangle");
			p.sendMessage(ChatColor.GREEN + "Your selections set with a selection wand will form rectangles.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "ellipse");
			p.sendMessage(ChatColor.GREEN + "Your selections will form ellipses.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "brush");
			p.sendMessage(ChatColor.GREEN + "By clicking on a maze's outline with this tool you can brush away the outline at that specific block.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "exit");
			p.sendMessage(ChatColor.GREEN + "By clicking on a maze's outline you can select exits, where gaps will be left when building the maze.");
			
			p.sendMessage("");
			p.sendMessage(ChatColor.GREEN + "All these actions are undoable with the command " + ChatColor.DARK_GREEN + "\"undo\"" + ChatColor.GREEN + ".");
			break;
		//add + cut
		case 6:
			p.sendMessage(ChatColor.YELLOW + "Add/Cut Command");
			p.sendMessage(ChatColor.GREEN + "If you have a ground plot for a maze and already selected a new selection you can use these 2 commands to either add the selection to your maze or to cut it away.");
			p.sendMessage(ChatColor.GREEN + "If you want to undo a mistake you can use the command" + ChatColor.DARK_GREEN + "\"undo\"" + ChatColor.GREEN + ".");
			break;
		//height
		case 7:
			p.sendMessage(ChatColor.YELLOW + "Height Command");
			p.sendMessage(ChatColor.GREEN + "With this command you can decide how tall the walls of your maze should be built.");
			p.sendMessage(ChatColor.GREEN + "The default height is 3 blocks and values between 1 and 20 will be accepted (which already would be extra ordinary to my mind).");
			break;
		//build
		case 8:
			p.sendMessage(ChatColor.YELLOW + "Build Command");
			p.sendMessage(ChatColor.GREEN + "This command will finally build your maze up. You are in the postion to choose which type of blocks should be used here:");
			p.sendMessage(ChatColor.GREEN + "After " + ChatColor.DARK_GREEN + "\"/tangledmaze build\"" + ChatColor.GREEN + " just type the names of the blocks you want to use and plus \":\" and the needed data value.");
			p.sendMessage(ChatColor.GREEN + "Depending on the size of you maze and the power of the server the time to finish can vary.");
			p.sendMessage(ChatColor.DARK_GREEN + "Warning: " + ChatColor.YELLOW + "This command cannot be undone, so be careful where you build your mazes!");
		default:
			break;
		}
		
		p.sendMessage("");
//		p.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + page + "/8");
	}
}