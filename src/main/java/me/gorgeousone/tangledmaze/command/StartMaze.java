package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.handler.ClipToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.tool.ClipTool;

public class StartMaze extends BasicCommand {

	private ClipToolHandler clipHandler;

	public StartMaze(ClipToolHandler clipHandler, MazeCommand mazeCommand) {
		super("start", null, true, mazeCommand);

		this.clipHandler = clipHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {

		Player player = (Player) sender;
		ClipTool clipboard = getCompletedClipboard(player);
		
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
		clipHandler.removeClipTool(player);
		return true;
	}
}