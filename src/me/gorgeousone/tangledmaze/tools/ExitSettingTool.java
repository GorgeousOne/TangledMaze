package me.gorgeousone.tangledmaze.tools;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class ExitSettingTool extends Tool {
	
	public ExitSettingTool(Player p) {
		super(p);
	}
	
	@Override
	public void interact(Block b, Action a) {
		
		Maze maze = MazeHandler.getMaze(getPlayer());
		Location clicked = b.getLocation();
		
		if(maze.exitsContain(clicked))
			maze.removeExit(clicked);
		else
			maze.addExit(clicked);
	}
}