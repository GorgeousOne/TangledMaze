package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.tools.ClipTool;

public class AddToMaze extends BasicCommand {

	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;

	public AddToMaze( MazeCommand mazeCommand, ClipToolHandler clipHandler, MazeHandler mazeHandler) {
		super("add", null, true, mazeCommand);
		addAlias("merge");

		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		
		Maze maze = mazeHandler.getStartedMaze(player, false, true);
		
		if(maze == null)
			return false;
		
		ClipTool clipboard = clipHandler.requireCompletedClipTool(player);
		
		if(clipboard == null)
			return false;
		
		ClipChange clipChange = maze.getAddition(clipboard.getClip());

		if(clipChange == null)
			return false;
		
		if(clipChange.getAddedFill().size() == clipboard.getClip().size()) {
			Messages.ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.sendTo(player);
			return false;
		}

		//TODO make cliphandler handle cliptool rendering
		clipHandler.removeClipTool(player);

		mazeHandler.processClipChange(maze, clipChange);
		return true;
	}
}