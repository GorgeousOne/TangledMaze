package me.gorgeousone.tangledmaze.core;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.MazePoint;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Renderer implements Listener {
	
	private static HashMap<ClippingTool, Boolean> clipVisibilities = new HashMap<>();
	private static HashMap<Maze, Boolean> mazeVisibilities = new HashMap<>();
	
	public static void reload() {

		for (ClippingTool selection : clipVisibilities.keySet()) {
			if (isClipboardVisible(selection)) {
				hideClipboard(selection, false);
			}
		}
		
		for(Maze maze : mazeVisibilities.keySet()) {
			if(isMazeVisible(maze)) {
				hideMaze(maze);
			}
		}
	}
	
	public static void registerClip(ClippingTool clipboard) {
		
		if(clipboard.getPlayer() == null) {
			return;
		}
		
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
		Player player = clipboard.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {

				if(clipboard.isComplete()) {
					for(MazePoint point : clipboard.getClip().getBorder()) {
						player.sendBlockChange(point, Constants.CLIPBOARD_BORDER, (byte) 0);
					}
				}

				for(Location vertex : clipboard.getVertices()) {
					player.sendBlockChange(vertex, Constants.CLIPBOARD_CORNER, (byte) 0);
				}
		//TODO change back to runTask() if you can find out why block click still interferes block change after 1 tick
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}
	
	@SuppressWarnings("deprecation")
	public static void showMaze(Maze maze) {
		
		mazeVisibilities.put(maze, true);
		Player player = maze.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {

				for(MazePoint point : maze.getClip().getBorder()) {
					player.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				}
				
				
				for(Location exit : maze.getExits()) {
					player.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
				}
				
				if(!maze.getExits().isEmpty()) {
					player.sendBlockChange(maze.getExits().get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
				}
			}
		}.runTask(TangledMain.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	public static void hideClipboard(ClippingTool clipboard, boolean updateMaze) {
		
		if(!isClipboardVisible(clipboard)) {
			return;
		}

		clipVisibilities.put(clipboard, false);
		Player player = clipboard.getPlayer();
		
		if(clipboard.isComplete()) {
			for(Location point : clipboard.getClip().getBorder()) {
				player.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
			}
		}
		
		for(Location vertex : clipboard.getVertices()) {
			player.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		}
		
		if(updateMaze && MazeHandler.getMaze(player) != null && isMazeVisible(MazeHandler.getMaze(player)))
			refreshMaze(player, clipboard, MazeHandler.getMaze(player));
	}
	
	@SuppressWarnings("deprecation")
	public static void hideMaze(Maze maze) {
		
		if(!isMazeVisible(maze)) {
			return;
		}
		
		mazeVisibilities.put(maze, false);
		Player player = maze.getPlayer();
		
		for(MazePoint point : maze.getClip().getBorder()) {
			player.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
		}
	}

	@SuppressWarnings("deprecation")
	public static void showMazeAction(Maze maze, ClipAction action) {
		
		Player player = maze.getPlayer();

		for(MazePoint point : action.getRemovedExits()) {
			player.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);

			if(maze.getExits().indexOf(point) == 0 && maze.getExits().size() > 1) {
				player.sendBlockChange(maze.getExits().get(1), Constants.MAZE_MAIN_EXIT, (byte) 0);
			}
		}

		for(Location point : action.getAddedBorder()) {
			player.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		}
		
		for(Location point : action.getRemovedBorder()) {
			player.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
		}
	}
	
	public static void updateChunk(Chunk chunk) {
		
		for(Maze maze : mazeVisibilities.keySet()) {
			
			if(!maze.isStarted() || !isMazeVisible(maze) || !maze.getClip().getChunks().contains(chunk)) {
				continue;
			}
			
			sendBlocksDelayed(maze.getPlayer(), maze.getClip().getBorder(chunk), Constants.MAZE_BORDER);
		}
		
		for(ClippingTool clipboard : clipVisibilities.keySet()) {
			
			if(!isClipboardVisible(clipboard) || !clipboard.isComplete() || !clipboard.getClip().getChunks().contains(chunk)) {
				continue;
			}
			
			Player player = clipboard.getPlayer();
			
			sendBlocksDelayed(player, clipboard.getClip().getBorder(chunk), Constants.CLIPBOARD_BORDER);
			sendBlocksDelayed(player, clipboard.getVertices(), Constants.CLIPBOARD_CORNER);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void refreshMaze(Player player, ClippingTool clipboard, Maze maze) {
		
		for(Location vertex : clipboard.getVertices()) {
			if(maze.isHighlighted(vertex.getBlock())) {
				player.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
			}
		}
		
		if(!clipboard.isComplete()) {
			return;
		}

		HashSet<Chunk> mazeBorderChunks = maze.getClip().getBorderChunks();
		
		for(Chunk clipBorderChunk : clipboard.getClip().getBorderChunks()) {
			
			if(!mazeBorderChunks.contains(clipBorderChunk))
				return;
			
			for(MazePoint borderPoint : clipboard.getClip().getBorder(clipBorderChunk)) {
				if(maze.isHighlighted(borderPoint.getBlock())) {
					maze.getPlayer().sendBlockChange(borderPoint, Constants.MAZE_BORDER , (byte) 0);
				}
			}
		}
	}
	
	public static void sendBlockDelayed(Player player, Location point, Material mat) {

		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				player.sendBlockChange(point, mat, (byte) 0);
			}
		}.runTask(TangledMain.getInstance());
	}
	
	public static void sendBlocksDelayed(Player player, Collection<MazePoint> points, Material mat) {
		
		BukkitRunnable delay = new BukkitRunnable() {
		
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				for(MazePoint point : points) {
					player.sendBlockChange(point, mat, (byte) 0);
				}
			}
		};
		delay.runTask(TangledMain.getInstance());
	}
}