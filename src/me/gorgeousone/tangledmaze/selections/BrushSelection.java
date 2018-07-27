package me.gorgeousone.tangledmaze.selections;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class BrushSelection extends Selection {
	
	public BrushSelection(Player builder) {
		super(builder);
	}		
	
	public void interact(Block b, Action a) {

		Maze maze = MazeHandler.getMaze(builder);
		MazeAction brush = null;
		
		//get affected blocks of shaping as a MazeAction
		if(a == Action.RIGHT_CLICK_BLOCK) {
			brush = maze.reduce(b);
		}else
			brush = maze.enlarge(b);
		
		//if the maze was shaped somehow, call an event
		if(brush != null) {
			
//			MazeShapeEvent brushing = new MazeShapeEvent(maze, brush);
//			Bukkit.getPluginManager().callEvent(brushing);
			
		}else if(Math.random() < 1/3d)
			builder.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This is not your maze's outline...");
	}
}