package me.gorgeousone.tangledmaze.handler;

import java.util.ArrayList;
import java.util.Arrays;

import me.gorgeousone.tangledmaze.util.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.*;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.util.Constants;

public class CommandHandler implements CommandExecutor {
	
	private TangledMain plugin;
	
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

	public CommandHandler(TangledMain plugin) {
		this.plugin = plugin;
		
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
			pageLinks[i].add(" page " + (i+1) + " ").color(Color.LIGHT_GREEN).click("/tm help " + (i+2), ClickAction.RUN);
		}
		
		pageLinks[0].add("/maze wand"             ).color(Color.GREEN).click("/tm help 2", ClickAction.RUN);
		pageLinks[1].add("/maze start"            ).color(Color.GREEN).click("/tm help 3", ClickAction.RUN);
		pageLinks[2].add("/maze discard"          ).color(Color.GREEN).click("/tm help 4", ClickAction.RUN);
		pageLinks[3].add("/maze teleport"         ).color(Color.GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[4].add("/maze select <tool>" ).color(Color.GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[5].add("/maze add / cut"         ).color(Color.GREEN).click("/tm help 6", ClickAction.RUN);
		pageLinks[6].add("/maze undo"             ).color(Color.GREEN).click("/tm help 7", ClickAction.RUN);
		pageLinks[7].add("/maze pathwidth / wallwidth / wallheight <integer").color(Color.GREEN).click("/tm help 8", ClickAction.RUN);
		pageLinks[8].add("/maze build <block> <block> ...").color(Color.GREEN).click("/tm help 9", ClickAction.RUN);
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		
		if (commandSender instanceof ConsoleCommandSender)
			return true;
		
		Player p = (Player) commandSender;

		if (!command.getName().equalsIgnoreCase("tangledmaze"))
			return true;

		if (args.length < 1) {
			sendCommandHelp(p, 1);
			return false;
		}
		
		switch (args[0].toLowerCase()) {
			case "wand":
				if(p.hasPermission(Constants.wandPerm)) {
					p.getInventory().addItem(plugin.getMazeWand());
					p.sendMessage(Constants.prefix + "Maze wand added to your inventory.");
				}else
					p.sendMessage(Constants.insufficientPerms);
				break;
				
			case "start":
				startCommand.execute(p);
				break;
				
			case "discard":
				discardCommand.execute(p);
				break;

			case "select":
				if(args.length >= 2)
					selectCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 5);
				break;
				
			case "add":
			case "merge":
				addCommand.execute(p);
				break;
				
			case "cut":
			case "subtract":
				cutCommand.execute(p);
				break;
			
			case "undo":
				undoCommand.execute(p);
				break;
			
			case "pathwidth":
				if(args.length >= 2)
					pathWidthCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 8);
				break;
			
			case "wallwidth":
				if(args.length >= 2)
					wallWidthCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 8);
				break;
			
			case "wallheight":
				if(args.length >= 2)
					wallHeightCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 8);
				break;
				
			case "build":

				if(args.length < 2) {
					sendCommandHelp(p, 9);
					break;
				}

				buildCommand.execute(p, (ArrayList<String>) Arrays.asList(args).subList(1, args.length));
				break;
			
			case "teleport":
			case "tp":
				tpCommand.execute(p);
				break;
				
			case "help":
			case "h":
			case "?":
				if(args.length >= 2) {
					try {
						int page = Integer.parseInt(args[1]);
						sendCommandHelp(p, page);
						return true;
						
					} catch (NumberFormatException e) {
						p.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + args[1] + "/cheese cake");
						p.sendMessage(ChatColor.YELLOW + "WOW ;) You discovered a hidden page! Not.");
						return true;
					}
				}
				sendCommandHelp(p, 1);
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
//			player.sendMessage(ChatColor.GREEN
//					+ "This command gives you a mighty maze wand. Use it considerately! "
//					+ "Click two blocks and a clipboard will appear with the equipped shape (rectangle or circle). "
//					+ "By clicking and dragging a blue corner you can resize your clipboard. "
//					+ "For starting over just click any other two blocks.");
			break;
		//start
		case 3:
			player.sendMessage(ChatColor.YELLOW + "/maze start");
			Messages.COMMAND_START.send(player);
