package me.tangledmazes.main.commandsAPI;

public interface ICommandSyntax {
	
	char Symbol();
	boolean isSyntax(String arg);
	Object getTranslatedSyntax(String arg);
	
}
