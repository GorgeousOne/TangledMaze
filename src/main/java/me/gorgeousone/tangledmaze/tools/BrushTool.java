package me.gorgeousone.tangledmaze.tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;

public class BrushTool extends Tool {

	//TODO check how to replace MazeHandler instance with parameters in methods
	private MazeHandler mazeHandler;

	public BrushTool(Player player, MazeHandler mazeHandler) {
		super(player);
		this.mazeHandler = mazeHandler;
	}

	@Override
	public String getName() {
		return "brush";
	}

	@Override
	public void interact(Block clickedBlock, Action interaction) {

		Maze maze = mazeHandler.getMaze(getPlayer());
		ClipChange brushing;

		brushing = interaction == Action.RIGHT_CLICK_BLOCK ?
				maze.getErasure(clickedBlock) :
				maze.getExpansion(clickedBlock);

		if (brushing != null)
			mazeHandler.processClipChange(maze, brushing);
	}
}