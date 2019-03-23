package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.util.TextMessage;
import me.gorgeousone.tangledmaze.util.Utils;

public class HelpCommand extends MazeCommand {
	
	private static RawMessage[] helpPageLinks;
	private static TextMessage[] helpPages;
	
	public HelpCommand() {
		
		super("help", "/tangledmaze help <page>", 0, false, null, "?");
		
		createPageLinks();
		listHelpPages();
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		sendHelpPage(sender, getPageNumber(arguments));
		return true;
	}
	
	public static void sendHelpPage(CommandSender sender, int pageNumber) {
		
		if(pageNumber < 1 || pageNumber > helpPages.length+1) {
			return;
		}
		
		sender.sendMessage("");
		sender.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + pageNumber + "/" + (helpPages.length+1));
		sender.sendMessage("");
		
		if(pageNumber == 1) {
			
			sender.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for(RawMessage pageLink : helpPageLinks)
				pageLink.send(sender);
		
		}else
			helpPages[pageNumber-2].send(sender);
	}
	
	private void createPageLinks() {
		
		helpPageLinks = new RawMessage[9];
		
		for(int i = 0; i < helpPageLinks.length; i++) {
			helpPageLinks[i] = new RawMessage();
			helpPageLinks[i].add("page " + (i+2) + " ").color(Color.LIGHT_GREEN).click("/maze help " + (i+2), ClickAction.RUN);
		}
		
		helpPageLinks[0].last().append("/maze wand").color(Color.GREEN);
		helpPageLinks[1].last().append("/maze start").color(Color.GREEN);
		helpPageLinks[2].last().append("/maze discard").color(Color.GREEN);
		helpPageLinks[3].last().append("/maze teleport").color(Color.GREEN);
		helpPageLinks[4].last().append("/maze select <tool>").color(Color.GREEN);
		helpPageLinks[5].last().append("/maze add / cut").color(Color.GREEN);
		helpPageLinks[6].last().append("/maze undo").color(Color.GREEN);
		helpPageLinks[7].last().append("/maze pathwidth / wallwidth / wallheight <integer>").color(Color.GREEN);
		helpPageLinks[8].last().append("/maze build <block> ...").color(Color.GREEN);
	}
	
	private void listHelpPages() {
		
		helpPages = new TextMessage[9];
		helpPages[0] = Messages.COMMAND_WAND;
		helpPages[1] = Messages.COMMAND_START;
		helpPages[2] = Messages.COMMAND_DISCARD;
		helpPages[3] = Messages.COMMAND_TELEPORT;
		helpPages[4] = Messages.COMMAND_SELECT;
		helpPages[5] = Messages.COMMAND_ADD_CUT;
		helpPages[6] = Messages.COMMAND_UNDO;
		helpPages[7] = Messages.COMMAND_DIMENSIONS;
		helpPages[8] = Messages.COMMAND_BUILD;
	}
	
	private int getPageNumber(String[] arguments) {
		
		if(arguments.length == 0)
			return 1;
		
		try {
			return Utils.limitInt(Integer.parseInt(arguments[0]), 1, helpPages.length+1);
		
		} catch (NumberFormatException ex) {
			return 1;
		}
	}
}
