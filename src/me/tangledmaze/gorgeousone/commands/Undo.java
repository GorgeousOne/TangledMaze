package me.tangledmaze.gorgeousone.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class Undo {

	@SuppressWarnings("unused")
	private SelectionHandler handler;
	
	public Undo() {
		handler = TangledMain.getPlugin().getSelectionHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		try {
			
		} catch (Exception e) {
			if(e instanceof IllegalArgumentException)
				p.sendMessage(ChatColor.RED + "");
		}
	}
}