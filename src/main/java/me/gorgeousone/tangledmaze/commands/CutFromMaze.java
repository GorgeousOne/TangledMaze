package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeChangeFactory;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CutFromMaze extends BasicCommand {
	
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	
	public CutFromMaze(MazeCommand mazeCommand, ClipToolHandler clipHandler, MazeHandler mazeHandler) {
		
		super("cut", null, true, mazeCommand);
		addAlias("remove");
		
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = mazeHandler.getStartedMaze(player, false, true);
		
		if (maze == null)
			return false;
		
		ClipTool clipTool = clipHandler.requireCompletedClipTool(player);
		
		if (clipTool == null)
			return false;
		
		ClipChange clipChange = MazeChangeFactory.createDeletion(maze, clipTool.getClip());
		clipHandler.removeClipTool(player);
		
		if (clipChange == null)
			return false;
		
		mazeHandler.processClipChange(maze, clipChange);
		return true;
	}
}