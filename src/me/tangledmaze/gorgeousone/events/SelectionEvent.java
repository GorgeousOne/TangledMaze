package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class SelectionEvent extends Event implements Cancellable {

	protected final HandlerList handlers = new HandlerList();
	protected boolean isCancelled;
	
	protected Player p;
	protected Block clickedBlock;
	protected BukkitRunnable thread;
	
	protected String cancelMessage;
	
	public SelectionEvent(Player p, Block clickedBlock) {
		this.p = p;
		this.clickedBlock = clickedBlock;
		isCancelled = false;
	}

	public Player getPlayer() {
		return p;
	}
	
	public Block getClickedBlock() {
		return clickedBlock;
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
	public void setCancelled(boolean b) {
		isCancelled = b;
	}
	
	public void setCancelled(boolean b, String cancelMessage) {
		isCancelled = b;
		this.cancelMessage = cancelMessage;
	}
}