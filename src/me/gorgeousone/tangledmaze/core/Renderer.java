package me.gorgeousone.tangledmaze.core;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.util.Vec2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.Collection;
import java.util.HashMap;

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
		clipVisibilities.put(clipboard, false);
	}
	
	public static void registerMaze(Maze maze) {
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
	public static void displayClipboard(ClippingTool clipboard) {

		clipVisibilities.put(clipboard, true);
		Player player = clipboard.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {

				if(clipboard.isComplete()) {
					for(Location loc : clipboard.getClip().getBorderBlocks())
						player.sendBlockChange(loc, Constants.CLIPBOARD_BORDER, (byte) 0);
				}

				for(Location vertex : clipboard.getVertices())
					player.sendBlockChange(vertex, Constants.CLIPBOARD_CORNER, (byte) 0);
			}
		}.runTask(TangledMain.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	public static void hideClipboard(ClippingTool clipboard, boolean updateMaze) {
		
		clipVisibilities.put(clipboard, false);
		Player player = clipboard.getPlayer();
		
		if(clipboard.isComplete()) {
			
			for(Location loc : clipboard.getClip().getBorderBlocks())
				player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
		}
		
		for(Location vertex : clipboard.getVertices())
			player.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		
		if(!updateMaze)
			return;
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.isStarted() && isMazeVisible(maze))
			redisplayMaze(maze, clipboard);
	}
	
	@SuppressWarnings("deprecation")
	public static void displayMaze(Maze maze) {
		
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
					player.sendBlockChange(maze.getClip().getLocation(exit), Constants.MAZE_EXIT, (byte) 0);
				
				if(!maze.getExits().isEmpty())
					player.sendBlockChange(maze.getClip().getLocation(maze.getMainExit()), Constants.MAZE_MAIN_EXIT, (byte) 0);
			}
		}.runTask(TangledMain.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	public static void hideMaze(Maze maze) {
		
		if(maze.isConstructed() || !isMazeVisible(maze))
			return;
		
		mazeVisibilities.put(maze, false);
		Player player = maze.getPlayer();
		
		for(Location loc : maze.getClip().getBorderBlocks())
			player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
	}
	
	public static void redisplayMazeBorder(Maze maze, Block block) {
		
		Player player = maze.getPlayer();
		
		Vec2 locVec = new Vec2(block);
		Location loc = block.getLocation();
		
		if(locVec.equals(maze.getMainExit()))
			sendBlockDelayed(player, loc, Constants.MAZE_MAIN_EXIT);
		
		else if(maze.exitsContain(locVec))
			sendBlockDelayed(player, loc, Constants.MAZE_EXIT);
		
		else
			sendBlockDelayed(player, loc, Constants.MAZE_BORDER);
	}
	
	@SuppressWarnings("deprecation")
	public static void displayMazeAction(Maze maze, ClipAction action) {
		
		Player player = maze.getPlayer();
		Clip clip = maze.getClip();
		
		for(Vec2 exit : action.getRemovedExits()) {
			
			player.sendBlockChange(clip.getLocation(exit), Constants.MAZE_BORDER, (byte) 0);

			if(exit.equals(maze.getMainExit()) && maze.getExits().size() > 1)
				player.sendBlockChange(clip.getLocation(maze.getExits().get(maze.getExits().size()-2)), Constants.MAZE_MAIN_EXIT, (byte) 0);
		}

		for(Vec2 loc : action.getAddedBorder())
			player.sendBlockChange(action.getBorder(loc), Constants.MAZE_BORDER, (byte) 0);
		
		for(Vec2 loc : action.getRemovedBorder()) {
			
			Location block = action.getBorder(loc);
			player.sendBlockChange(block, block.getBlock().getType(), (byte) 0);
			
		}
	}
	
//	public static void updateChunk(Chunk chunk) {
//		
//		for(Maze maze : mazeVisibilities.keySet()) {
//			
//			if(!maze.isStarted() || !isMazeVisible(maze) || !maze.getClip().getChunks().contains(chunk))
//				continue;
//			
//			sendBlocksDelayed(maze.getPlayer(), maze.getClip().getBorderBlocks(chunk), Constants.MAZE_BORDER);
//		}
//		
//		for(ClippingTool clipboard : clipVisibilities.keySet()) {
//			
//			if(!isClipboardVisible(clipboard) || !clipboard.isComplete() || !clipboard.getClip().getChunks().contains(chunk))
//				continue;
//			
//			Player player = clipboard.getPlayer();
//			
//			sendBlocksDelayed(player, clipboard.getClip().getBorderBlocks(chunk), Constants.CLIPBOARD_BORDER);
//			sendBlocksDelayed(player, clipboard.getVertices(), Constants.CLIPBOARD_CORNER);
//		}
//	}
	
//	private static void displayAction(Maze maze, ClipAction action) {
//		
//		Player player = maze.getPlayer();
//		
//		for(Vec2 border : action.getAddedBorder()) {
//			
//			
//		}
//	}
	
	//Displays maze parts that were covered under a clipboard.
	@SuppressWarnings("deprecation")
	private static void redisplayMaze(Maze maze, ClippingTool hiddenClipboard) {
		
		Player player = maze.getPlayer();
		
		for(Location vertex : hiddenClipboard.getVertices()) {
			
			if(maze.getClip().isBorderBlock(vertex.getBlock()))
				player.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
		}
		
		if(!hiddenClipboard.isComplete())
			return;
		
		for(Location border : hiddenClipboard.getClip().getBorderBlocks()) {
			
			if(maze.getClip().isBorderBlock(border.getBlock()))
				maze.getPlayer().sendBlockChange(border, Constants.MAZE_BORDER , (byte) 0);
		}
	}
	
	public static void sendBlockDelayed(Player player, Location loc, Material mat) {

		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				player.sendBlockChange(loc, mat, (byte) 0);
			}
		}.runTask(TangledMain.getInstance());
	}
	
	public static void sendBlocksDelayed(Player player, Collection<Location> locs, Material mat) {
		
		new BukkitRunnable() {
		
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				for(Location loc : locs)
					player.sendBlockChange(loc, mat, (byte) 0);
			}
		}.runTask(TangledMain.getInstance());
	}
}