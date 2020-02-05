package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.handler.ClipToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClipTool;

public class CutFromMaze extends BasicCommand {

	ClipToolHandler clipHandler;

	public CutFromMaze(ClipToolHandler clipHandler, MazeCommand mazeCommand) {

		super("cut", null, true, mazeCommand);
		addAlias("remove");

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
		
		ClipAction action = maze.getDeletion(clipboard.getClip());

		//TODO make cliphandler handle cliptool rendering
		Renderer.hideClipboard(clipboard, true);
		clipHandler.removeClipTool(player);

		if(action == null)
			return false;

		maze.processAction(action, true);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
}