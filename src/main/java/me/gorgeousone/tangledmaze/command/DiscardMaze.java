package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;

public class DiscardMaze extends BasicCommand {
	
	public DiscardMaze(MazeCommand mazeCommand) {
		super("discard", null, mazeCommand);
	}
	
	@Override
	public boolean onExecute(CommandSender sender, String[] arguments) {

		Player player = (Player) sender;
		
		Renderer.hideMaze(MazeHandler.getMaze(player));
		MazeHandler.setMaze(player, new Maze(player));
		ToolHandler.resetToDefaultTool(player);
		return true;
	}
}