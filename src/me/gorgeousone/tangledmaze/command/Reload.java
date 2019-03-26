package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;

public class Reload extends MazeCommand {
	
	public Reload() {
		super("reload", "/tangledmaze reload", 0, false, Constants.RELOAD_PERM, "rl");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		TangledMain.getInstance().reloadPlugin();
		return true;
	}
}