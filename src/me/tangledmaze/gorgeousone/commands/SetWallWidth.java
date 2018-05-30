package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class SetWallWidth {

	private MazeHandler mHandler;
	
	public SetWallWidth() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallWidth = 0;
		
		try {
			wallWidth = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(wallWidth < 1) {
			p.sendMessage(ChatColor.RED + "A wall cannot be thinner than 1 block.");
			return;
		}
		
		if(wallWidth > 10) {
			p.sendMessage(Constants.prefix + "In order to not exclude Mexicans the wall thickness is limited to 10 blocks. "
					+ "In case you are a preseident you may look for another plugin.");
			return;
		}
		
		mHandler.setMazeWallWidth(p, wallWidth);
		p.sendMessage(Constants.prefix + "Set wall width to " + wallWidth + ".");
	}
}