//			player.sendMessage(ChatColor.GREEN
//					+ "With this command you transform your clipboard into a maze's floor plan. "
//					+ "Now you can add or cut away other clipboards.");
			sendPageLink(6, player);
			break;
		//discard
		case 4:
			player.sendMessage(ChatColor.YELLOW + "/maze discard");
			Messages.COMMAND_DISCARD.send(player);
//			player.sendMessage(ChatColor.GREEN + "Deletes your floor plan and clipboard.");
			break;
		//select
		case 5:
			player.sendMessage(ChatColor.YELLOW + " /maze select <tool>");
			Messages.COMMAND_SELECT.send(player);
			Messages.TOOL_RECT.send(player);
			Messages.TOOL_CIRCLE.send(player);
			Messages.TOOL_BRUSH.send(player);
			Messages.TOOL_EXIT.send(player);

//			player.sendMessage(ChatColor.GREEN
//					+ "Lets you choose tools for editing your maze's floor plan. "
//					+ "The following tools can be selected:");
//
//			player.sendMessage("");
//			player.sendMessage(ChatColor.DARK_GREEN + "rectangle");
//			player.sendMessage(ChatColor.GREEN + "Your clipboards created with a wand will form rectangles.");
//
//			player.sendMessage(ChatColor.DARK_GREEN + "circle");
//			player.sendMessage(ChatColor.GREEN + "Your clipboards will form circles.");
//
//			player.sendMessage(ChatColor.DARK_GREEN + "brush");
//			player.sendMessage(ChatColor.GREEN
//					+ "Left click on your maze's outline to reduce it at that block. "
//					+ "Right click on your maze's outline to expand it at that block.");
//
//			player.sendMessage(ChatColor.DARK_GREEN + "exit");
//			player.sendMessage(ChatColor.GREEN
//					+ "Click on your maze's outline to set exits (or entrances, however you perceive that). "
//					+ "Click on an exit a second time to delete it again. "
//					+ "The diamond exit indicates where the maze generator will begin building.");
			break;
			
		//add + cut
		case 6:
			player.sendMessage(ChatColor.YELLOW + "/maze add / cut");
			Messages.COMMAND_ADD_CUT.send(player);
//			player.sendMessage(ChatColor.GREEN
//					+ "Adds or cuts away your clipboard from your floor plan. This only works if the clipboard is touching your maze. "
//					+ "If you cut off an area from the main part of your maze (with diamond exit) there won't be generated any paths (just don't).");
//
//			player.sendMessage("");
//			player.sendMessage(ChatColor.DARK_GREEN + "For undoing one of these action use:");
			sendPageLink(7, player);
			break;
		//undo
		case 7:
			player.sendMessage(ChatColor.YELLOW + "/maze undo");
			Messages.COMMAND_UNDO.send(player);

//			player.sendMessage(ChatColor.GREEN
//					+ "Undoes the last action performed on you maze like adding, cutting away or brushing. "
//					+ "Only the last 10 actions will be saved for undoing.");
			break;
		//dimensions
		case 8:
			player.sendMessage(ChatColor.YELLOW + "/maze pathwidth / wallwidth / wallheight");
			Messages.COMMAND_DIMENSIONS.send(player);
//			player.sendMessage(ChatColor.GREEN + "These three commands are for customizing the size of maze paths and walls when being generated.");

			break;
		//build
		case 9:
			player.sendMessage(ChatColor.YELLOW + "/maze build <block> <block> ...");
			Messages.COMMAND_BUILD.send(player);

//			player.sendMessage(ChatColor.GREEN
//					+ "Builds your maze with the with a mixture of blocks you enter as arguments. "
//					+ "Specify each block type with it's name (and their data value if necessary), for example: ");
//
//			player.sendMessage(ChatColor.DARK_GREEN + "\"/maze build quartz_block:1\" " + ChatColor.GREEN + "(that's chiseled quartz).");
//			player.sendMessage(ChatColor.GREEN + "Keep in mind that a built maze cannot be edited any further.");
			break;

		//teleport
		case 10:
			player.sendMessage(ChatColor.YELLOW + "/maze teleport");
			Messages.COMMAND_TELEPORT.send(player);
//			player.sendMessage(ChatColor.GREEN + "Teleports you back to your maze (if you have the permission for that).");
			break;

		default:
			break;
		}
	}

	private void sendPageLink(int page, Player player) {
		pageLinks[page-1].send(player);
	}
}