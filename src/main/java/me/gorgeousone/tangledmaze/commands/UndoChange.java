package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoChange extends BasicCommand {
	
	private MazeHandler mazeHandler;
	
	public UndoChange(MazeCommand mazeCommand, MazeHandler mazeHandler) {
		super("undo", null, true, mazeCommand);
		
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = mazeHandler.getStartedMaze(player, false, true);
		
		if (maze == null)
			return;
		
		if (maze.getActionHistory().isEmpty())
			return;
		
		ClipChange clipChange = maze.getActionHistory().popLastAction().invert();
		mazeHandler.processClipChange(player, maze, clipChange);
		return;
	}
}