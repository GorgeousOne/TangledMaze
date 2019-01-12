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

import me.gorgeousone.tangledmaze.listeners.BlockChangeListener;
import me.gorgeousone.tangledmaze.listeners.PlayerListener;
import me.gorgeousone.tangledmaze.listeners.WandListener;

public class TangledMain extends JavaPlugin {
	
	private final String[] enchants = {
			"Selecting Thingy III",
			"Difficult Handling II",
			"Would Recommend It X/X",
			"Unbreaking âˆž",
			"Overpowered X",
			"Tangly III",
			"Wow I",
			"Ignore WorldGuard V",
			"Infinite Maze I",
			"Wubba Lubba Dub Dub IV",
			"Artifact Lv. XCIX"
	};
	
	private static TangledMain plugin;

	private ItemStack mazeTool;
	private int staffMazeSize, vipMazeSize, normalMazeSize;

	@Override
	public void onEnable() {
		plugin = this;
		
		createMazeWand();
		loadConfig();
		registerListeners();
	}
	
	@Override
	public void onDisable() {
		Renderer.reload();
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
	
	public boolean isMazeWand(ItemStack item) {
		if(item == null)
			return false;
		
		ItemMeta itemMeta = item.getItemMeta();
		
		return
			item.getType() == mazeTool.getType() &&
			itemMeta.getDisplayName() != null &&
			itemMeta.getDisplayName().equals(mazeTool.getItemMeta().getDisplayName());
	}
	
	public ItemStack getMazeWand() {
		ItemMeta meta = mazeTool.getItemMeta();
		List<String> lore = meta.getLore();

		lore.set(0, ChatColor.GRAY + getCustomEnchantment());
		meta.setLore(lore);
		mazeTool.setItemMeta(meta);

		return mazeTool;
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
	
	private void createMazeWand() {
		mazeTool = new ItemStack(Material.GOLD_SPADE);
		
		ItemMeta meta = mazeTool.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Maze Tool");
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "A tool designed to create mazes.");
		lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Look at it's delicate curves! つ◕_◕つ");
		lore.add(ChatColor.GREEN + "Click on the ground to start a clipboard.");
		
		meta.setLore(lore);
		mazeTool.setItemMeta(meta);
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new WandListener(this), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockChangeListener(), this);
		
		getCommand("tangledmaze").setExecutor(new CommandHandler(this));
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}
}
