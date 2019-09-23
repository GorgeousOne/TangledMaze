package me.gorgeousone.tangledmaze.util;

import org.bukkit.command.CommandSender;

public class HelpPage {
	
	private TextMessage[] description;
	
	public HelpPage(TextMessage... description) {
		this.description = description;
	}
	
	public void send(CommandSender sender) {
		
		for(TextMessage text : description)
			text.sendTo(sender);
	}
}
