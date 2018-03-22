package me.tangledmazes.main.commandsAPI;

public interface ICommandSyntax {
	
	String Symbol();
	boolean isSyntax(String arg);
	Object getTranslatedSyntax(String arg);
	
}
