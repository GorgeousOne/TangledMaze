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

public class CommandListener implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String name, String[] args) {
		
		
		if(commandSender instanceof ConsoleCommandSender) {
			return true;
		}
		
		Player p = (Player)commandSender;
		
		if(command.getName().equalsIgnoreCase("tangledmaze")) {
			if(args.length > 0) {
				switch(args[0].toLowerCase()) {
					case "wand":
						
						String NAME = "§dSelection Shovel";
						
						for(ItemStack i : p.getInventory().getContents()) {
							if(i != null && i.hasItemMeta() && i.getItemMeta().getDisplayName().equalsIgnoreCase(NAME))
								p.getInventory().remove(i);
						}
						
						ItemStack wand = new ItemStack(Material.GOLD_SPADE);
						wand.setAmount(1);
						wand.setDurability((short)1);
						ItemMeta wandmeta = wand.getItemMeta();
						wandmeta.setDisplayName(NAME);
						wandmeta.addEnchant(Enchantment.ARROW_INFINITE,1,true);
						wandmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						ArrayList<String> lore = new ArrayList<>();
						lore.add("§7" + getCustomEnchanment());
						lore.add("§bUltimate tool for maze selections.");
						lore.add("§bUse right click to set or move points.");
						wandmeta.setLore(lore);
						wand.setItemMeta(wandmeta);
						p.getInventory().addItem(wand);
						break;
					case "undo":
						break;
					case "deselect":
						break;
					case "help":
					case "?":
					case "h":
						if(args.length > 2) {
							try {
								int page = Integer.parseInt(args[2]);
								sendHelp(p,page);
							} catch(NumberFormatException e) {
								sendHelp(p,1);
							}
						} else {
							sendHelp(p,1);
						}
						break;
					default:
						sendHelp(p,1);
				}
			} else {
				sendHelp(p,1);
			}
		}
		return false;
	}
	
	private void hasAnyPermission(Permission... perms) {
	
	}
	
	private void hasAllPermissions(Permission... perms) {
	
	}
	
	private void sendHelp(Player p,int page) {
		p.sendMessage("Help");
	}
	
	private static final String[] ench = {
			"Selector I",
			"Selector II",
			"Selector III",
			"Infinite Maze I",
			"Ban hammer I",
			"Force OP I",
			"Java.exe II",
			"Ignore WorldGuard IV"
	};
	private static final Random   rnd  = new Random();
	
	private String getCustomEnchanment() {
		int select = rnd.nextInt(ench.length);
		return ench[select];
	}
}
