package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.command.MazeCommand;

public class HelpPage {
	
	private String title;
	private TextMessage description;
	
	public HelpPage(MazeCommand command, TextMessage description) {
	
		title = ChatColor.DARK_GREEN + command.getUsage();
		this.description = description;
	}
	
	public void send(CommandSender sender) {
		
		sender.sendMessage(title);
		description.send(sender);
	}
}
