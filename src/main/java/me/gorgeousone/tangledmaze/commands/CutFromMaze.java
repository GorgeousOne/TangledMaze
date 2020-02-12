package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.tools.ClipTool;

public class CutFromMaze extends BasicCommand {

	ClipToolHandler clipHandler;
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
		
		if(maze == null)
			return false;
		
		ClipTool clipboard = clipHandler.requireCompletedClipTool(player);
		
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