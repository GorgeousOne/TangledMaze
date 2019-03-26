package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.util.HelpPage;
import me.gorgeousone.tangledmaze.util.Utils;

public class HelpCommand extends MazeCommand {
	
	private static RawMessage[] pageLinks;
//	private static TextMessage[] helpPages;
	private static HelpPage[] pages;
	
	public HelpCommand() {
		
		super("help", "/tangledmaze help <page>", 0, false, null, "?");
		
		createPageLinks();
		listHelpPages();
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		sendHelpPage(sender, getPageNumber(arguments));
		return true;
	}
	
	public static void sendHelpPage(CommandSender sender, int pageNumber) {
		
		if(pageNumber < 1 || pageNumber > pages.length+1) {
			return;
		}
		
		sender.sendMessage("");
		sender.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + pageNumber + "/" + (pages.length+1));
		sender.sendMessage("");
		
		if(pageNumber == 1) {
			
			sender.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : pageLinks)
				pageLink.send(sender);
		
		}else
			pages[pageNumber-2].send(sender);
	}
	
	private void createPageLinks() {
		
		pageLinks = new RawMessage[9];
		
		for(int i = 0; i < pageLinks.length; i++) {
			pageLinks[i] = new RawMessage();
			pageLinks[i].add("page " + (i+2) + " ").color(Color.LIGHT_GREEN).click("/maze help " + (i+2), ClickAction.RUN);
		}
		
		pageLinks[0].last().append("/maze wand").color(Color.GREEN);
		pageLinks[1].last().append("/maze start").color(Color.GREEN);
		pageLinks[2].last().append("/maze discard").color(Color.GREEN);
		pageLinks[3].last().append("/maze teleport").color(Color.GREEN);
		pageLinks[4].last().append("/maze select <tool>").color(Color.GREEN);
		pageLinks[5].last().append("/maze add / cut").color(Color.GREEN);
		pageLinks[6].last().append("/maze undo").color(Color.GREEN);
		pageLinks[7].last().append("/maze pathwidth / wallwidth / wallheight <integer>").color(Color.GREEN);
		pageLinks[8].last().append("/maze build <block> ...").color(Color.GREEN);
	}
	
	private void listHelpPages() {
		
		pages = new HelpPage[9];
		pages[0] = new HelpPage(Messages.COMMAND_WAND);
		pages[1] = new HelpPage(Messages.COMMAND_START);
		pages[2] = new HelpPage(Messages.COMMAND_DISCARD);
		pages[3] = new HelpPage(Messages.COMMAND_TELEPORT);
		pages[4] = new HelpPage(Messages.COMMAND_SELECT, Messages.TOOL_RECT, Messages.TOOL_CIRCLE, Messages.TOOL_BRUSH, Messages.TOOL_EXIT);
		pages[5] = new HelpPage(Messages.COMMAND_ADD_CUT);
		pages[6] = new HelpPage(Messages.COMMAND_UNDO);
		pages[7] = new HelpPage(Messages.COMMAND_DIMENSIONS);
		pages[8] = new HelpPage(Messages.COMMAND_BUILD);

	}
	
	private int getPageNumber(String[] arguments) {
		
		if(arguments.length == 0)
			return 1;
		
		try {
			return Utils.limitInt(Integer.parseInt(arguments[0]), 1, pages.length+1);
		
		} catch (NumberFormatException ex) {
			return 1;
		}
	}
}
