package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class MazeHandler {
	
	private HashMap<UUID, Maze> mazes;
	private HashMap<UUID, Integer> mazeHeights;
	private HashMap<Maze, Boolean> mazeVisibilities;
	
	public MazeHandler() {
		mazes            = new HashMap<>();
		mazeHeights      = new HashMap<>();
		mazeVisibilities = new HashMap<>();
	}
	
	public void reload() {
		for(Maze maze : mazes.values())
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
	
	public boolean isVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	public void setMazeHeight(Player p, int height) {
		mazeHeights.put(p.getUniqueId(), height);
		
		if(hasMaze(p))
			getMaze(p).setWallHeight(height);
	}
	
	public Integer getMazeHeight(Player p) {
		return mazeHeights.get(p.getUniqueId());
	}

	public void deselctMaze(Player p) {
		if(hasMaze(p)) {
			Maze maze = getMaze(p);
			
			hide(maze);
			mazeVisibilities.remove(maze);
			mazes.remove(p.getUniqueId());
		}
	}
	
	public void remove(Player p) {
		mazeVisibilities.remove(getMaze(p));
		mazeHeights.remove(p.getUniqueId());
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
		mazeVisibilities.put(maze, false);
		
		if(!mazeHeights.containsKey(uuid))
			mazeHeights.put(uuid, 3);
		
		maze.setWallHeight(mazeHeights.get(uuid));
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
	
	@SuppressWarnings("deprecation")
	public void hide(Maze maze) {
		Player p = maze.getOwner();
		
		if( p == null)
			return;
		
		mazeVisibilities.put(maze, false);
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	@SuppressWarnings("deprecation")
	public void showMazeAction(Player p, Maze maze, MazeAction action) {
		
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

	public void addExitToMaze(Player p, Block b) {
		if(!hasMaze(p))
			return;
		
		Maze maze = getMaze(p);

		//test if the clicked block is maze border
		if(!maze.isHighlighted(b)) {
			if(Math.random() < 1/3d)
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You can't place an exit here...");
			return;
		}
		
		Location loc = b.getLocation();
		
		//test if the clicked border block touches the inside as well as the outside of the maze
		if(!maze.canBeExit(loc)) {
			Utils.sendBlockLater(p, loc, Constants.MAZE_BORDER);
			return;
		}

		ArrayList<Location> exits = maze.getExits();

		//remove any existing exit at this block
		if(exits.contains(loc)) {
			Utils.sendBlockLater(p, loc, Constants.MAZE_BORDER);
			
			if(exits.indexOf(loc) == 0 && exits.size() > 1)
				Utils.sendBlockLater(p, exits.get(1), Constants.MAZE_MAIN_EXIT);
			
			maze.removeExit(loc);
		
		//add an exit to free border blocks
		}else {
			if(!exits.isEmpty())
				Utils.sendBlockLater(p, exits.get(0), Constants.MAZE_EXIT);
			
			Utils.sendBlockLater(p, loc, Constants.MAZE_MAIN_EXIT);
			maze.addExit(loc);
		}
	}		
}