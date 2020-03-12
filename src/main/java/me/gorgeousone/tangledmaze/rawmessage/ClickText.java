package me.gorgeousone.tangledmaze.rawmessage;

public class ClickText {
	
	private String value;
	private ClickAction action;
	
	public ClickText(String value, ClickAction action) {
		this.value = value;
		this.action = action;
	}
	
	@Override
	public String toString() {
		return "\"clickEvent\":{\"action\":\"" + action + "\",\"value\":\"" + value + "\"}";
	}
}