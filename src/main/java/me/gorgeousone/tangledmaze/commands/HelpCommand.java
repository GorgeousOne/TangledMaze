package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.messages.HelpPage;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.rawmessage.ClickAction;
import me.gorgeousone.tangledmaze.rawmessage.Color;
import me.gorgeousone.tangledmaze.rawmessage.RawMessage;
import me.gorgeousone.tangledmaze.utils.MathHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand extends ArgCommand {
	
	private List<RawMessage> pageLinks;
	private List<HelpPage> helpPages;
	
	public HelpCommand(MazeCommand mazeCommand) {
		super("help", null, true, mazeCommand);
		
		addAlias("?");
		addArg(new Argument("page", ArgType.INTEGER).setDefaultTo("1"));
		
		loadHelpPagesContent();
		createPageLinks();
	}
	
	private void loadHelpPagesContent() {
		
		helpPages = new ArrayList<>(Arrays.asList(
		
		new HelpPage(Messages.COMMAND_WAND),
		new HelpPage(Messages.COMMAND_START),
		new HelpPage(Messages.COMMAND_DISCARD),
		new HelpPage(Messages.COMMAND_TELEPORT),
		new HelpPage(Messages.COMMAND_SELECT,
		             Messages.TOOL_RECT,
		             Messages.TOOL_CIRCLE,
		             Messages.TOOL_BRUSH,
		             Messages.TOOL_EXIT),
		new HelpPage(Messages.COMMAND_ADD_CUT),
		new HelpPage(Messages.COMMAND_UNDO),
		new HelpPage(Messages.COMMAND_DIMENSIONS,
		             Messages.DIMENSION_WALL_HEIGHT,
		             Messages.DIMENSION_PATH_WIDTH,
		             Messages.DIMENSION_WALL_WIDTH,
		             Messages.DIMENSION_ROOF_WIDTH,
		             Messages.DIMENSION_PATH_LENGTH),
		new HelpPage(Messages.COMMAND_BUILD),
		new HelpPage(Messages.COMMAND_UNBUILD),
		new HelpPage(Messages.COMMAND_BACKUP),
		new HelpPage(Messages.COMMAND_LOAD)));
	}
	
	private void createPageLinks() {
		
		pageLinks = new ArrayList<>();
		
		for (int i = 0; i < helpPages.size(); i++) {
			
			RawMessage pageLink = new RawMessage();
			pageLink
					.addText("page " + (i + 2) + " ")
					.color(Color.GREEN)
					.onClick("/maze help " + (i + 2), ClickAction.RUN)
					.hoverText("Click to open");
			pageLinks.add(pageLink);
		}
		
		int iter = -1;
		
		pageLinks.get(++iter).addText("/maze wand").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze start").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze discard").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze teleport").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze select <tool>").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze add / cut").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze undo").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze set <dimensions> <integer>").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze build <part> <block> ...").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze unbuild <part>").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze backup <filename>").color(Color.LIGHT_GREEN);
		pageLinks.get(++iter).addText("/maze load <filename>").color(Color.LIGHT_GREEN);
	}
	
	@Override
	protected void onCommand(CommandSender sender, ArgValue[] arguments) {
		
		int pageNumber = MathHelper.clamp(arguments[0].getInt(), 1, getTotalPages());
		sendHelpPage((Player) sender, pageNumber);
	}
	
	public void sendHelpPage(Player sender, int pageNumber) {
		
		sender.sendMessage("");
		sender.sendMessage(Constants.prefix + "--- Help Pages --- " + ChatColor.GREEN + pageNumber + "/" + getTotalPages());
		sender.sendMessage("");
		
		if (pageNumber == 1) {
			sender.sendMessage(ChatColor.GREEN + "List of all /tangledmaze commands: ");
			
			for (RawMessage pageLink : pageLinks) {
				pageLink.sendTo(sender);
			}
			
		} else
			helpPages.get(pageNumber - 2).send(sender);
	}
	
	private int getTotalPages() {
		return helpPages.size() + 1;
	}
}