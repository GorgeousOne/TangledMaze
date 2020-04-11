package me.gorgeousone.tangledmaze.messages;

import me.gorgeousone.tangledmaze.data.Messages;
import org.bukkit.command.CommandSender;

/**
 * Simply a collection of Langs.
 */
public class HelpPage {
	
	private Messages[] description;
	
	public HelpPage(Messages... description) {
		this.description = description;
	}
	
	public void send(CommandSender sender) {
		
		for (Messages text : description) {
			text.sendTo(sender);
		}
	}
}