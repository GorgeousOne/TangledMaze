package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.tools.ClipTool;

public class StartMaze extends BasicCommand {

	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;

	public StartMaze(MazeCommand mazeCommand, ClipToolHandler clipHandler, MazeHandler mazeHandler) {
		super("start", null, true, mazeCommand);

		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {

		Player player = (Player) sender;
		ClipTool clipboard = clipHandler.requireCompletedClipTool(player);
		
		if(clipboard == null)
			return false;
		
		Renderer.hideClipboard(clipboard, false);
		Maze maze = mazeHandler.getMaze(player);
		
		if(maze.isConstructed()) {

			maze = new Maze(player).setClip(clipboard.getClip());
			mazeHandler.setMaze(player, maze);
			
		}else {
			Renderer.hideMaze(maze);
			maze.setClip(clipboard.getClip());
		}
		
		Renderer.displayMaze(maze);
		clipHandler.removeClipTool(player);
		return true;
	}
}