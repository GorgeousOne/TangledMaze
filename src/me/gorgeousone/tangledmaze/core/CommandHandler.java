package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.*;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.utils.Constants;

public class CommandHandler implements CommandExecutor {
	
	private TangledMain plugin;
	
	private StartMaze startCommand;
	private DiscardAll discardCommand;
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
		discardCommand    = new DiscardAll();
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
			pageLinks[i].add(" page " + (i+2) + " ").color(Color.LIGHT_GREEN).click("/tm help " + (i+2), ClickAction.RUN);
		}
		
		pageLinks[0].add("/wand"               ).color(Color.GREEN).click("/tm help 2", ClickAction.RUN);
		pageLinks[1].add("/start"              ).color(Color.GREEN).click("/tm help 3", ClickAction.RUN);
		pageLinks[2].add("/discard"            ).color(Color.GREEN).click("/tm help 4", ClickAction.RUN);
		pageLinks[3].add("/teleport"           ).color(Color.GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[4].add("/select <tool type>" ).color(Color.GREEN).click("/tm help 5", ClickAction.RUN);
		pageLinks[5].add("/add /cut"           ).color(Color.GREEN).click("/tm help 6", ClickAction.RUN);
		pageLinks[6].add("/undo"               ).color(Color.GREEN).click("/tm help 7", ClickAction.RUN);
		pageLinks[7].add("/wallheight <integer").color(Color.GREEN).click("/tm help 8", ClickAction.RUN);
		pageLinks[8].add("/build <block type 1> ... <block type n>").color(Color.GREEN).click("/tm help 9", ClickAction.RUN);
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
					p.getInventory().addItem(plugin.getWand());
					p.sendMessage(Constants.prefix + "Selection wand added to your inventory.");
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
				ArrayList<String> materials = new ArrayList<>();
				
				if(args.length < 2) {
					sendCommandHelp(p, 9);
					break;
				}
				
				for (int i = 1; i < args.length; i++)
					materials.add(args[i]);
				
				buildCommand.execute(p, materials);
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
						p.sendMessage(ChatColor.YELLOW + "Well sadly this is not any secret page you discovered by accident, better luck next time.");
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
	
	private void sendCommandHelp(Player p, int page) {
		
		if(page < 0 || page > pageLinks.length+1)
			page = 1;
		
		p.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + page + "/" + (pageLinks.length+1));
		p.sendMessage("");
		
		switch (page) {
		//commands list
		case 1:
			p.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : pageLinks)
				pageLink.send(p);
			
			break;
			
		//wand
		case 2:
			p.sendMessage(ChatColor.YELLOW + "Wand Command");
			p.sendMessage(ChatColor.GREEN
					+ "This command hands you over a mighty selection wand. Use it considerately! "
					+ "Click two blocks and a selection will appear with the equipped shape (rectangle or ellipse). "
					+ "By clicking and dragging a blue corner you can resize your selection. "
					+ "For a new selection just click any other two blocks");
			break;
		//start
		case 3:
			p.sendMessage(ChatColor.YELLOW + "Start Command");
			p.sendMessage(ChatColor.GREEN
					+ "With this command you transform your selection into a maze's ground plot. "
					+ "Only now you can add other selections or cut things away.");
			pageLinks[6].send(p);
			break;
		//discard
		case 4:
			p.sendMessage(ChatColor.YELLOW + "Discard Command");
			p.sendMessage(ChatColor.GREEN
					+ "If you have a ground plot for a maze you don't want to continue building use this command to delete it. "
					+ "It will also remove any existing selection of yours!");
			break;
		//select
		case 5:
			p.sendMessage(ChatColor.YELLOW + "Select Command");
			p.sendMessage(ChatColor.GREEN
					+ "This command lets you choose tools for shaping/editing your maze's ground plot. "
					+ "The following tools can be selected:");

			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "rectangle");
			p.sendMessage(ChatColor.GREEN + "Your selections created with a wand will form rectangles.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "ellipse");
			p.sendMessage(ChatColor.GREEN + "Your selections will form ellipses.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "brush");
			p.sendMessage(ChatColor.GREEN
					+ "Left click on your maze's outline to reduce it at that block. "
					+ "Right click on your maze's outline to expand it at that block.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "exit");
			p.sendMessage(ChatColor.GREEN
					+ "Click on your maze's outline to mark where gaps shall be left. "
					+ "Click on a marking a second time to delete it. "
					+ "The diamond exit indicates where the path generating algorithm will begin building.");
			break;
			
		//teleport
		case 10:
			p.sendMessage(ChatColor.YELLOW + "Teleport Command");
			p.sendMessage(ChatColor.GREEN + "Teleports you back to your maze (if you have the permission for that.)");
			break;

		//add + cut
		case 6:
			p.sendMessage(ChatColor.YELLOW + "Add/Cut Command");
			p.sendMessage(ChatColor.GREEN
					+ "Adds or cuts away your selection from your ground plot. This only works with selections touching your maze. "
					+ "If you cut off parts of your maze from the main part (with diamond exit) there won't be generated any paths (just don't).");
			
			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "For undoing one of an action use:");
			pageLinks[5].send(p);
			break;
		//undo
		case 7:
			p.sendMessage(ChatColor.YELLOW + "Undo Command");
			p.sendMessage(ChatColor.GREEN
					+ "Undoes the last action performed on you maze like a brush / addition / cut away. "
					+ "Only the last 10 actions will be available for undoing.");
			break;
		//height
		case 8:
			p.sendMessage(ChatColor.YELLOW + "Path/Wallwidth & Wallheight Command");
			p.sendMessage(ChatColor.GREEN
					+ "Three commands for customization of the pathg eneration. " 
					+ "Path and wall width are limited to 10 blocks, wall height can be up to 20.");
			break;
		//build
		case 9:
			p.sendMessage(ChatColor.YELLOW + "Build Command");
			p.sendMessage(ChatColor.GREEN + "Builds your maze with the with a mixture of blocks you provide. " 
					+ "After the command's name enter type of blocks and their data value (if necessary), for example: ");
			p.sendMessage(ChatColor.DARK_GREEN + "\"quartz_block:1\" " + ChatColor.GREEN + "(chiseled quartz block).");
			p.sendMessage(ChatColor.GREEN
					+ "If you leave the server before you mazes starts to get built your work will be discarded. "
					+ "A finished maze cannot be edited any further.");

		default:
			return;
		}
		
		p.sendMessage("");
	}
}