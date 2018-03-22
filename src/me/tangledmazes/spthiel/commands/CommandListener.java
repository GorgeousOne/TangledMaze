package me.tangledmazes.spthiel.commands;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

public class CommandListener implements CommandExecutor {
	
	private ItemStack wand;

	public CommandListener() {
		
		wand = new ItemStack(Material.GOLD_SPADE);
		wand.setAmount(1);
		wand.setDurability((short)1);
		ItemMeta wandmeta = wand.getItemMeta();
		wandmeta.setDisplayName("§fSelection Shovel");
		wandmeta.addEnchant(Enchantment.ARROW_INFINITE,1,true);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§bUltimate tool for maze selections.");
		lore.add("§bUse right click to set or move points.");
		wandmeta.setLore(lore);
		wand.setItemMeta(wandmeta);
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		
		if(commandSender instanceof ConsoleCommandSender) {
			return true;
		}
		
		Player p = (Player)commandSender;
		
		if(command.getName().equalsIgnoreCase("tangledmaze")) {
			
			if(args.length < 2)
				sendHelp(p,1);

			switch(args[0].toLowerCase()) {
				case "wand":
					p.getInventory().addItem(wand.clone());
					break;
				case "undo":
					break;
				case "deselect":
					break;
				case "help":
				case "?":
				case "h":
					if(args.length < 3)
						sendHelp(p, 1);

					try {
						int page = Integer.parseInt(args[2]);
						sendHelp(p,page);
					}catch(NumberFormatException e) {
						sendHelp(p, 1);
					}
					break;
				default:
					sendHelp(p, 1);
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private void hasAnyPermission(Permission... perms) {
	
	}
	
	@SuppressWarnings("unused")
	private void hasAllPermissions(Permission... perms) {
	
	}
	
	private void sendHelp(Player p,int page) {
	
	}
}
