package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeChangeFactory;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.tools.ToolType;
import me.gorgeousone.tangledmaze.utils.RenderUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class stores tools in relation to players.
 * Listeners and commands can get information about what kind of tool a player is using.
 * There is an unsafe shortcut method for getting clipboards :)
 */
//TODO make a singelton out of this? Create one instance handed around by TangledMain
public class ToolHandler {
	
	private JavaPlugin plugin;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	
	private Map<UUID, ToolType> playersTools;
	
	public ToolHandler(JavaPlugin plugin,
	                   ClipToolHandler clipHandler,
	                   MazeHandler mazeHandler) {
		
		this.plugin = plugin;
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
		
		playersTools = new HashMap<>();
	}
	
	public ToolType getToolType(Player player) {
		
		if (!player.hasPermission(Constants.BUILD_PERM))
			return null;
		
		UUID uuid = player.getUniqueId();
		
		if (!playersTools.containsKey(uuid))
			setToolType(player, ToolType.CLIP_TOOL);
		
		return playersTools.get(player.getUniqueId());
	}
	
	public boolean setToolType(Player player, ToolType toolType) {
		return playersTools.put(player.getUniqueId(), toolType) != toolType;
	}
	
	public void removePlayer(Player player) {
		
		if (getToolType(player) == ToolType.CLIP_TOOL)
			clipHandler.removePlayer(player);
		
		playersTools.remove(player.getUniqueId());
	}
	
	public void resetToDefaultTool(Player player) {
		
		if (clipHandler.hasClipTool(player))
			clipHandler.setClipShape(player, ClipShape.RECTANGLE);
		else
			clipHandler.setClipTool(player, new ClipTool(player, ClipShape.RECTANGLE));
		
		setToolType(player, ToolType.CLIP_TOOL);
	}
	
	public void handleToolInteraction(Player player, Block clickedBlock, Action action) {
		
		switch (getToolType(player)) {
			
			case CLIP_TOOL:
				clipHandler.handleClipInteraction(player, clickedBlock);
				break;
			
			case BRUSH_TOOL:
				
				brushMaze(player, clickedBlock, action);
				break;
			
			case EXIT_SETTER:
				setMazeExit(player, clickedBlock);
				break;
		}
	}
	
	private void brushMaze(Player player, Block clickedBlock, Action action) {
		
		Maze maze = mazeHandler.getMaze(player);
		ClipChange brushing;
		
		brushing = action == Action.RIGHT_CLICK_BLOCK ?
				MazeChangeFactory.createErasure(maze, clickedBlock) :
				MazeChangeFactory.createExpansion(maze, clickedBlock);
		
		if (brushing != null)
			mazeHandler.processClipChange(player, maze, brushing);
	}
	
	private void setMazeExit(Player player, Block clickedBlock) {
		
		Maze maze = mazeHandler.getMaze(player);
		
		if (!maze.getClip().isBorderBlock(clickedBlock))
			return;
		
		if (maze.isExit(clickedBlock)) {
			
			maze.removeExit(clickedBlock);
			RenderUtils.sendBlockDelayed(player, clickedBlock.getLocation(), Constants.MAZE_BORDER, plugin);
			
			if (maze.hasExits())
				RenderUtils.sendBlockDelayed(player, maze.getClip().getBlockLoc(maze.getEntrance()), Constants.MAZE_ENTRANCE, plugin);
			
		} else if (maze.canBeExit(clickedBlock)) {
			
			if (maze.hasExits())
				RenderUtils.sendBlockDelayed(player, maze.getClip().getBlockLoc(maze.getEntrance()), Constants.MAZE_EXIT, plugin);
			
			maze.addExit(clickedBlock);
			RenderUtils.sendBlockDelayed(player, clickedBlock.getLocation(), Constants.MAZE_ENTRANCE, plugin);
			
		} else
			RenderUtils.sendBlockDelayed(player, clickedBlock.getLocation(), Constants.MAZE_BORDER, plugin);
	}
}