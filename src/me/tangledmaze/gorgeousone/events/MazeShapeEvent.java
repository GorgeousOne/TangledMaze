package me.tangledmaze.gorgeousone.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeAction;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.selections.ShapeSelection;
import me.tangledmaze.gorgeousone.utils.Constants;

public class MazeShapeEvent extends Event implements Cancellable {

	protected final HandlerList handlers = new HandlerList();
	protected boolean isCancelled;
	
	private Maze maze;
	private MazeAction action;
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	private String cancelMessage;
	
	public MazeShapeEvent(Maze maze, MazeAction action) {
		this.maze = maze;
		this.action = action;
		this.sHandler = TangledMain.getPlugin().getSelectionHandler();
		this.mHandler = TangledMain.getPlugin().getMazeHandler();
		
		if(maze.getPlayer() != null) {
			Player p = maze.getPlayer();

			int maxMazeSize = TangledMain.getPlugin().getNormalMazeSize();
			
			if(p.hasPermission(Constants.staffPerm))
				maxMazeSize = TangledMain.getPlugin().getStaffMazeSize();
			else if(p.hasPermission(Constants.vipPerm))
				maxMazeSize = TangledMain.getPlugin().getVipMazeSize();
			
			if(maxMazeSize >= 0 && maze.size() + action.getAddedFill().size() > maxMazeSize) {
				setCancelled(true, ChatColor.RED + "Your maze would become " + (maze.size() + action.getAddedFill().size() - maxMazeSize)
						+ " blocks greater that the amount of blocks you are allowed to use at once (" + maxMazeSize + " blocks).");
			}
		}
		
		Player p = maze.getPlayer();
		
		if(isCancelled) {
			p.sendMessage(cancelMessage);
//			Utils.sendBlockDelayed(p, b.getLocation(), Constants.MAZE_BORDER);	TODO somehow make the block border again
			
		}else {
			sHandler.discardSelection(maze.getPlayer());
			mHandler.showMazeAction(p, maze, action);
			maze.processAction(action, true);
			
			if(maze.size() == 0) {
				p.sendMessage(Constants.prefix + "Now you erased your whole maze. You will have to start a new one.");
				mHandler.discardMaze(p);
				sHandler.setSelection(p, new ShapeSelection(p));
			}
		}
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
	
	public void setCancelled(boolean b, String cancelMessage) {
		isCancelled = b;
		this.cancelMessage = cancelMessage;
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