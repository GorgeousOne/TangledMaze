package me.tangledmaze.gorgeousone.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeAction;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class MazeShapeEvent extends Event implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();
	protected boolean isCancelled;
	
	private Maze maze;
	private MazeAction action;
	private SelectionHandler sHandler;
	private SelectionHandler mHandler;
	
	public MazeShapeEvent(Maze maze, MazeAction action) {
		this.maze = maze;
		this.action = action;
		this.sHandler = TangledMain.getPlugin().getSelectionHandler();
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				if(!isCancelled()) {
					sHandler.deselectSelection(maze.getPlayer());
					maze.process(action);
					
					Player p = maze.getPlayer();
						
					if(maze.size() == 0 && p != null) {
						p.sendMessage(Constants.prefix + "Now you erased your maze away. You will have to start a new one.");
						mHandler.remove(p);
					}
				}
			}
		};
		event.runTask(TangledMain.getPlugin());
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
	
	public Maze getMaze() {
		return maze;
	}
	
	public MazeAction getAction() {
		return action;
	}
}