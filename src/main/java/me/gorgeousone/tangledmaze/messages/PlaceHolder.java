package me.gorgeousone.tangledmaze.messages;

public class PlaceHolder {
	
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
		char holderChar = '%';
		return message.replaceAll(holderChar + getKey() + holderChar, getValue());
	}
}