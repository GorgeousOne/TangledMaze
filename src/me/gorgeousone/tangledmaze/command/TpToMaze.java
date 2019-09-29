package me.gorgeousone.tangledmaze.command;

import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commandapi.command.BasicCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.util.Vec2;

public class TpToMaze extends BasicCommand {
	
	public TpToMaze(MazeCommand mazeCommand) {
		super("teleport", Constants.MAZE_TP_PERM, mazeCommand);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		Maze maze = getStartedMaze(player, false, false);
		
		if(maze == null)
			return false;
		
		Vec2 firstLoc = ((TreeSet<Vec2>) maze.getClip().getBorder()).first();
		
		Location tpLoc = maze.getClip().getLocation(firstLoc);
		tpLoc.add(0.5, 2, 0.5);
		tpLoc.setDirection(player.getLocation().getDirection());
		
		player.teleport(tpLoc);
		Renderer.displayMaze(maze);
		return true;
	}
}