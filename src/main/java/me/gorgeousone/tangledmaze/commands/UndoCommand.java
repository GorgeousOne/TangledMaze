package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;

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

		ClipChange clipChange = maze.getActionHistory().popLastAction().invert();
		mazeHandler.processClipChange(maze, clipChange);
		return true;
	}
}