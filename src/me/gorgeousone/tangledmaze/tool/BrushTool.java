package me.gorgeousone.tangledmaze.tool;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;

public class BrushTool extends Tool {

	public BrushTool(Player builder) {
		super(builder);
	}		
	
	@Override
	public String getName() {
		return "brush";
	}
	
	@Override
	public void interact(Block clicked, Action interaction) {

		Maze maze = MazeHandler.getMaze(getPlayer());
		ClipAction brushing = null;
		
		//get affected blocks of shaping as a MazeAction
		if(interaction == Action.RIGHT_CLICK_BLOCK)
			brushing = maze.getErasure(clicked);
		else
			brushing = maze.getExpansion(clicked);
		
		if(brushing == null)
			Messages.ERROR_NO_MAZE_BORDER_CLICKED.send(maze.getPlayer());
		else
			maze.processAction(brushing, true);
	}
}