package me.gorgeousone.tangledmaze.util;

import org.bukkit.command.CommandSender;

public class TextException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private TextMessage text;
	private PlaceHolder placeHolder;
	
	public TextException(TextMessage message, PlaceHolder placeHolder) {
		
		this.text = message;
		this.placeHolder = placeHolder;
	}

	private TextMessage getText() {
		return text;
	}

	private PlaceHolder getPlaceHolder() {
		return placeHolder;
	}
	
	public void sendTextTo(CommandSender receiver) {
		getText().sendTo(receiver, getPlaceHolder());
	}
}