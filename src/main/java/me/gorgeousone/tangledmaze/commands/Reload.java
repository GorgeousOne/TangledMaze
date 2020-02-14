package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import org.bukkit.command.CommandSender;

public class Reload extends BasicCommand {

	public Reload(MazeCommand mazeCommand) {
		super("reload", Constants.RELOAD_PERM, false, mazeCommand);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] arguments) {

		TangledMain.getInstance().reloadPlugin();
		Messages.MESSAGE_PLUGIN_RELOADED.sendTo(sender);
		return true;
	}
}