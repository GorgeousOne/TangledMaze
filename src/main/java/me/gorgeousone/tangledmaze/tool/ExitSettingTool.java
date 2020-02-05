package me.gorgeousone.tangledmaze.tool;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;

public class ExitSettingTool extends Tool {
	
	public ExitSettingTool(Player builder) {
		super(builder);
	}
	
	@Override
	public String getName() {
		return "exit setter";
	}
	
	public void interact(Block clickedBlock, Action interaction) {
		
		Maze maze = MazeHandler.getMaze(getPlayer());
		
		if(!maze.getClip().isBorderBlock(clickedBlock))
			return;
		
		if(maze.isExit(clickedBlock)) {
			
			maze.removeExit(clickedBlock);
			Renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_BORDER);

			if(maze.hasExits())
				Renderer.sendBlockDelayed(getPlayer(), maze.getClip().getLocation(maze.getMainExit()), Constants.MAZE_MAIN_EXIT);
			
		}else if(maze.canBeExit(clickedBlock)) {
			
			if(maze.hasExits())
				Renderer.sendBlockDelayed(getPlayer(), maze.getClip().getLocation(maze.getMainExit()), Constants.MAZE_EXIT);
			
			maze.addExit(clickedBlock);
			Renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_MAIN_EXIT);

		}else
			Renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_BORDER);
	}
}