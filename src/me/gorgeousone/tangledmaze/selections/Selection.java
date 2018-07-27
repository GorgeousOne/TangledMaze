package me.gorgeousone.tangledmaze.selections;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public abstract class Selection {
	
	protected Player builder;

	public Selection(Player builder) {
		this.builder = builder;
	}
	
	public Player getPlayer() {
		return builder;
	}
	
	public abstract void interact(Block clickedBlock, Action interactAction);
}