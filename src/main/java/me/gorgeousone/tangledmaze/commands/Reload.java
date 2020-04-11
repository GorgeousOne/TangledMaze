package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.command.BasicCommand;
import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import org.bukkit.command.CommandSender;

public class Reload extends BasicCommand {
	
	private TangledMain main;
	
	public Reload(TangledMain main, MazeCommand mazeCommand) {
		super("reload", Constants.RELOAD_PERM, false, mazeCommand);
		
		this.main = main;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		
		main.reloadPlugin();
		Messages.MESSAGE_PLUGIN_RELOADED.sendTo(sender);
		return;
	}
}