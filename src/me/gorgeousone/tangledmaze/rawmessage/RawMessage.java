package me.gorgeousone.tangledmaze.rawmessage;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class RawMessage {

	private static final String
			start = "{\"text\":\"\",\"extra\":[",
			end = "]}";

	private ArrayList<RawElement> rawElements;

	public RawMessage() {
		rawElements = new ArrayList<>();
	}
	
	public RawElement add(String text) {
		RawElement element = new RawElement(text, this);
		rawElements.add(element);
		return element;
	}

	public void send(CommandSender sender) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + this.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder message = new StringBuilder(start);
		
		for(RawElement collection : rawElements)
			message.append("{" + collection.toString() + "},");

		message.deleteCharAt(message.length()-1);
		message.append(end);

		return message.toString();
	}
}