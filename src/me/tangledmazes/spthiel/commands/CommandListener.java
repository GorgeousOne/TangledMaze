package me.tangledmazes.spthiel.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

import me.tangledmazes.main.TangledMain;

public class CommandListener implements CommandExecutor {
	
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		
		
		if (commandSender instanceof ConsoleCommandSender) {
			return true;
		}
		
		Player p = (Player) commandSender;
		
		if (command.getName().equalsIgnoreCase("tangledmaze")) {
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
					case "wand":
						
						
						for (ItemStack i : p.getInventory().getContents()) {
							if (i != null && TangledMain.isSelectionWand(i))
								p.getInventory().remove(i);
						}
						
						p.getInventory().addItem(TangledMain.getWand());
						break;
					case "undo":
						break;
					case "deselect":
						break;
					case "help":
					case "?":
					case "h":
						if (args.length > 2) {
							try {
								int page = Integer.parseInt(args[2]);
								sendHelp(p, page);
							} catch (NumberFormatException e) {
								sendHelp(p, 1);
							}
						} else {
							sendHelp(p, 1);
						}
						break;
					default:
						sendHelp(p, 1);
				}
			} else {
				sendHelp(p, 1);
			}
		}
		return false;
	}
	
	
	private void hasAnyPermission(Permission... perms) {
	
	}
	
	private void hasAllPermissions(Permission... perms) {
	
	}
	
	private void sendHelp(Player p, int page) {
		p.sendMessage("Help");
	}

}
