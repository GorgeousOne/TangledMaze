package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.commands.helppages.HelpPage;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.utils.MathHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand extends ArgCommand {
	
	private final int commandCount = 10;
	private final int pageCount = commandCount + 1;
	
	private RawMessage[] pageLinks;
	private HelpPage[] helpPages;
	
	public HelpCommand(MazeCommand mazeCommand) {
		super("help", null, false, mazeCommand);
		
		addAlias("?");
		addArg(new Argument("page", ArgType.INTEGER).setDefaultTo("1"));
		
		createPageLinks();
		loadHelpPagesContent();
	}
	
	private void createPageLinks() {
		
		pageLinks = new RawMessage[commandCount];
		
		for (int i = 0; i < pageLinks.length; i++) {
			pageLinks[i] = new RawMessage();
			pageLinks[i].addText("page " + (i + 2) + " ").color(Color.LIGHT_GREEN).onClick("/maze help " + (i + 2), ClickAction.RUN);
		}
		
		int iter = -1;
		
		pageLinks[++iter].lastText().append("/maze wand").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze start").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze discard").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze teleport").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze select <tool>").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze add / cut").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze undo").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze set <dimensions> <integer>").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze build <part> <block> ...").color(Color.GREEN);
		pageLinks[++iter].lastText().append("/maze unbuild <part>").color(Color.GREEN);
	}
	
	private void loadHelpPagesContent() {
		
		helpPages = new HelpPage[commandCount];
		int iter = -1;
		
		helpPages[++iter] = new HelpPage(Messages.COMMAND_WAND);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_START);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_DISCARD);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_TELEPORT);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_SELECT,
		                                 Messages.TOOL_RECT,
		                                 Messages.TOOL_CIRCLE,
		                                 Messages.TOOL_BRUSH,
		                                 Messages.TOOL_EXIT);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_ADD_CUT);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_UNDO);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_DIMENSIONS,
		                                 Messages.DIMENSION_WALL_HEIGHT,
		                                 Messages.DIMENSION_PATH_WIDTH,
		                                 Messages.DIMENSION_WALL_WIDTH,
		                                 Messages.DIMENSION_ROOF_WIDTH,
		                                 Messages.DIMENSION_PATH_LENGTH);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_BUILD);
		helpPages[++iter] = new HelpPage(Messages.COMMAND_UNBUILD);
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {
		
		int pageNumber = MathHelper.clamp(arguments[0].getInt(), 1, pageCount);
		sendHelpPage(sender, pageNumber);
		return true;
	}
	
	public void sendHelpPage(CommandSender sender, int pageNumber) {
		
		sender.sendMessage("");
		sender.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + pageNumber + "/" + pageCount);
		sender.sendMessage("");
		
		if (pageNumber == 1) {
			sender.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for (RawMessage pageLink : pageLinks) {
				pageLink.sendTo(sender);
			}
			
		} else
			helpPages[pageNumber - 2].send(sender);
	}
}