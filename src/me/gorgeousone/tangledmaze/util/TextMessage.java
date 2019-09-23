package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TextMessage {

	private String[] paragraphs;

	public TextMessage(String message, boolean readColorCodes) {
		setText(message, readColorCodes);
	}
	
	public void setText(String message, boolean readColorCodes) {
		
		String alteredMessage;
		
		if(readColorCodes)
			alteredMessage = ChatColor.translateAlternateColorCodes('&', message);
		else
			alteredMessage = message;
			
		paragraphs = alteredMessage.split("\\\\n");

		if(paragraphs.length < 2)
			return;

		for(int i = 1; i < paragraphs.length; i++)
			paragraphs[i] = ChatColor.getLastColors(paragraphs[i-1]) + paragraphs[i];
	}
	
	public void sendTo(CommandSender receiver) {
		
		for(String paragraph : paragraphs)
			receiver.sendMessage(paragraph);
	}
	
	public void sendTo(CommandSender receiver, PlaceHolder... placeHolders) {
		
		for(String paragraph : paragraphs) {
			
			String alteredParagraph = paragraph;
			
			for(PlaceHolder placeHolder : placeHolders)
				alteredParagraph = placeHolder.apply(alteredParagraph);
			
			receiver.sendMessage(alteredParagraph);
		}
	}
}