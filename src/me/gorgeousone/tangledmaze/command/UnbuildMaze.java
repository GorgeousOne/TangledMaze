package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.MazeHandler;

public class UnbuildMaze extends MazeCommand {
	
	public UnbuildMaze() {
		super("unbuild", "/tangledmaze unbuild", 0, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		Maze maze = MazeHandler.getMaze(player);
		
		if(!maze.isConstructed()) {
			Messages.MESSAGE_NO_MAZE_TO_UNBUILD.sendTo(player);
			return true;
		}
		
		BuildHandler.unbuildMaze(maze);
		Messages.MESSAGE_MAZE_UNBUILDING_STARTED.sendTo(player);
		return true;
	}
}