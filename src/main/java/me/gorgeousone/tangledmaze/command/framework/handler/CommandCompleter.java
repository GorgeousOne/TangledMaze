package me.gorgeousone.tangledmaze.command.framework.handler;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.gorgeousone.tangledmaze.command.framework.command.BasicCommand;

public class CommandCompleter implements TabCompleter {

	private CommandHandler cmdHandler;
	
	public CommandCompleter(CommandHandler cmdHandler) {
		this.cmdHandler = cmdHandler;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		for(BasicCommand command : cmdHandler.getCommands()) {
			
			if(command.matches(cmd.getName())) {
				List<String> tabList = new LinkedList<>();
				
				for(String tab : command.getTabList(args)) {
					
					if(tab.startsWith(args[args.length-1]))
						tabList.add(tab);
				}
				
				return tabList;
			}
		}
		
		return null;
	}
}