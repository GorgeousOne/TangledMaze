package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.command.api.argument.ArgType;
import me.gorgeousone.tangledmaze.command.api.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.api.argument.Argument;
import me.gorgeousone.tangledmaze.command.api.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.util.HelpPage;
import me.gorgeousone.tangledmaze.util.Utils;

public class HelpCommand extends ArgCommand {
	
	private static int commandCount = 11;
	private static int pageCount = commandCount + 1;
	
	private static RawMessage[] pageLinks;
	private static HelpPage[] pages;
	
	public HelpCommand(MazeCommand mazeCommand) {
		super("help", null, mazeCommand);

		addAlias("?");
		addArg(new Argument("page", ArgType.INTEGER, new ArgValue(ArgType.INTEGER, "1")));
		
		createPageLinks();
		listHelpPages();
	}
	
	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] arguments) {

		int pageNumber = Utils.limit(arguments[0].getInt(), 1, pageCount);
		sendHelpPage(sender, pageNumber);
		return true;
	}
	
	public static void sendHelpPage(CommandSender sender, int pageNumber) {
		
		sender.sendMessage("");
		sender.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + pageNumber + "/" + pageCount);
		sender.sendMessage("");
		
		if(pageNumber == 1) {
			
			sender.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : pageLinks)
				pageLink.sendTo(sender);
		
		}else
			pages[pageNumber-2].send(sender);
	}
	
	private void createPageLinks() {
		
		pageLinks = new RawMessage[commandCount];
		
		for(int i = 0; i < pageLinks.length; i++) {
			pageLinks[i] = new RawMessage();
			pageLinks[i].add("page " + (i+2) + " ").color(Color.LIGHT_GREEN).click("/maze help " + (i+2), ClickAction.RUN);
		}

		int i = -1;

		pageLinks[++i].last().append("/maze wand").color(Color.GREEN);
		pageLinks[++i].last().append("/maze start").color(Color.GREEN);
		pageLinks[++i].last().append("/maze discard").color(Color.GREEN);
		pageLinks[++i].last().append("/maze teleport").color(Color.GREEN);
		pageLinks[++i].last().append("/maze select <tool>").color(Color.GREEN);
		pageLinks[++i].last().append("/maze add / cut").color(Color.GREEN);
		pageLinks[++i].last().append("/maze undo").color(Color.GREEN);
		pageLinks[++i].last().append("/maze set <dimension> <integer>").color(Color.GREEN);
		pageLinks[++i].last().append("/maze build <part> <block1> ...").color(Color.GREEN);
		pageLinks[++i].last().append("/maze unbuild <part>").color(Color.GREEN);
	}
	
	private void listHelpPages() {
		
		pages = new HelpPage[commandCount];
		int i = -1;

		pages[++i] = new HelpPage(Messages.COMMAND_WAND);
		pages[++i] = new HelpPage(Messages.COMMAND_START);
		pages[++i] = new HelpPage(Messages.COMMAND_DISCARD);
		pages[++i] = new HelpPage(Messages.COMMAND_TELEPORT);
		pages[++i] = new HelpPage(Messages.COMMAND_SELECT,
				Messages.TOOL_RECT,
				Messages.TOOL_CIRCLE,
				Messages.TOOL_BRUSH,
				Messages.TOOL_EXIT);
		pages[++i] = new HelpPage(Messages.COMMAND_ADD_CUT);
		pages[++i] = new HelpPage(Messages.COMMAND_UNDO);
		pages[++i] = new HelpPage(Messages.COMMAND_DIMENSIONS,
				Messages.DIMENSION_WALL_HEIGHT,
				Messages.DIMENSION_PATH_WIDTH,
				Messages.DIMENSION_WALL_WIDTH,
				Messages.DIMENSION_ROOF_WIDTH,
				Messages.DIMENSION_PATH_LENGTH);
		pages[++i] = new HelpPage(Messages.COMMAND_BUILD);
		pages[++i] = new HelpPage(Messages.COMMAND_UNBUILD);
	}
}