package me.gorgeousone.tangledmaze.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.gorgeousone.tangledmaze.selections.Selection;

public class SelectionEvent extends Event implements Cancellable {

	protected final HandlerList handlers = new HandlerList();
	protected boolean isCancelled;
	
	private Selection selection;
	
	public SelectionEvent(Selection selection) {
		isCancelled = false;
		
		this.selection = selection;
	}

	public Selection getSelection() {
		return selection;
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
}