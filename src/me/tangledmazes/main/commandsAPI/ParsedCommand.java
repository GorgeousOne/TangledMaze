package me.tangledmazes.main.commandsAPI;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.tangledmazes.spthiel.api.Entry;

public class ParsedCommand {
	
	private ICommand command;
	private ArrayList<Entry<String,ArrayList<ICommandSyntax>>> parsedSyntaxes;
	
	public ParsedCommand(ICommand command) {
	
		this.command = command;
		for(String s : command.getSyntaxes()) {
			String[] splitted = s.split(" ");
			String value = null;
			ArrayList<ICommandSyntax> syntaxes = new ArrayList<>();
			for (int i = 0 ; i < splitted.length ; i++) {
				String arg = splitted[i];
				if(i == 0) {
					value = arg;
				} else {
					for (ICommandSyntax syntax : CommandParser.syntaxes) {
						if (syntax.Symbol().equalsIgnoreCase(arg)) {
							syntaxes.add(syntax);
							break;
						}
					}
				}
			}
			if(value != null)
				parsedSyntaxes.add(new Entry<>(value,syntaxes));
		}
	}
	
	public ArrayList<Entry<String, ArrayList<ICommandSyntax>>> getParsedSyntaxes() {
		
		return parsedSyntaxes;
	}
	
	public void run(Player p, String[] args, int syntax) {
		command.run(p,args,syntax);
	}
}
