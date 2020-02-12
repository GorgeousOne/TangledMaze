package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handlers.Renderer;

public class UndoCommand extends BasicCommand {

	private MazeHandler mazeHandler;

	public UndoCommand(MazeCommand mazeCommand, MazeHandler mazeHandler) {
		super("undo", null, true, mazeCommand);

		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = mazeHandler.getStartedMaze(player, false, true);

		if(maze == null)
			return false;

		if(maze.getActionHistory().isEmpty())
			return false;

		ClipAction action = maze.getActionHistory().popLastAction().invert();
		maze.processAction(action, false);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
}