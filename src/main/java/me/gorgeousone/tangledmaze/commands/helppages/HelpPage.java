package me.gorgeousone.tangledmaze.commands.helppages;

import me.gorgeousone.tangledmaze.messages.TextMessage;
import org.bukkit.command.CommandSender;

/**
 * Simply a collection of TextMessages.
 */
public class HelpPage {
	
	private TextMessage[] description;
	
	public HelpPage(TextMessage... description) {
		this.description = description;
	}
	
	public void send(CommandSender sender) {
		
		for (TextMessage text : description) {
			text.sendTo(sender);
		}
	}
}