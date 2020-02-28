package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.BlockVec;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

//TODO move the tasks of this class to ToolHandler and MazeHandler. Reduce methods to simply displaying collections of blocks/locations

/**
 * This class handles the visibility of every maze and clipboard (Clip of Clippingtool).
 * There are different methods for showing and hiding a maze or clipboard,
 * also to display changed that happened in a MazeAction or refresh a maze when hiding a clipboard.
 */
public class Renderer implements Listener {
	
	private JavaPlugin plugin;
	
	private HashMap<ClipTool, Boolean> clipVisibilities;
	private HashMap<Maze, Boolean> mazeVisibilities;
	
	//TODO think about replacement for this temporary MazeHandler instance?
	private MazeHandler mazeHandler;
	
	public Renderer(JavaPlugin plugin) {
		
		this.plugin = plugin;
		
		clipVisibilities = new HashMap<>();
		mazeVisibilities = new HashMap<>();
	}
	
	public void unregisterClipTool(ClipTool clipTool) {
		clipVisibilities.remove(clipTool);
	}
	
	public void unregisterMaze(Maze maze) {
		mazeVisibilities.remove(maze);
	}
	
	public void setMazeHandler(MazeHandler mazeHandler) {
		this.mazeHandler = mazeHandler;
	}
	
	public void hideAllClues() {
		
		for (ClipTool clipTool : clipVisibilities.keySet()) {
			if (isClipToolVisible(clipTool))
				hideClipTool(clipTool, false);
		}
		
		for (Maze maze : mazeVisibilities.keySet()) {
			if (isMazeVisible(maze))
				hideMaze(maze);
		}
	}
	
	public boolean isClipToolVisible(ClipTool shape) {
		return clipVisibilities.get(shape);
	}
	
	public boolean isMazeVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	//displays a clipboard to it's owner with block changes
	public void displayClipTool(ClipTool clipTool) {
		
		clipVisibilities.put(clipTool, true);
		Player player = clipTool.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				if (clipTool.hasClip()) {
					for (Location point : clipTool.getClip().getBorderBlocks()) {
						player.sendBlockChange(point, Constants.CLIPBOARD_BORDER.createBlockData());
					}
				}
				
				for (BlockVec vertex : clipTool.getVertices())
					player.sendBlockChange(vertex.toLocation(), Constants.CLIPBOARD_VERTEX.createBlockData());
			}
		}.runTaskLater(plugin, 2);
	}
	
	//hides a clipboard completely with the option to redisplay previously covered maze parts
	public void hideClipTool(ClipTool clipTool, boolean updateMaze) {
		
		clipVisibilities.put(clipTool, false);
		Player player = clipTool.getPlayer();
		
		for (BlockVec vertex : clipTool.getVertices())
			player.sendBlockChange(vertex.toLocation(), vertex.getBlock().getBlockData());
		
		if (clipTool.hasClip()) {
			for (Location point : clipTool.getClip().getBorderBlocks()) {
				player.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
			}
		}
		
		//TODO reactivate maze updates on clip hiding
		if (updateMaze) {
			Maze maze = mazeHandler.getMaze(player);
			
			if (maze != null && maze.hasClip() && isMazeVisible(maze))
				redisplayMaze(maze, clipTool);
		}
	}
	
	//displays a maze to it's owner with block changes
	public void displayMaze(Maze maze) {
		
		if (maze.isConstructed())
			return;
		
		mazeVisibilities.put(maze, true);
		Player player = maze.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for (Location point : maze.getClip().getBorderBlocks())
					player.sendBlockChange(point, Constants.MAZE_BORDER.createBlockData());
				
				for (Vec2 exit : maze.getSecondaryExits())
					player.sendBlockChange(maze.getClip().getBlockLoc(exit), Constants.MAZE_EXIT.createBlockData());
				
				if (maze.hasExits())
					player.sendBlockChange(maze.getClip().getBlockLoc(maze.getEntrance()), Constants.MAZE_MAIN_EXIT.createBlockData());
				
			}
		}.runTaskLater(plugin, 2);
	}
	
	//hides a maze
	public void hideMaze(Maze maze) {
		
		if (maze.isConstructed())
			return;
		
		mazeVisibilities.put(maze, false);
		Player player = maze.getPlayer();
		
		for (Location point : maze.getClip().getBorderBlocks())
			player.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	//Displays maze parts that were covered under a clipboard.
	private void redisplayMaze(Maze maze, ClipTool hiddenClipboard) {
		
		Player player = maze.getPlayer();
		
		for (BlockVec vertex : hiddenClipboard.getVertices()) {
			
			if (maze.getClip().isBorderBlock(vertex.getBlock()))
				player.sendBlockChange(vertex.toLocation(), Constants.MAZE_BORDER.createBlockData());
		}
		
		if (!hiddenClipboard.hasClip())
			return;
		
		for (Location border : hiddenClipboard.getClip().getBorderBlocks()) {
			
			if (maze.getClip().isBorderBlock(border.getBlock()))
				maze.getPlayer().sendBlockChange(border, Constants.MAZE_BORDER.createBlockData());
		}
	}
	
	//displays single blocks of a clipboard (which might covered by e.g. falling block)
	public void redisplayClipboardBlock(ClipTool clipTool, Location point) {
		
		Player player = clipTool.getPlayer();
		
		if (clipTool.isVertex(point.getBlock()))
			sendBlockDelayed(player, point, Constants.CLIPBOARD_VERTEX);
		else
			sendBlockDelayed(player, point, Constants.CLIPBOARD_BORDER);
	}
	
	//displays single blocks of a maze
	public void redisplayMazeBlock(Maze maze, Location point) {
		
		Player player = maze.getPlayer();
		Vec2 locVec = new Vec2(point);
		
		if (locVec.equals(maze.getEntrance()))
			sendBlockDelayed(player, point, Constants.MAZE_MAIN_EXIT);
		
		else if (maze.exitsContain(locVec))
			sendBlockDelayed(player, point, Constants.MAZE_EXIT);
		
		else
			sendBlockDelayed(player, point, Constants.MAZE_BORDER);
	}
	
	public void displayMazeAction(Maze maze, ClipChange action) {
		
		Player player = maze.getPlayer();
		Clip clip = maze.getClip();
		
		for (Vec2 exit : action.getRemovedExits()) {
			
			player.sendBlockChange(clip.getBlockLoc(exit), Constants.MAZE_BORDER.createBlockData());
			
			List<Vec2> mazeExits = maze.getExits();
			
			if (exit.equals(maze.getEntrance()) && mazeExits.size() > 1)
				player.sendBlockChange(clip.getBlockLoc(mazeExits.get(mazeExits.size() - 2)), Constants.MAZE_MAIN_EXIT.createBlockData());
		}
		
		for (Vec2 point : action.getAddedBorder()) {
			player.sendBlockChange(action.getBorder(point), Constants.MAZE_BORDER.createBlockData());
		}
		
		for (Vec2 point : action.getRemovedBorder()) {
			Location block = action.getBorder(point);
			player.sendBlockChange(block, block.getBlock().getType().createBlockData());
		}
	}
	
	public void sendBlockDelayed(Player player, Location point, Material mat) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.sendBlockChange(point, mat.createBlockData());
			}
		}.runTaskLater(plugin, 2);
	}
}