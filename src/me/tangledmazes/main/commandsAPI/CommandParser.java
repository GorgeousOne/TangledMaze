package me.tangledmazes.main.commandsAPI;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandParser implements CommandExecutor {
	
	public static ArrayList<ICommandSyntax> syntaxes = new ArrayList<>();
	
	public static void addSyntax(ICommandSyntax syntax) {
		syntaxes.add(syntax);
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		
		return false;
	}
}
