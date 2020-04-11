package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartMaze extends BasicCommand {
	
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	
	public StartMaze(MazeCommand mazeCommand, ClipToolHandler clipHandler, MazeHandler mazeHandler) {
		super("start", null, true, mazeCommand);
		
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		ClipTool clipTool = clipHandler.requireCompletedClipTool(player);
		
		if (clipTool == null)
			return;
		
		mazeHandler.setMaze(player, new Maze(player.getWorld()).setClip(clipTool.getClip()));
		clipHandler.removeClipTool(player);
	}
}