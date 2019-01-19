package me.gorgeousone.tangledmaze.tool;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeAction;
import me.gorgeousone.tangledmaze.maze.MazeHandler;

public class BrushTool extends Tool {
	
	public BrushTool(Player builder) {
		super(builder);
	}		
	
	@Override
	public void interact(Block clicked, Action interaction) {

		Maze maze = MazeHandler.getMaze(getPlayer());
		MazeAction brush = null;
		
		//get affected blocks of shaping as a MazeAction
		if(interaction == Action.RIGHT_CLICK_BLOCK) {
			brush = maze.getReduction(clicked);
		}else
			brush = maze.getExpansion(clicked);
		
		if(brush == null)
			getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This is not your maze's outline...");
		else
			maze.processAction(brush, true);
	}
}