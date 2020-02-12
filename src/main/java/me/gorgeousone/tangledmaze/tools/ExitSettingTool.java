package me.gorgeousone.tangledmaze.tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;

public class ExitSettingTool extends Tool {

	//TODO check how to replace MazeHandler instance with parameters in methods
	private MazeHandler mazeHandler;

	public ExitSettingTool(Player player, MazeHandler mazeHandler) {
		super(player);
		this.mazeHandler = mazeHandler;
	}

	@Override
	public String getName() {
		return "exit setter";
	}
	
	public void interact(Block clickedBlock, Action interaction) {
		
		Maze maze = mazeHandler.getMaze(getPlayer());
		
		if(!maze.getClip().isBorderBlock(clickedBlock))
			return;
		
		if(maze.isExit(clickedBlock)) {
			
			maze.removeExit(clickedBlock);
			Renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_BORDER);

			if(maze.hasExits())
				Renderer.sendBlockDelayed(getPlayer(), maze.getClip().getBlockLoc(maze.getMainExit()), Constants.MAZE_MAIN_EXIT);
			
		}else if(maze.canBeExit(clickedBlock)) {
			
			if(maze.hasExits())
				Renderer.sendBlockDelayed(getPlayer(), maze.getClip().getBlockLoc(maze.getMainExit()), Constants.MAZE_EXIT);
			
			maze.addExit(clickedBlock);
			Renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_MAIN_EXIT);

		}else
			Renderer.sendBlockDelayed(getPlayer(), clickedBlock.getLocation(), Constants.MAZE_BORDER);
	}
}