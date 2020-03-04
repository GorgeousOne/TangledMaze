package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		
		if (maze == null)
			return false;
		
		if (maze.getActionHistory().isEmpty())
			return false;
		
		ClipChange clipChange = maze.getActionHistory().popLastAction().invert();
		mazeHandler.processClipChange(maze, clipChange);
		return true;
	}
}