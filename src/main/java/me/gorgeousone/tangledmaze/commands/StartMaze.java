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
	private Renderer renderer;

	public StartMaze(MazeCommand mazeCommand, ClipToolHandler clipHandler, MazeHandler mazeHandler, Renderer renderer) {
		super("start", null, true, mazeCommand);

		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
		this.renderer = renderer;
	}

	//TODO unify the way a maze is created from a clip
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {

		Player player = (Player) sender;
		ClipTool clipboard = clipHandler.requireCompletedClipTool(player);
		
		if(clipboard == null)
			return false;
		
		Maze maze = mazeHandler.getMaze(player);
		
		if(maze.isConstructed()) {

			maze = new Maze(player).setClip(clipboard.getClip());
			mazeHandler.setMaze(player, maze);
			
		}else {
			renderer.hideMaze(maze);
			maze.setClip(clipboard.getClip());
		}

		clipHandler.removeClipTool(player);
		renderer.displayMaze(maze);
		return true;
	}
}