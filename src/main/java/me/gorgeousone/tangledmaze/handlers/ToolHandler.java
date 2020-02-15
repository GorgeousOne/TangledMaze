package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeChangeFactory;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.tools.ToolType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Collection;
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

	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;

	private Map<UUID, ToolType> playersTools = new HashMap<>();
	
	public ToolHandler(ClipToolHandler clipHandler, MazeHandler mazeHandler) {
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	

	//	public boolean hasClipboard(Player player) {
	//		return
	//			tools.containsKey(player.getUniqueId()) &&
	//			tools.get(player.getUniqueId()) instanceof ClipTool;
	//	}

	public ToolType getToolType(Player player) {

		if (!player.hasPermission(Constants.BUILD_PERM))
			return null;

		UUID uuid = player.getUniqueId();

		if (!playersTools.containsKey(uuid))
			setToolType(player, ToolType.CLIP_TOOL);

		return playersTools.get(player.getUniqueId());
	}

	public Collection<ToolType> getPlayersTools() {
		return playersTools.values();
	}

	//	public ClipTool getClipboard(Player p) {
	//		Tool clipboard = tools.get(p.getUniqueId());
	//		return clipboard instanceof ClipTool ? (ClipTool) clipboard : null;
	//	}

	public boolean setToolType(Player player, ToolType toolType) {
		return playersTools.put(player.getUniqueId(), toolType) != toolType;
	}

	//	TODO check how to replace resetToDefaultTool correctly

	public void resetToDefaultTool(Player player) {

		if (clipHandler.hasClipTool(player))
			clipHandler.setClipShape(player, ClipShape.RECTANGLE);
		else
			clipHandler.setClipTool(player, new ClipTool(player, ClipShape.RECTANGLE));

		setToolType(player, ToolType.CLIP_TOOL);
	}

	public void removeTool(Player player) {

		//		if(hasClipboard(player))
		//			renderer.unregisterShape(getClipboard(player));

		playersTools.remove(player.getUniqueId());
	}

	public void handleToolInteraction(Player player, Block clickedBlock, Action action) {

		switch (getToolType(player)) {
			
			case CLIP_TOOL:
				clipHandler.handleClipInteraction(player, clickedBlock);
				break;
				
			case BRUSH_TOOL:
				
				brushMaze(mazeHandler.getMaze(player), clickedBlock, action);
				break;
				
			case EXIT_SETTER:
				player.sendMessage("need to code this first nigga");
				break;
		}
	}
	
	private void brushMaze(Maze maze, Block clickedBlock, Action action) {
		
		ClipChange brushing;
		
		brushing = action == Action.RIGHT_CLICK_BLOCK ?
				MazeChangeFactory.createErasure(maze, clickedBlock) :
				MazeChangeFactory.createExpansion(maze, clickedBlock);
		
		if (brushing != null)
			mazeHandler.processClipChange(maze, brushing);
	}
	
	private void setMazeExit() {
	
	}
	
//	public void interact(Block clickedBlock, Action interaction) {
//
//		Maze maze = mazeHandler.getMaze(getPlayer());
//
//		if (!maze.getClip().isBorderBlock(clickedBlock))
//			return;
//
//		if (maze.isExit(clickedBlock)) {
//
//			maze.removeExit(clickedBlock);
//			renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_BORDER);
//
//			if (maze.hasExits())
//				renderer.sendBlockDelayed(getPlayer(), maze.getClip().getBlockLoc(maze.getMainExit()), Constants.MAZE_MAIN_EXIT);
//
//		} else if (maze.canBeExit(clickedBlock)) {
//
//			if (maze.hasExits())
//				renderer.sendBlockDelayed(getPlayer(), maze.getClip().getBlockLoc(maze.getMainExit()), Constants.MAZE_EXIT);
//
//			maze.addExit(clickedBlock);
//			renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_MAIN_EXIT);
//
//		} else
//			renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_BORDER);
//	}
}