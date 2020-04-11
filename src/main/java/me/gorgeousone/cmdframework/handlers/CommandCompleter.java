package me.gorgeousone.cmdframework.handlers;

import me.gorgeousone.cmdframework.command.BasicCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CommandCompleter implements TabCompleter {
	
	private CommandHandler cmdHandler;
	
	public CommandCompleter(CommandHandler cmdHandler) {
		this.cmdHandler = cmdHandler;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		for (BasicCommand command : cmdHandler.getCommands()) {
			
			if (command.matches(cmd.getName())) {
				List<String> tabList = new LinkedList<>();
				
				for (String tab : command.getTabList(sender, args)) {
					
					if (tab.startsWith(args[args.length - 1]))
						tabList.add(tab);
				}
				
				return tabList;
			}
		}
		
		return null;
	}
}