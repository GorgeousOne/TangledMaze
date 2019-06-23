package me.gorgeousone.tangledmaze.util;

public class TextException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private TextMessage text;
	private PlaceHolder placeHolder;
	
	public TextException(TextMessage message, PlaceHolder placeHolder) {
		
		this.text = message;
		this.placeHolder = placeHolder;
	}

	public TextMessage getText() {
		return text;
	}

	public PlaceHolder getPlaceHolder() {
		return placeHolder;
	}
}