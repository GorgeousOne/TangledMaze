package me.gorgeousone.tangledmaze.selections;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class ExitSetterSelection extends Selection {
	
	public ExitSetterSelection(Player p) {
		super(p);
	}
	
	public void interact(Block b, Action a) {
		
		Maze maze = MazeHandler.getMaze(getPlayer());
		
		if(maze.canBeExit(b.getLocation()))
			maze.addExit(b.getLocation());
	}
}