package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.handler.ClipToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClipTool;

public class AddToMaze extends BasicCommand {

	private ClipToolHandler clipHandler;

	public AddToMaze(ClipToolHandler clipHandler, MazeCommand mazeCommand) {
		super("add", null, true, mazeCommand);
		addAlias("merge");

		this.clipHandler = clipHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		
		Maze maze = getStartedMaze(player, false, true);
		
		if(maze == null)
			return false;
		
		ClipTool clipboard = getCompletedClipboard(player);
		
		if(clipboard == null)
			return false;
		
		ClipAction action = maze.getAddition(clipboard.getClip());

		if(action == null)
			return false;
		
		if(action.getAddedFill().size() == clipboard.getClip().size()) {
			Messages.ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.sendTo(player);
			return false;
		}

		//TODO make cliphandler handle cliptool rendering
		Renderer.hideClipboard(clipboard, true);
		clipHandler.removeClipTool(player);

		maze.processAction(action, true);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
}