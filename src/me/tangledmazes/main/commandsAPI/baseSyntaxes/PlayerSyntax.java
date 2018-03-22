package me.tangledmazes.main.commandsAPI.baseSyntaxes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.tangledmazes.main.commandsAPI.ICommandSyntax;

public class PlayerSyntax implements ICommandSyntax {
	
	@Override
	public String Symbol() {
		return "p";
	}
	
	@Override
	public boolean isSyntax(String arg) {
		
		return arg.matches("^[a-zA-Z_0-9]{3,16}$") && getPlayer(arg) != null;
	}
	
	@Override
	public Object getTranslatedSyntax(String arg) {
		
		return getPlayer(arg);
	}
	
	private Player getPlayer(String arg) {
		
		return Bukkit.getServer().getPlayerExact(arg);
	}
	
}
