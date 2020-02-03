package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class CutFromMaze extends BasicCommand {

	public CutFromMaze(MazeCommand mazeCommand) {
		super("cut", null, true, mazeCommand);
		addAlias("remove");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = getStartedMaze(player, false, true);
		
		if(maze == null)
			return false;
		
		ClippingTool clipboard = getCompletedClipboard(player);
		
		if(clipboard == null)
			return false;
		
		ClipAction action = maze.getDeletion(clipboard.getClip());

		Renderer.hideClipboard(clipboard, true);
		clipboard.reset();

		if(action == null)
			return false;

		maze.processAction(action, true);
		Renderer.displayMazeAction(maze, action);
		return true;
	}
}