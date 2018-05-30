package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class MazeHandler {
	
	private HashMap<UUID, Maze> mazes;
	private HashMap<UUID, Vector> mazeDimensions;
	private HashMap<Maze, Boolean> mazeVisibilities;
	
	public MazeHandler() {
		mazes            = new HashMap<>();
		mazeDimensions   = new HashMap<>();
		mazeVisibilities = new HashMap<>();
	}
	
	/**
	 * Hides all mazes for a reload. They will be deleted afterwards.
	 */
	public void reload() {
		for(Maze maze : mazes.values())
			if(isVisible(maze))
				hide(maze);
	}
	
	public ArrayList<Maze> getMazes() {
		return new ArrayList<Maze>(mazes.values());
	}
	
	public boolean hasMaze(Player p) {
		return mazes.containsKey(p.getUniqueId());
	}
	
	public Maze getMaze(Player p) {
		return mazes.get(p.getUniqueId());
	}
	
	/**
	 * @return if a maze is displayed to it's owner.
	 */
	public boolean isVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}

	public void setMazePathWidth(Player p, int height) {
		mazeDimensions.get(p.getUniqueId()).setX(height);
		
		if(hasMaze(p))
			getMaze(p).setPathWidth(height);
	}
	
	public void setMazeWallHeight(Player p, int height) {
		mazeDimensions.get(p.getUniqueId()).setY(height);
		
		if(hasMaze(p))
			getMaze(p).setWallHeight(height);
	}
	
	public void setMazeWallWidth(Player p, int height) {
		mazeDimensions.get(p.getUniqueId()).setY(height);
		
		if(hasMaze(p))
			getMaze(p).setWallWidth(height);
	}
	
	public Integer getMazeHeight(Player p) {
		return mazeDimensions.get(p.getUniqueId()).getBlockY();
	}

	public void discardMaze(Player p) {
		if(hasMaze(p)) {
			Maze maze = getMaze(p);
			
			hide(maze);
			mazeVisibilities.remove(maze);
			mazes.remove(p.getUniqueId());
		}
	}
	
	//removes all held data to a player in this object.
	public void remove(Player p) {
		mazeVisibilities.remove(getMaze(p));
		mazeDimensions.remove(p.getUniqueId());
		mazes.remove(p.getUniqueId());
	}
	
	public void startMaze(Player p, RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		UUID uuid = p.getUniqueId();

		if(hasMaze(p))
			hide(getMaze(p));
		
		Maze maze = new Maze(selection.getShape(), p);
		mazes.put(uuid, maze);
		show(maze);
		
		if(!mazeDimensions.containsKey(uuid))
			mazeDimensions.put(uuid, new Vector(1, 3, 1));
		
		maze.setWallHeight(mazeDimensions.get(uuid).getBlockY());
	}
	
	public void addSelectionToMaze(Maze maze, RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");

		MazeAction action = maze.getAddition(selection.getShape());

		if(action.getAddedFill().size() == selection.getShape().size())
			throw new IllegalArgumentException("The passed selection does not intersect the maze properly.");
		
		Bukkit.getPluginManager().callEvent(new MazeShapeEvent(maze, action));
	}
	
	public void cutSelctionFromMaze(Maze maze, RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		MazeAction action = maze.getDeletion(selection.getShape());
		
		if(action.getRemovedFill().size() == 0)
			throw new IllegalArgumentException("The passed selection does not intersect the maze properly.");
		
		Bukkit.getPluginManager().callEvent(new MazeShapeEvent(maze, action));
	}
	
	public void addExitToMaze(Player p, Block b) {
		if(!hasMaze(p))
			return;
		
		Maze maze = getMaze(p);

		//test if the clicked block is maze border
		if(!maze.isBorder(b)) {
			if(Math.random() < 1/3d)
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You can't place an exit here...");
			return;
		}
		
		Location loc = b.getLocation();
		
		//test if the clicked border block touches the inside as well as the outside of the maze
		if(!maze.canBeExit(loc)) {
			Utils.sendBlockDelayed(p, loc, Constants.MAZE_BORDER);
			return;
		}

		ArrayList<Location> exits = maze.getExits();

		//remove any existing exit at this block
		if(exits.contains(loc)) {
			Utils.sendBlockDelayed(p, loc, Constants.MAZE_BORDER);
			
			if(exits.indexOf(loc) == 0 && exits.size() > 1)
				Utils.sendBlockDelayed(p, exits.get(1), Constants.MAZE_MAIN_EXIT);
			
			maze.removeExit(loc);
		
		//add an exit to free border blocks
		}else {
			if(!exits.isEmpty())
				Utils.sendBlockDelayed(p, exits.get(0), Constants.MAZE_EXIT);
			
			Utils.sendBlockDelayed(p, loc, Constants.MAZE_MAIN_EXIT);
			maze.addExit(loc);
		}
	}		
	
	/**
	 * Displays the border of a maze to it's owner. A visibility check with <b>isVisible(Maze);</b> is recommended before.
	 */
	@SuppressWarnings("deprecation")
	public void show(Maze maze) {
		Player p = maze.getOwner();
		
		if(p == null)
			return;

		mazeVisibilities.put(maze, true);
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
		for(Location exit : maze.getExits())
			p.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
		
		if(!maze.getExits().isEmpty())
			p.sendBlockChange(maze.getExits().get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
	}
	
	/**
	 * Hides a maze from the player who is creating it. A visibility check with <b>isVisible(Maze);</b> is recommended before.
	 */
	@SuppressWarnings("deprecation")
	public void hide(Maze maze) {
		Player p = maze.getOwner();
		
		if(p == null)
			return;

		mazeVisibilities.put(maze, false);
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	@SuppressWarnings("deprecation")
	public void showMazeAction(Player p, Maze maze, MazeAction action) {

		if(!isVisible(maze))
			return;
		
		for(Location point : action.getRemovedExits()) {
			p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
			if(maze.getExits().indexOf(point) == 0 && maze.getExits().size() > 1)
				p.sendBlockChange(maze.getExits().get(1), Constants.MAZE_MAIN_EXIT, (byte) 0);
		}
		
		for(Location point : action.getAddedBorder())
			p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
		for(Location point : action.getRemovedBorder())
			p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
}