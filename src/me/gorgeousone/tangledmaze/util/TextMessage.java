package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TextMessage {

	private String[] paragraphs;
	private ChatColor color;

	public TextMessage(String unalteredMessage, ChatColor color) {
		paragraphs = unalteredMessage.split("\\\\n");
		this.color = color;
	}

	public void send(CommandSender sender) {

		for(String paragraph : paragraphs) {
			sender.sendMessage(color + paragraph);
		}
	}
}
