package me.gorgeousone.tangledmaze.selections;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public abstract class Selection {
	
	protected Player p;

	public Selection(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public abstract void interact(Block b, Action a);
}