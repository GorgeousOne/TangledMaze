package me.gorgeousone.tangledmaze.utils;

import org.bukkit.command.CommandSender;

public class TextException extends Exception {
	
	private TextMessage text;
	private PlaceHolder[] placeHolders;
	
	public TextException(TextMessage message, PlaceHolder... placeHolders) {

		this.text = message;
		this.placeHolders = placeHolders;
	}

	public void sendTextTo(CommandSender receiver) {
		text.sendTo(receiver, placeHolders);
	}
}