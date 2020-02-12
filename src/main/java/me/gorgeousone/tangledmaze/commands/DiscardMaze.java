package me.gorgeousone.tangledmaze.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;

public class DiscardMaze extends BasicCommand {

	private ToolHandler toolHandler;
	private MazeHandler mazeHandler;

	public DiscardMaze(MazeCommand mazeCommand, ToolHandler toolHandler, MazeHandler mazeHandler) {
		super("discard", null, true, mazeCommand);

		this.toolHandler = toolHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {

		Player player = (Player) sender;
		
		Renderer.hideMaze(mazeHandler.getMaze(player));
		mazeHandler.setMaze(player, new Maze(player));
		toolHandler.resetToDefaultTool(player);
		return true;
	}
}