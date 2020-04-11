package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends BasicCommand {
	
	private MazeHandler mazeHandler;
	
	public TeleportCommand(MazeCommand mazeCommand, MazeHandler mazeHandler) {
		super("teleport", Constants.MAZE_TP_PERM, true, mazeCommand);
		
		this.mazeHandler = mazeHandler;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = mazeHandler.getStartedMaze(player, false, false);
		
		if (maze == null)
			return;
		
		Clip mazeClip = maze.getClip();
		Vec2 firstBorder = mazeClip.getBorder().iterator().next();
		Location tpPoint = mazeClip.getBlockLoc(firstBorder);
		
		tpPoint.add(0.5, 2, 0.5);
		tpPoint.setDirection(player.getLocation().getDirection());
		
		player.teleport(tpPoint);
		mazeHandler.displayMazeOf(player);
		return;
	}
}