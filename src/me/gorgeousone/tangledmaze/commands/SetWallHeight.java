package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Constants;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class SetWallHeight {

	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallHeight = 0;
		
		try {
			wallHeight = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(wallHeight < 1) {
			p.sendMessage(ChatColor.RED + "A wall cannot be flatter than 1 block.");
			return;
		}
		
		if(wallHeight > 20) {
			p.sendMessage(Constants.prefix + "In the interests of the noighbours upstairs the wall height is limited to 20 blocks. "
					+ "I am sure they will thank you in return.");
			return;
		}
		
		MazeHandler.getMaze(p).setWallHeight(wallHeight);
		p.sendMessage(Constants.prefix + "Set wall height to " + wallHeight + ".");
	}
}