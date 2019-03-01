package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;

public class Help extends MazeCommand {

	public Help() {
		
		super("help", "/help <page>", 0, false, null, "h", "?");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		
		
		return true;
	}

}
