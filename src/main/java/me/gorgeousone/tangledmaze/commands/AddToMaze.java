package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeChangeFactory;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddToMaze extends BasicCommand {
	
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	
	public AddToMaze(MazeCommand mazeCommand, ClipToolHandler clipHandler, MazeHandler mazeHandler) {
		
		super("add", null, true, mazeCommand);
		addAlias("merge");
		
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		
		Maze maze = mazeHandler.getStartedMaze(player, false, true);
		
		if (maze == null)
			return;
		
		ClipTool clipTool = clipHandler.requireCompletedClipTool(player);
		
		if (clipTool == null)
			return;
		
		ClipChange clipChange = MazeChangeFactory.createAddition(maze, clipTool.getClip());
		
		if (clipChange == null)
			return;
		
		if (clipChange.getAddedFill().size() == clipTool.getClip().size()) {
			Messages.ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.sendTo(player);
			return;
		}
		
		clipHandler.removeClipTool(player);
		mazeHandler.processClipChange(player, maze, clipChange);
		return;
	}
}