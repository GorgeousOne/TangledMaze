package me.gorgeousone.tangledmaze.command;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.MazePoint;

public class TpToMaze extends MazeCommand {
	
	public TpToMaze() {
		super("teleport", "/tangledmaze teleport", 0, true, Constants.MAZE_TP_PERM, "tp");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		if(!MazeHandler.getMaze(player).isStarted()) {

			Messages.ERROR_MAZE_NOT_STARTED.send(player);
			player.sendMessage("/tangledmaze start");
			return false;
		}
		
		MazePoint target = MazeHandler.getMaze(player).getClip().getBorder().first();

		target.add(0.5, 2, 0.5);
		target.setDirection(player.getLocation().getDirection());
		
		player.teleport(target);
		return true;
	}
}