package me.gorgeousone.tangledmaze.command.api.handler;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.command.api.command.BasicCommand;

public class CommandHandler implements CommandExecutor {

	private Set<BasicCommand> commands;

	public CommandHandler() {
		commands = new HashSet<>();
	}
	
	public void registerCommand(BasicCommand command) {
		commands.add(command);
	}
	
	public Set<BasicCommand> getCommands() {
		return commands;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String cmdName = cmd.getName();
		
		for(BasicCommand command : commands) {
			
			if(command.matches(cmdName)) {
				
				command.execute(sender, args);
				return true;
			}
		}
		return false;
	}
}