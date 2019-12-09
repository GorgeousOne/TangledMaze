package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handler.Renderer;

public class UndoCommand extends BasicCommand {

	public UndoCommand(MazeCommand mazeCommand) {
		super("undo", null, true, mazeCommand);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = getStartedMaze(player, false, true);

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