package me.gorgeousone.tangledmaze.handler;

import java.util.ArrayList;
import java.util.Arrays;

import me.gorgeousone.tangledmaze.util.Messages;
import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.*;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.util.Constants;

public class CommandHandler implements CommandExecutor {
	
	private StartMaze startCommand;
	private DiscardMaze discardCommand;
	private SelectTool selectCommand;
	private AddToMaze addCommand;
	private CutFromMaze cutCommand;
	private UndoAction undoCommand;
	private SetPathWidth pathWidthCommand;
	private SetWallHeight wallHeightCommand;
	private SetWallWidth wallWidthCommand;
	private BuildMaze buildCommand;
	private TpToMaze tpCommand;
	
	//contents of the first help page
	private RawMessage[] pageLinks;

	public CommandHandler() {
		
		startCommand      = new StartMaze();
		discardCommand    = new DiscardMaze();
		selectCommand     = new SelectTool();
		addCommand        = new AddToMaze();
		cutCommand        = new CutFromMaze();
		undoCommand       = new UndoAction();
		pathWidthCommand  = new SetPathWidth();
		wallHeightCommand = new SetWallHeight();
		wallWidthCommand  = new SetWallWidth();
		buildCommand      = new BuildMaze();
		tpCommand         = new TpToMaze();
		
		pageLinks = new RawMessage[9];
		
		for(int i = 0; i < pageLinks.length; i++) {
			pageLinks[i] = new RawMessage();
			pageLinks[i].add("page " + (i+1) + " ").color(Color.LIGHT_GREEN).click("/tm help " + (i+2), ClickAction.RUN);
		}
		
		pageLinks[0].add("/maze wand"             ).color(Color.GREEN).click("/tm help 2", ClickAction.RUN);
		pageLinks[1].add("/maze start"            ).color(Color.GREEN).click("/tm help 3", ClickAction.RUN);
		pageLinks[2].add("/maze discard"          ).color(Color.GREEN).click("/tm help 4", ClickAction.RUN);
		pageLinks[3].add("/maze teleport"         ).color(Color.GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[4].add("/maze select <tool>" ).color(Color.GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[5].add("/maze add / cut"         ).color(Color.GREEN).click("/tm help 6", ClickAction.RUN);
		pageLinks[6].add("/maze undo"             ).color(Color.GREEN).click("/tm help 7", ClickAction.RUN);
		pageLinks[7].add("/maze pathwidth / wallwidth / wallheight <integer").color(Color.GREEN).click("/tm help 8", ClickAction.RUN);
		pageLinks[8].add("/maze build <block> ...").color(Color.GREEN).click("/tm help 9", ClickAction.RUN);
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		
		if (commandSender instanceof ConsoleCommandSender)
			return true;
		
		Player player = (Player) commandSender;

		if (!command.getName().equalsIgnoreCase("tangledmaze"))
			return true;

		if (args.length < 1) {
			sendCommandHelp(player, 1);
			return false;
		}
		
		switch (args[0].toLowerCase()) {
			case "wand":
				if(player.hasPermission(Constants.WAND_PERM)) {
					player.getInventory().addItem(Utils.getMazeWand());
					player.sendMessage(Constants.prefix + "Maze wand added to your inventory.");
				}else
					player.sendMessage(Constants.insufficientPerms);
				break;
				
			case "start":
				startCommand.execute(player);
				break;
				
			case "discard":
				discardCommand.execute(player);
				break;

			case "select":
				if(args.length >= 2)
					selectCommand.execute(player, args[1]);
				else
					sendCommandHelp(player, 5);
				break;
				
			case "add":
			case "merge":
				addCommand.execute(player);
				break;
				
			case "cut":
			case "remove":
				cutCommand.execute(player);
				break;
			
			case "undo":
				undoCommand.execute(player);
				break;
			
			case "pathwidth":
				if(args.length >= 2)
					pathWidthCommand.execute(player, args[1]);
				else
					sendCommandHelp(player, 8);
				break;
			
			case "wallwidth":
				if(args.length >= 2)
					wallWidthCommand.execute(player, args[1]);
				else
					sendCommandHelp(player, 8);
				break;
			
			case "wallheight":
				if(args.length >= 2)
					wallHeightCommand.execute(player, args[1]);
				else
					sendCommandHelp(player, 8);
				break;
				
			case "build":

				if(args.length < 2) {
					sendCommandHelp(player, 9);
					break;
				}

				buildCommand.execute(player, (ArrayList<String>) Arrays.asList(args).subList(1, args.length));
				break;
			
			case "teleport":
			case "tp":
				tpCommand.execute(player);
				break;
				
			case "help":
			case "h":
			case "?":
				if(args.length >= 2) {
					try {
						int page = Integer.parseInt(args[1]);
						sendCommandHelp(player, page);
						return true;
						
					} catch (NumberFormatException e) {
						player.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + args[1] + "/cheese cake");
						player.sendMessage(ChatColor.YELLOW + "WOW ;) You discovered a hidden page! Not.");
						return true;
					}
				}
				sendCommandHelp(player, 1);
				break;
				
			default:
				return false;
		}
		return true;
	}
	
	private void sendCommandHelp(Player player, int page) {
		
		player.sendMessage("");

		if(page < 1 || page > pageLinks.length+1)
			page = 1;
		
		player.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + page + "/" + (pageLinks.length+1));
		player.sendMessage("");
		
		switch (page) {
		//commands list
		case 1:
			player.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : pageLinks) {
				pageLink.send(player);
			}
			
			break;
			
		//wand
		case 2:
			player.sendMessage(ChatColor.YELLOW + "/maze wand");
			Messages.COMMAND_WAND.send(player);
			break;
		//start
		case 3:
			player.sendMessage(ChatColor.YELLOW + "/maze start");
			Messages.COMMAND_START.send(player);
			sendPageLink(6, player);
			break;
		//discard
		case 4:
			player.sendMessage(ChatColor.YELLOW + "/maze discard");
			Messages.COMMAND_DISCARD.send(player);
			break;
		//select
		case 5:
			player.sendMessage(ChatColor.YELLOW + " /maze select <tool>");
			Messages.COMMAND_SELECT.send(player);
			player.sendMessage(ChatColor.DARK_GREEN + "rectangle:");
			Messages.TOOL_RECT.send(player);
			player.sendMessage(ChatColor.DARK_GREEN + "circle:");
			Messages.TOOL_CIRCLE.send(player);
			player.sendMessage(ChatColor.DARK_GREEN + "brush:");
			Messages.TOOL_BRUSH.send(player);
			player.sendMessage(ChatColor.DARK_GREEN + "exit:");
			Messages.TOOL_EXIT.send(player);

			break;
			
		//add + cut
		case 6:
			player.sendMessage(ChatColor.YELLOW + "/maze add / cut");
			Messages.COMMAND_ADD_CUT.send(player);
			sendPageLink(7, player);
			break;
		//undo
		case 7:
			player.sendMessage(ChatColor.YELLOW + "/maze undo");
			Messages.COMMAND_UNDO.send(player);
			break;
		//dimensions
		case 8:
			player.sendMessage(ChatColor.YELLOW + "/maze pathwidth / wallwidth / wallheight");
			Messages.COMMAND_DIMENSIONS.send(player);
			break;
		//build
		case 9:
			player.sendMessage(ChatColor.YELLOW + "/maze build <block> ...");
			Messages.COMMAND_BUILD.send(player);
			break;
		//teleport
		case 10:
			player.sendMessage(ChatColor.YELLOW + "/maze teleport");
			Messages.COMMAND_TELEPORT.send(player);
			break;

		default:
			break;
		}
	}

	private void sendPageLink(int page, Player player) {
		pageLinks[page-1].send(player);
	}
}