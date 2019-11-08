package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class AddToMaze extends BasicCommand {

	public AddToMaze(MazeCommand mazeCommand) {
		super("add", null, mazeCommand);
		addAlias("merge");
	}
	
	@Override
	public boolean onExecute(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		
		Maze maze = getStartedMaze(player, false, true);
		
		if(maze == null)
			return false;
		
		ClippingTool clipboard = getCompletedClipboard(player);
		
		if(clipboard == null)
			return false;
		
		ClipAction action = maze.getAddition(clipboard.getClip());

		if(action == null)
			return false;
		
		if(action.getAddedFill().size() == clipboard.getClip().size()) {

			Messages.ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.sendTo(player);
			return false;
		}
		
		Renderer.hideClipboard(clipboard, true);
		clipboard.reset();
		
		maze.processAction(action, true);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
}