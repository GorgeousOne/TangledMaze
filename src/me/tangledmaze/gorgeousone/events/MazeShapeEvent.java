package me.tangledmaze.gorgeousone.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeAction;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class MazeShapeEvent extends Event implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();
	protected boolean isCancelled;
	
	private Maze maze;
	private MazeAction action;
	private SelectionHandler sHandler;
	
	public MazeShapeEvent(Maze maze, MazeAction action) {
		this.maze = maze;
		this.action = action;
		this.sHandler = TangledMain.getPlugin().getSelectionHandler();
		
		Player p = maze.getPlayer();
		int totalSize = maze.size() + action.getAddedFill().size() + action.getRemovedBorder().size();
		
		if(totalSize <= maze.size())
			maze.process(action);
			
		int buildLimit;
		
		if(p.hasPermission(Constants.staffPerm))
			buildLimit = TangledMain.getBuidlSizeStaff();
		else if(p.hasPermission(Constants.vipPerm))
			buildLimit = TangledMain.getBuidlSizeVIP();
		else
			buildLimit = TangledMain.getBuidlSizeNormal();
		
		if(buildLimit >= 0 && totalSize > buildLimit)
			setCancelled(true);
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				if(!isCancelled()) {
					sHandler.deselectSelection(p);
					maze.process(action);
				}else
					p.sendMessage(ChatColor.RED + "This action was cancelled because you maze would become " + 
					(totalSize - buildLimit) + " blocks bigger than your limit of " + buildLimit + " blocks.");
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