package me.gorgeousone.tangledmaze.util;

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

	public String getValueString() {
		return value.toString();
	}
}