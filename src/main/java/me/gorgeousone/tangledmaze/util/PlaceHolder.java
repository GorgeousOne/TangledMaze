package me.gorgeousone.tangledmaze.util;

public class PlaceHolder {
	
	private static char holderChar = '%';
	
	private String key;
	private Object value;
	
	public PlaceHolder(String placeHolder, Object value) {
		this.key = placeHolder;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public String getValue() {
		return value.toString();
	}
	
	public String apply(String message) {
		return message.replaceAll(holderChar + getKey() + holderChar, getValue());
	}
}