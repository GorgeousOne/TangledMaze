package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TextMessage {

	private String[] paragraphs;

	public TextMessage(String message, boolean readColorCodes) {

		if(readColorCodes) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}

		paragraphs = message.split("\\\\n");

		if(paragraphs.length < 2 || !readColorCodes) {
			return;
		}

		for(int i = 1; i < paragraphs.length; i++) {
			paragraphs[i] = ChatColor.getLastColors(paragraphs[i-1]) + paragraphs[i];
		}
	}

	public void send(CommandSender sender) {
		
		for(String paragraph : paragraphs) {
			sender.sendMessage(paragraph);
		}
	}
	
	public void send(CommandSender sender, PlaceHolder placeHolder) {
		
		for(String paragraph : paragraphs) {
			sender.sendMessage(paragraph.replaceAll("%" + placeHolder.getKey() + "%", placeHolder.getValueString()));
		}
	}
}
