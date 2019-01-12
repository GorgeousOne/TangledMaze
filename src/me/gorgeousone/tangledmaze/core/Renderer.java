package me.gorgeousone.tangledmaze.core;

import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.tools.ClippingTool;
import me.gorgeousone.tangledmaze.utils.Constants;
import me.gorgeousone.tangledmaze.utils.MazePoint;

public abstract class Renderer implements Listener {
	
	private static HashMap<ClippingTool, Boolean> clipVisibilities = new HashMap<>();
	private static HashMap<Maze, Boolean> mazeVisibilities = new HashMap<>();
	
	public static void reload() {
		for(ClippingTool selection : clipVisibilities.keySet()) {
			if(isClipboardVisible(selection))
				hideClipboard(selection, false);
		}
		
		for(Maze maze : mazeVisibilities.keySet()) {
			if(isMazeVisible(maze))
				hideMaze(maze);
		}
	}
	
	public static void registerClip(ClippingTool clipboard) {
		
		if(clipboard.getPlayer() == null)
			return;
		
		clipVisibilities.put(clipboard, false);
	}
	
	public static void registerMaze(Maze maze) {

		if(maze.getPlayer() == null)
			return;

		mazeVisibilities.put(maze, false);
	}
	
	public static void unregisterShape(ClippingTool clipboard) {
		clipVisibilities.remove(clipboard);
	}
	
	public static void unregisterMaze(Maze maze) {
		mazeVisibilities.remove(maze);
	}
	
	public static boolean isClipboardVisible(ClippingTool shape) {
		return clipVisibilities.get(shape);
	}
	
	public static boolean isMazeVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	@SuppressWarnings("deprecation")
	public static void showClipboard(ClippingTool clipboard) {
		
		clipVisibilities.put(clipboard, true);
		Player p = clipboard.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				if(clipboard.isComplete()) {
					for(MazePoint point : clipboard.getClip().getBorder()) {
						p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
					}
				}
				
				for(Location vertex : clipboard.getVertices()) {
					p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
				}
				
			}
		}.runTask(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public static void showMaze(Maze maze) {
		
		mazeVisibilities.put(maze, true);
		Player p = maze.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for(MazePoint point : maze.getClip().getBorder()) {
					p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				}
				
				for(Location exit : maze.getExits()) {
					p.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
				}
				
				if(!maze.getExits().isEmpty()) {
					p.sendBlockChange(maze.getExits().get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
				}
			}
		}.runTask(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public static void hideClipboard(ClippingTool clipboard, boolean updateMaze) {
		
		if(!isClipboardVisible(clipboard))
			return;
		
		clipVisibilities.put(clipboard, false);
		Player p = clipboard.getPlayer();
		
		if(clipboard.isComplete()) {
			for(Location point : clipboard.getClip().getBorder()) {
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
			}
		}
		
		for(Location vertex : clipboard.getVertices()) {
			p.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		}
		
		if(updateMaze && MazeHandler.getMaze(p).isStarted() && isMazeVisible(MazeHandler.getMaze(p)))
			refreshMaze(p, clipboard, MazeHandler.getMaze(p));
	}
	
	@SuppressWarnings("deprecation")
	public static void hideMaze(Maze maze) {
		
		if(maze.getPlayer() == null || !isMazeVisible(maze))
			return;
		
		mazeVisibilities.put(maze, false);
		Player p = maze.getPlayer();
		
		for(MazePoint point : maze.getClip().getBorder()) {
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
		}
	}

	@SuppressWarnings("deprecation")
	public static void showMazeAction(Maze maze, MazeAction action) {
		
		if(maze.getPlayer() == null)
			return;
		
		Player p = maze.getPlayer();
		
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
	
	@SuppressWarnings("deprecation")
	public static void updateChunk(Chunk chunk) {
		
		for(Maze maze : mazeVisibilities.keySet()) {
			
			if(!maze.isStarted() || !isMazeVisible(maze) || !maze.getClip().getChunks().contains(chunk))
				continue;
			
			Player p = maze.getPlayer();
			
			//TODO high - maybe find chunk specific blocks that need to be updated
			for(MazePoint point : maze.getClip().getBorder()) {
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
			}
		}
		
		for(ClippingTool clipboard : clipVisibilities.keySet()) {
			
			if(!isClipboardVisible(clipboard) || !clipboard.isComplete())
				continue;

			Player p = clipboard.getPlayer();
			
			for(Location point : clipboard.getClip().getBorder())
				p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
			
			for(Location vertex : clipboard.getVertices())
				p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
		}
	}
	
	//TODO high - find fast way to iterate/compare blocks
	@SuppressWarnings("deprecation")
	private static void refreshMaze(Player p, ClippingTool clipboard, Maze maze) {

		for(Location vertex : clipboard.getVertices()) {
			if(maze.isHighlighted(vertex.getBlock())) {
				p.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
			}
		}

		if(!clipboard.isComplete()) {
			return;
		}
		
		for(MazePoint borderPoint : clipboard.getClip().getBorder()) {
			if(maze.isHighlighted(borderPoint.getBlock())) {
				maze.getPlayer().sendBlockChange(borderPoint, Constants.MAZE_BORDER , (byte) 0);
			}
		}
	}
}