package me.tangledmazes.main.commandsAPI;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.tangledmazes.spthiel.api.Entry;

public class CommandParser implements CommandExecutor {
	
	public static ArrayList<ICommandSyntax> syntaxes = new ArrayList<>();
	public static ArrayList<ParsedCommand> commands = new ArrayList<>();
	
	public static void addSyntax(ICommandSyntax syntax) {
		syntaxes.add(syntax);
	}
	
	public static void addCommand(ICommand command) {
		ParsedCommand c = new ParsedCommand(command);
		if(c.getParsedSyntaxes().size() > 0)
			commands.add(c);
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		
		if(commandSender instanceof ConsoleCommandSender) {
			System.out.println("[TangledMaze] This plugin does not support console commands.");
			return true;
		}
		
		Player player = (Player)commandSender;
		for(ParsedCommand parsedCommand : commands) {
			for(Entry<String,ArrayList<ICommandSyntax>> entry : parsedCommand.getParsedSyntaxes()) {
				if(entry.getKey() == command.getName()) {
					
					for(ICommandSyntax syntax : entry.getValue()) {
					
					}
				}
			}
		}
		
		return true;
	}
	
	
}
