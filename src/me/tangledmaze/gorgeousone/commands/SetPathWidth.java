package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class SetPathWidth {

	private MazeHandler mHandler;
	
	public SetPathWidth() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int pathWidth = 0;
		
		try {
			pathWidth = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(pathWidth < 1) {
			p.sendMessage(ChatColor.RED + "A path cannot be thinner than 1 block.");
			return;
		}
		
		if(pathWidth > 10) {
			p.sendMessage(Constants.prefix + "So that grandma can still path alone the width is limited to 10 blocks. "
					+ "There will not always be a handsome guy like you around to help her.");
			return;
		}
		
		mHandler.setMazePathWidth(p, pathWidth);
		p.sendMessage(Constants.prefix + "Set path width to " + pathWidth + ".");
	}
}