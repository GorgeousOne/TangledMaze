package me.gorgeousone.tangledmaze.tools;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class BrushTool extends Tool {
	
	public BrushTool(Player builder) {
		super(builder);
	}		
	
	@Override
	public void interact(Block b, Action a) {

		Maze maze = MazeHandler.getMaze(getPlayer());
		MazeAction brush = null;
		
		//get affected blocks of shaping as a MazeAction
		if(a == Action.RIGHT_CLICK_BLOCK) {
			brush = maze.getReduction(b);
		}else
			brush = maze.getExpansion(b);
		
		if(brush == null)
			getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This is not your maze's outline...");
		else
			maze.processAction(brush, true);
	}
}