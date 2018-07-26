package me.gorgeousone.tangledmaze.selections;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class ExitSetterSelection extends Selection {
	
	public ExitSetterSelection(Player p) {
		super(p);
	}
	
	public void interact(Block b, Action a) {
	}
}