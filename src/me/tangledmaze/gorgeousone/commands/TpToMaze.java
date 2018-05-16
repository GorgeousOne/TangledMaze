package me.tangledmaze.gorgeousone.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class TpToMaze {
	
	private MazeHandler mHandler;
	
	public TpToMaze() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}

	public void execute(Player p) {

		if(!p.hasPermission(Constants.mazeTpPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!mHandler.hasMaze(p)) {
			p.sendMessage(ChatColor.RED + "You did not create any any maze to teleport to, yet.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = mHandler.getMaze(p);

		if(maze.contains(p.getLocation())) {
			p.sendMessage(Constants.prefix + "You are already standing inside your maze xD");
			return;
		}
		
		Location target = maze.getFill().get(maze.getChunks().get(0)).get(0).clone();
		target.add(0.5, 1, 0.5);
		target.setDirection(p.getLocation().getDirection());
		
		p.teleport(target);
	}
}