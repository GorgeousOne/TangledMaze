package me.tangledmaze.gorgeousone.core;

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

public class CommandHandler implements CommandExecutor {
	
	private StartMaze startCommand;
	private DiscardtAll discardCommand;
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
		discardCommand    = new DiscardtAll();
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
		
		pageLinks[0].add("wand").color(Color.YELLOW);
		pageLinks[1].add("start").color(Color.YELLOW);
		pageLinks[2].add("discard").color(Color.YELLOW);
		pageLinks[3].add("select <tool type>").color(Color.YELLOW);
		pageLinks[4].add("add/cut").color(Color.YELLOW);
		pageLinks[5].add("undo").color(Color.YELLOW);
		pageLinks[6].add("wallheight <integer>").color(Color.YELLOW);
		pageLinks[7].add("teleport").color(Color.YELLOW);
		pageLinks[8].add("build <block type 1> ... <block type n>").color(Color.YELLOW);
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
			
			case "wallheight":
				if(args.length >= 2)
					wallHeightCommand.execute(p, args[1]);
				else
					sendCommandHelp(p, 8);
				break;
				
			case "wallwidth":
				if(args.length >= 2)
					wallWidthCommand.execute(p, args[1]);
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
			default:
				
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
			p.sendMessage(ChatColor.YELLOW + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : pageLinks)
				pageLink.send(p);
			
			break;
		//wand
		case 2:
			p.sendMessage(ChatColor.YELLOW + "Wand Command");
			p.sendMessage(ChatColor.GREEN + "This command hands you over a mighty selection wand. Use it consideretely!");
			p.sendMessage(ChatColor.GREEN + "By clicking any 2 blocks you can create a new selection (shown in gold). With this selection you can either create a maze ground plot or shape an exising one.");

			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "For these actions see:");
			pageLinks[1].send(p);
			pageLinks[4].send(p);
			break;
		//start
		case 3:
			p.sendMessage(ChatColor.YELLOW + "Start Command");
			p.sendMessage(ChatColor.GREEN + "After finishing a selection you can use this command to create a ground plot for your maze (shown in red).");
			p.sendMessage(ChatColor.GREEN + "The purpose of this step is that you can now change the shape by adding/cutting out other selections or brushing it to your wishes.");
			
			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "For more details see:");
			pageLinks[3].send(p);
			pageLinks[4].send(p);
			break;
		//discard
		case 4:
			p.sendMessage(ChatColor.YELLOW + "Discard Command");
			p.sendMessage(ChatColor.GREEN + "If you have created a ground plot for a maze you don't want to continue anymore use this command to delete it.");
			p.sendMessage(ChatColor.GREEN + "It will also remove any existing selection of yours!");
			break;
		//select
		case 5:
			p.sendMessage(ChatColor.YELLOW + "Select Command");
			p.sendMessage(ChatColor.GREEN + "With this command you can choose which tool you want to use for shaping/editing your maze's ground plot.");
			p.sendMessage(ChatColor.GREEN + "You have the choice between the following tools:");
			p.sendMessage("");
			
			p.sendMessage(ChatColor.DARK_GREEN + "rectangle");
			p.sendMessage(ChatColor.GREEN + "Your selections set with a wand will form rectangles.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "ellipse");
			p.sendMessage(ChatColor.GREEN + "Your selections will form ellipses.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "brush");
			p.sendMessage(ChatColor.GREEN + "By clicking on a maze's outline with this tool you can brush away the outline at that specific block.");
			
			p.sendMessage(ChatColor.DARK_GREEN + "exit");
			p.sendMessage(ChatColor.GREEN + "By clicking on a maze's outline you can select exits, where gaps will be left when building the maze.");
			
			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "For more information on how to shape a maze with an selection see:");
			pageLinks[4].send(p);
			break;
		//add + cut
		case 6:
			p.sendMessage(ChatColor.YELLOW + "Add/Cut Command");
			p.sendMessage(ChatColor.GREEN + "You can use this comand to add or cut away further selections from the ground plot of your maze.");
			p.sendMessage(ChatColor.GREEN + "Any shape that isn't connected to the main part/the part where you set the main exit will only be filled with walls.");
			p.sendMessage(ChatColor.GREEN + "(just don't)");
			
			p.sendMessage("");
			p.sendMessage(ChatColor.DARK_GREEN + "For undoing one of these actions see:");
			pageLinks[5].send(p);
			break;
		//undo
		case 7:
			p.sendMessage(ChatColor.YELLOW + "Undo Command");
			p.sendMessage(ChatColor.GREEN + "If you think the last thing you edited on your maze's ground plot does not look that good use this command to undo it.");
			p.sendMessage(ChatColor.GREEN + "Only the last 10 actions will be undoable and you cannot change anything on an already built maze.");
			break;
		//height
		case 8:
			p.sendMessage(ChatColor.YELLOW + "Wall Height Command");
			p.sendMessage(ChatColor.GREEN + "With this command you can decide how tall the walls of your maze should be built.");
			p.sendMessage(ChatColor.GREEN + "The default height is 3 blocks and it can be set up to 20 (which already would be extra ordinary to my mind).");
			break;
		//build
		case 9:
			p.sendMessage(ChatColor.YELLOW + "Build Command");
			p.sendMessage(ChatColor.GREEN + "This command will finally build up your maze. You have to choose which type of blocks should be used therefore.");
			p.sendMessage(ChatColor.GREEN + "After the command type in a kind block and it's data value (if not 0). An example would be "
					+ ChatColor.DARK_GREEN + "\"quartz_block:1\" " + ChatColor.GREEN + "(Which is chiseled quartz).");
			p.sendMessage(ChatColor.GREEN + "Depending on the size of your maze and the power of the server the time to finish can vary.");
			p.sendMessage(ChatColor.GREEN + "If you leave the server before you mazes gets built your work will be discarded.");
			p.sendMessage(ChatColor.GREEN + "A finished maze cannot be edited any further.");
			break;
		//teleport
		case 10:
			p.sendMessage(ChatColor.YELLOW + "Teleport Command");
			p.sendMessage(ChatColor.GREEN + "In case you ever forgot where you started to build your maze you can use this command to teleport back to it.");
			p.sendMessage(ChatColor.GREEN + "(Under the condition you have the permission for it.)");
			break;
		default:
			break;
		}
		
		p.sendMessage("");
	}
}