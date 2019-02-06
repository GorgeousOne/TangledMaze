package me.gorgeousone.tangledmaze.tool;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;

public class ExitSettingTool extends Tool {
	
	public ExitSettingTool(Player p) {
		super(p);
	}
	
	@Override
	public void interact(Block clicked, Action interaction) {
		
		Maze maze = MazeHandler.getMaze(getPlayer());
		
//		if(maze.exitsContain(clicked))
//			maze.removeExit(clicked);
//		else
		
		maze.toggleExit(clicked);
	}
}