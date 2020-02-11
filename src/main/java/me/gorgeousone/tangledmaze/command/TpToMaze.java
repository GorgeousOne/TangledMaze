package me.gorgeousone.tangledmaze.command;

import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.util.Vec2;

public class TpToMaze extends BasicCommand {
	
	public TpToMaze(MazeCommand mazeCommand) {
		super("teleport", Constants.MAZE_TP_PERM, true, mazeCommand);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = getStartedMaze(player, false, false);
		
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