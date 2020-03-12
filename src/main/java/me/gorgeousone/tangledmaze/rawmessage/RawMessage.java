package me.gorgeousone.tangledmaze.rawmessage;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RawMessage {
	
	private static final String
			start = "[",
			end = "]";
	
	private List<RawElement> rawElements;
	
	public RawMessage() {
		rawElements = new ArrayList<>();
	}
	
	public RawElement addText(String text) {
		
		RawElement element = new RawElement(text, this);
		rawElements.add(element);
		return element;
	}
	
	public RawElement lastText() {
		return rawElements.isEmpty() ? null : rawElements.get(rawElements.size() - 1);
	}
	
	public void sendTo(CommandSender sender) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + this.toString());
	}
	
	@Override
	public String toString() {
		
		StringBuilder message = new StringBuilder(start);
		
		for (RawElement elements : rawElements) {
			message.append("{");
			message.append(elements.toString());
			message.append("},");
		}
		
		message.deleteCharAt(message.length() - 1);
		message.append(end);
		
		return message.toString();
	}
}