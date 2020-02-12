package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handlers.Renderer;

public class TpToMaze extends BasicCommand {

	private MazeHandler mazeHandler;

	public TpToMaze(MazeCommand mazeCommand, MazeHandler mazeHandler) {
		super("teleport", Constants.MAZE_TP_PERM, true, mazeCommand);

		this.mazeHandler = mazeHandler;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = mazeHandler.getStartedMaze(player, false, false);
		
		if(maze == null)
			return false;
		
		Location tpLoc = maze.getClip().getBorderBlocks().iterator().next();
		tpLoc.add(0.5, 2, 0.5);
		tpLoc.setDirection(player.getLocation().getDirection());
		
		player.teleport(tpLoc);
		Renderer.displayMaze(maze);
		return true;
	}
}