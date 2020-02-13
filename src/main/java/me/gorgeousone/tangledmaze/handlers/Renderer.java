package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.BlockVec;
import me.gorgeousone.tangledmaze.utils.Vec2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//TODO move the tasks of this class to ToolHandler and MazeHandler. Reduce methods to simply displaying collections of blocks/locations
/**
 * This class handles the visibility of every maze and clipboard (Clip of Clippingtool).
 * There are different methods for showing and hiding a maze or clipboard,
 * also to display changed that happened in a MazeAction or refresh a maze when hiding a clipboard.
 */
public class Renderer implements Listener {

	private HashMap<ClipTool, Boolean> clipVisibilities;
	private HashMap<Maze, Boolean> mazeVisibilities;

	public Renderer() {
		clipVisibilities = new HashMap<>();
		mazeVisibilities = new HashMap<>();
	}

	public void hideAllClues() {
		for (ClipTool clipboard : clipVisibilities.keySet()) {
			if(isClipboardVisible(clipboard))
				hideClipboard(clipboard, false);
		}

		for(Maze maze : mazeVisibilities.keySet()) {
			if(isMazeVisible(maze))
				hideMaze(maze);
		}
	}

	public void registerClip(ClipTool clipboard) {
		clipVisibilities.put(clipboard, false);
	}
	
	public void registerMaze(Maze maze) {
		mazeVisibilities.put(maze, false);
	}
	
	public void unregisterShape(ClipTool clipboard) {
		clipVisibilities.remove(clipboard);
	}
	
	public void unregisterMaze(Maze maze) {
		mazeVisibilities.remove(maze);
	}
	
	public boolean isClipboardVisible(ClipTool shape) {
		return clipVisibilities.get(shape);
	}
	
	public boolean isMazeVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	//displays a clipboard to it's owner with block changes
	public void displayClipboard(ClipTool clipboard) {

		clipVisibilities.put(clipboard, true);
		Player player = clipboard.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {

				if(clipboard.hasClip()) {
					for(Location loc : clipboard.getClip().getBorderBlocks())
						player.sendBlockChange(loc, Constants.CLIPBOARD_BORDER, (byte) 0);
				}

				for(BlockVec vertex : clipboard.getVertices())
					player.sendBlockChange(vertex.toLocation(), Constants.CLIPBOARD_VERTEX, (byte) 0);
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}
	
	//hides a clipboard completely with the option to redisplay previously covered maze parts
	public void hideClipboard(ClipTool clipboard, boolean updateMaze) {
		
		clipVisibilities.put(clipboard, false);
		Player player = clipboard.getPlayer();
		
		for(BlockVec vertex : clipboard.getVertices())
			player.sendBlockChange(vertex.toLocation(), vertex.getBlock().getType(), vertex.getBlock().getData());
		
		if(clipboard.hasClip()) {
			for(Location loc : clipboard.getClip().getBorderBlocks())
				player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
		}

		//TODO reactivate maze updates on clip hiding
//		if(updateMaze) {
//			Maze maze = MazeHandler.getMaze(player);
//
//			if(maze != null && maze.isStarted() && isMazeVisible(maze))
//				redisplayMaze(maze, clipboard);
//		}
	}
	
	//displays a maze to it's owner with block changes
	public void displayMaze(Maze maze) {
		
		if(maze.isConstructed())
			return;
		
		mazeVisibilities.put(maze, true);
		Player player = maze.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {

				for(Location loc : maze.getClip().getBorderBlocks())
					player.sendBlockChange(loc, Constants.MAZE_BORDER, (byte) 0);
				
				for(Vec2 exit : maze.getExits())
					player.sendBlockChange(maze.getClip().getBlockLoc(exit), Constants.MAZE_EXIT, (byte) 0);
				
				if(maze.hasExits())
					player.sendBlockChange(maze.getClip().getBlockLoc(maze.getMainExit()), Constants.MAZE_MAIN_EXIT, (byte) 0);
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}
	
	//hides a maze
	public void hideMaze(Maze maze) {
		
		if(maze.isConstructed())
			return;
		
		mazeVisibilities.put(maze, false);
		Player player = maze.getPlayer();
		
		for(Location loc : maze.getClip().getBorderBlocks())
			player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
	}
	
	//displays single blocks of a clipboard (which might covered by e.g. falling block)
	public void redisplayClipboardBlock(ClipTool clipboard, Location loc) {
		
		Player player = clipboard.getPlayer();
		
		if(clipboard.isVertex(loc.getBlock()))
			sendBlockDelayed(player, loc, Constants.CLIPBOARD_VERTEX);
		
		else
			sendBlockDelayed(player, loc, Constants.CLIPBOARD_BORDER);
	}

	//displays single blocks of a maze
	public void redisplayMazeBlock(Maze maze, Location loc) {
		
		Player player = maze.getPlayer();
		Vec2 locVec = new Vec2(loc);
		
		if(locVec.equals(maze.getMainExit()))
			sendBlockDelayed(player, loc, Constants.MAZE_MAIN_EXIT);
		
		else if(maze.exitsContain(locVec))
			sendBlockDelayed(player, loc, Constants.MAZE_EXIT);
		
		else
			sendBlockDelayed(player, loc, Constants.MAZE_BORDER);
	}
	
	//
	public void displayMazeAction(Maze maze, ClipChange action) {
		
		Player player = maze.getPlayer();
		Clip clip = maze.getClip();
		
		for(Vec2 exit : action.getRemovedExits()) {
			
			player.sendBlockChange(clip.getBlockLoc(exit), Constants.MAZE_BORDER, (byte) 0);
			
			List<Vec2> mazeExits = maze.getExits();
			
			if(exit.equals(maze.getMainExit()) && mazeExits.size() > 1)
				player.sendBlockChange(clip.getBlockLoc(mazeExits.get(mazeExits.size()-2)), Constants.MAZE_MAIN_EXIT, (byte) 0);
		}

		for(Vec2 loc : action.getAddedBorder())
			player.sendBlockChange(action.getBorder(loc), Constants.MAZE_BORDER, (byte) 0);
		
		for(Vec2 loc : action.getRemovedBorder()) {
			System.out.println(loc.toString());
			Location block = action.getBorder(loc);
			player.sendBlockChange(block, block.getBlock().getType(), (byte) 0);
		}
	}
	
	//Displays maze parts that were covered under a clipboard.
	private void redisplayMaze(Maze maze, ClipTool hiddenClipboard) {
		
		Player player = maze.getPlayer();
		
		for(BlockVec vertex : hiddenClipboard.getVertices()) {
			
			if(maze.getClip().isBorderBlock(vertex.getBlock()))
				player.sendBlockChange(vertex.toLocation(), Constants.MAZE_BORDER, (byte) 0);
		}
		
		if(!hiddenClipboard.hasClip())
			return;
		
		for(Location border : hiddenClipboard.getClip().getBorderBlocks()) {
			
			if(maze.getClip().isBorderBlock(border.getBlock()))
				maze.getPlayer().sendBlockChange(border, Constants.MAZE_BORDER, (byte) 0);
		}
	}
	
	public void sendBlockDelayed(Player player, Location loc, Material mat) {

		new BukkitRunnable() {

			@Override
			public void run() {
				player.sendBlockChange(loc, mat, (byte) 0);
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}
	
	public void sendBlocksDelayed(Player player, Collection<Location> locs, Material mat) {
		
		new BukkitRunnable() {
		
			@Override
			public void run() {
				
				for(Location loc : locs)
					player.sendBlockChange(loc, mat, (byte) 0);
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}
}