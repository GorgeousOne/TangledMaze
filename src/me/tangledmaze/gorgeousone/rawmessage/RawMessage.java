package me.tangledmaze.gorgeousone.rawmessage;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RawMessage {

	private ArrayList<RawElement> collections;
	private String start, end;
	
	public RawMessage() {
		collections = new ArrayList<>();
		
		start = "{\"text\":\"\",\"extra\":[";
		end = "]}";
	}
	
	public RawElement add(String text) {
		RawElement collection = new RawElement(text, this);
		collections.add(collection);
		return collection;
	}

	public void send(Player p) {
		String command = "tellraw " + p.getName() + " " + this.toString();
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	@Override
	public String toString() {
		StringBuilder message = new StringBuilder(start);
		
		for(RawElement collection : collections)
			message.append("{" + collection.toString() + "},");

		message.deleteCharAt(message.length()-1);
		message.append(end);

		return message.toString();
	}
}