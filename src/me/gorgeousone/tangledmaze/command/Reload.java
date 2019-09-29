package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;


import me.gorgeousone.tangledmaze.commandapi.command.BasicCommand;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;

public class Reload extends BasicCommand {
	
	public Reload(MazeCommand mazeCommand) {
		super("reload",	Constants.RELOAD_PERM, mazeCommand);
//		super("reload", "/tangledmaze reload", 0, false, Constants.RELOAD_PERM, "rl");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		TangledMain.getInstance().reloadPlugin();
		Messages.MESSAGE_PLUGIN_RELOADED.sendTo(sender);
		return true;
	}
}