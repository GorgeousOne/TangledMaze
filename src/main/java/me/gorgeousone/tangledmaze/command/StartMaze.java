package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClippingTool;

public class StartMaze extends BasicCommand {
	
	public StartMaze(MazeCommand mazeCommand) {
		super("start", null, true, mazeCommand);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {

		Player player = (Player) sender;
		ClippingTool clipboard = getCompletedClipboard(player);
		
		if(clipboard == null)
			return false;
		
		Renderer.hideClipboard(clipboard, false);
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.isConstructed()) {

			maze = new Maze(player).setClip(clipboard.getClip());
			MazeHandler.setMaze(player, maze);
			
		}else {
			Renderer.hideMaze(maze);
			maze.setClip(clipboard.getClip());
		}
		
		Renderer.displayMaze(maze);
		clipboard.reset();
		return true;
	}
}