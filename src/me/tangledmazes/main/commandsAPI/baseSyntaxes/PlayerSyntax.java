package me.tangledmazes.main.commandsAPI.baseSyntaxes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.tangledmazes.main.commandsAPI.ICommandSyntax;

public class PlayerSyntax implements ICommandSyntax {
	
	@Override
	public char Symbol() {
		return 'p';
	}
	
	@Override
	public boolean isSyntax(String arg) {
		
		return arg.matches("^[a-zA-Z_0-9]{3,16}$");
	}
	
	@Override
	public Object getTranslatedSyntax(String arg) {
		
		return null;
	}
	
	private boolean isPlayer(String arg) {
		
		Player p = Bukkit.getServer().getPlayerExact(arg);
		return p != null;
	}
	
}
