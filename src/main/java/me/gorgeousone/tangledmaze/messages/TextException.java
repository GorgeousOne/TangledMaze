package me.gorgeousone.tangledmaze.messages;

import me.gorgeousone.tangledmaze.data.Messages;
import org.bukkit.command.CommandSender;

public class TextException extends Exception {
	
	private Messages text;
	private PlaceHolder[] placeHolders;
	
	public TextException(Messages message, PlaceHolder... placeHolders) {
		
		this.text = message;
		this.placeHolders = placeHolders;
	}
	
	public void sendTextTo(CommandSender receiver) {
		text.sendTo(receiver, placeHolders);
	}
}