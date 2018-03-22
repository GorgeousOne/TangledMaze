package me.tangledmazes.main.commandsAPI;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public interface ICommand {
	
	String[] getSyntaxes();
	Set<Permission> optionalPermissions();
	Set<Permission> requiredPermissions();
	void run(Player player, String[] args, int syntax);
	
}
