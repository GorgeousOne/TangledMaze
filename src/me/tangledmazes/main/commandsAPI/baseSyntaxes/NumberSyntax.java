package me.tangledmazes.main.commandsAPI.baseSyntaxes;

import me.tangledmazes.main.commandsAPI.ICommandSyntax;

public class NumberSyntax implements ICommandSyntax {
	
	@Override
	public char Symbol() {
		return '#';
	}
	
	@Override
	public boolean isSyntax(String arg) {
		
		return arg.matches("^\\d+$");
	}
	
	@Override
	public Object getTranslatedSyntax(String arg) {
		
		return Integer.parseInt(arg);
	}
}
