package me.tangledmazes.main.commandsAPI.baseSyntaxes;

import me.tangledmazes.main.commandsAPI.ICommandSyntax;

public class StringSyntax implements ICommandSyntax {
	
	@Override
	public char Symbol() {
		return '&';
	}
	
	@Override
	public boolean isSyntax(String arg) {
		
		return arg.matches("^[A-Za-z]+$");
	}
	
	@Override
	public Object getTranslatedSyntax(String arg) {
		
		return arg;
	}
}
