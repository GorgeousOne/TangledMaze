package me.gorgeousone.tangledmaze.selections;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class ExitSetter extends Selection {
	
	public ExitSetter(Player p) {
		super(p);
	}
	
	public void interact(Block b, Action a) {
		
		Maze maze = MazeHandler.getMaze(getPlayer());
		Location clicked = b.getLocation();
		
		if(maze.exitsContain(clicked)) {
			
			maze.removeExit(clicked);
			
		}else {
			
			maze.addExit(clicked);
		}
	}
}