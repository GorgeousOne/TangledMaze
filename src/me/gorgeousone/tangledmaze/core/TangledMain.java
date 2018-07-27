package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.gorgeousone.tangledmaze.listeners.PlayerListener;
import me.gorgeousone.tangledmaze.listeners.WandListener;

public class TangledMain extends JavaPlugin {
	
	private final String[] enchants = {
			"Selecting Thingy III",
			"Difficult Handling II",
			"Would Recommend It X/X",
			"Unbreaking ∞",
			"Overpowered X",
			"Tangly III",
			"Wow I",
			"Ignore WorldGuard V",
			"Infinite Maze I",
			"Wubba Lubba Dub Dub IV",
			"Artifact Lv. XCIX"
	};
	
	private static TangledMain plugin;

	private ItemStack wand;
	private int staffMazeSize, vipMazeSize, normalMazeSize;

	@Override
	public void onEnable() {
		plugin = this;
		
		createWand();
		loadConfig();
		registerListeners();
	}
	
	@Override
	public void onDisable() {
		Renderer.unregister();
		super.onDisable();
	}
	
	public static TangledMain getPlugin() {
		return plugin;
	}
	
	public int getStaffMazeSize() {
		return staffMazeSize;
	}

	public int getVipMazeSize() {
		return vipMazeSize;
	}

	public int getNormalMazeSize() {
		return normalMazeSize;
	}
	
	public boolean isSelectionWand(ItemStack item) {
		if(item == null)
			return false;
		
		ItemMeta itemMeta = item.getItemMeta();
		ItemMeta wandmeta = wand.getItemMeta();
		
		return item.getType() == wand.getType() && itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equals(wandmeta.getDisplayName());
	}
	
	public ItemStack getWand() {
		ItemMeta meta = wand.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0, ChatColor.GRAY + getCustomEnchantment());
		meta.setLore(lore);
		wand.setItemMeta(meta);
		return wand;
	}
	
	private String getCustomEnchantment() {
		Random rnd = new Random();
		
		int select = rnd.nextInt(enchants.length);
		return enchants[select];
	}
	
	private void loadConfig() {
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		staffMazeSize = getConfig().getInt("staff");
		vipMazeSize = getConfig().getInt("vip");
		normalMazeSize = getConfig().getInt("normal");
	}
	
	private void createWand() {
		wand = new ItemStack(Material.GOLD_SPADE);
		
		ItemMeta meta = wand.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Selection Wand");
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "The tool to create mazes.");
		lore.add(ChatColor.GREEN + "Click on the ground to start a selection.");
		
		meta.setLore(lore);
		wand.setItemMeta(meta);
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new WandListener(this), this);
		pm.registerEvents(new PlayerListener(), this);
		
		getCommand("tangledmaze").setExecutor(new CommandHandler(this));
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}
}
