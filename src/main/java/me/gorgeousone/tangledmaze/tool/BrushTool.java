package me.gorgeousone.tangledmaze.tool;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;

public class BrushTool extends Tool {

	public BrushTool(Player builder) {
		super(builder);
	}		
	
	@Override
	public String getName() {
		return "brush";
	}
	
	@Override
	public void interact(Block clickedBlock, Action interaction) {

		Maze maze = MazeHandler.getMaze(getPlayer());
		ClipAction brushing = null;
		
		if(interaction == Action.RIGHT_CLICK_BLOCK)
			brushing = maze.getErasure(clickedBlock);
		else
			brushing = maze.getExpansion(clickedBlock);
		
		if(brushing != null) {
			
			maze.processAction(brushing, true);
			Renderer.displayMazeAction(maze, brushing);
		}
	}
}