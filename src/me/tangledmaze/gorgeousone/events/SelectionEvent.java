package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class SelectionEvent extends Event implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();
	protected boolean isCancelled;
	
	protected Player p;
	protected Block clickedBlock;
	
	public SelectionEvent(Player p, Block clickedBlock) {
		this.p = p;
		this.clickedBlock = clickedBlock;
		isCancelled = false;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}
	
	//TODO 
	public Player getPlayer() {
		return p;
	}
	
	public Block getClickedBlock() {
		return clickedBlock;
	}
}