package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class SetMazeHeight {

	private MazeHandler mHandler;
	
	public SetMazeHeight() {
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
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" could not be identified as an integer.");
			return;
		}
		
		if(mazeHeight < 1) {
			p.sendMessage(ChatColor.RED + "A maze cannot be flatter than 1 block.");
			return;
		}
		
		if(mazeHeight > 20) {
			p.sendMessage(Constants.prefix + "For reasons of safety we do not recommend to build mazes taller than 20 blocks.");
			return;
		}
		
		mHandler.setMazeHeight(p, mazeHeight);
		p.sendMessage(Constants.prefix + "Set maze building height to " + mazeHeight + ".");
	}
}