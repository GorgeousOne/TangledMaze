package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class SetWallHeight {

	private MazeHandler mHandler;
	
	public SetWallHeight() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int mazeHeight = 0;
		
		try {
			mazeHeight = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(mazeHeight < 1) {
			p.sendMessage(ChatColor.RED + "A wall cannot be flatter than 1 block.");
			return;
		}
		
		if(mazeHeight > 20) {
			p.sendMessage(Constants.prefix + "In the interests of the noighbours upstairs the wall height is limited to 20 blocks. "
					+ "I am sure they will thank you in return.");
			return;
		}
		
		mHandler.setMazeWallHeight(p, mazeHeight);
		p.sendMessage(Constants.prefix + "Set wall height to " + mazeHeight + ".");
	}
}