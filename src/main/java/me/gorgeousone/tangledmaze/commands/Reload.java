package me.gorgeousone.tangledmaze.commands;

import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.commands.framework.command.BasicCommand;
import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;

public class Reload extends BasicCommand {
	
	public Reload(MazeCommand mazeCommand) {
		super("reload",	Constants.RELOAD_PERM, false, mazeCommand);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {
		
		TangledMain.getInstance().reloadPlugin();
		Messages.MESSAGE_PLUGIN_RELOADED.sendTo(sender);
		return true;
	}
}