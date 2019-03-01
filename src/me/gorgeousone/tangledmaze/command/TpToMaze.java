package me.gorgeousone.tangledmaze.command;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Messages;

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
	
	public void execute(Player p) {

		if(!p.hasPermission(Constants.MAZE_TP_PERM)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(ChatColor.RED + "You did not create any any maze to teleport to, yet.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);

		if(maze.getClip().contains(new MazePoint(p.getLocation()))) {
			p.sendMessage(Constants.prefix + "You are already standing inside your maze xD");
			return;
		}
		
		MazePoint target = maze.getClip().getBorder().first();
		target.add(0.5, 2, 0.5);
		target.setDirection(p.getLocation().getDirection());
		
		p.teleport(target);
	}
}