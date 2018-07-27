package me.gorgeousone.tangledmaze.selections;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public abstract class Selection {
	
	private UUID player;

	public Selection(Player builder) {
		this.player = builder.getUniqueId();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}
	
	public abstract void interact(Block clickedBlock, Action interactAction);
}