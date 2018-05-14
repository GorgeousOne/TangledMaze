package me.tangledmaze.gorgeousone.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmaze.gorgeousone.listener.*;
import me.tangledmaze.gorgeousone.mazes.MazeBuilder;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class TangledMain extends JavaPlugin {
	
	private static TangledMain plugin;
	private static ItemStack wand;
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private MazeBuilder mBuilder;
	
	private int staffMazeSize, vipMazeSize, normalMazeSize;
	
	@Override
	public void onLoad() {}
	
	@Override
	public void onEnable() {
		plugin = this;
		loadConfig();
		initWand();
		
		mHandler = new MazeHandler();
		mBuilder  = new MazeBuilder();
		sHandler = new SelectionHandler();
		
		PluginManager pm = getServer().getPluginManager();
		BlockChangeListener bl = new BlockChangeListener();
		
		new ToolListener(bl);
		
		pm.registerEvents(new ToolListener(bl), this);
//		pm.registerEvents(bl, this);
		pm.registerEvents(new PlayerVanishListener(), this);
		
		getCommand("tangledmaze").setExecutor(new CommandHandler());
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}

	@Override
	public void onDisable() {
		sHandler.reload();
		mHandler.reload();
	}
	
	private void loadConfig() {
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		staffMazeSize = getConfig().getInt("staff");
		vipMazeSize = getConfig().getInt("vip");
		normalMazeSize = getConfig().getInt("normal");
	}
	
	public static TangledMain getPlugin() {
		return plugin;
	}
	
	public SelectionHandler getSelectionHandler() {
		return sHandler;
	}
	
	public MazeHandler getMazeHandler() {
		return mHandler;
	}
	
	public MazeBuilder getMazeBuilder() {
		return mBuilder;
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

	public static ItemStack getWand() {
		ItemMeta meta = wand.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0, ChatColor.GRAY + getCustomEnchantment());
		meta.setLore(lore);
		wand.setItemMeta(meta);
		return wand;
	}
	
	private void initWand() {
		wand = new ItemStack(Material.GOLD_SPADE);
		
		ItemMeta meta = wand.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Selection Wand");
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "The tool to create mazes.");
		lore.add(ChatColor.GREEN + "Use left click to start a selection.");
		
		meta.setLore(lore);
		wand.setItemMeta(meta);
	}
	
	public static boolean isSelectionWand(ItemStack item) {
		if(item == null)
			return false;
		
		ItemMeta itemMeta = item.getItemMeta();
		ItemMeta wandmeta = wand.getItemMeta();
		return item.getType() == wand.getType() && itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equals(wandmeta.getDisplayName());
	}
	
	private static final String[] enchants = {
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
	
	private static String getCustomEnchantment() {
		Random rnd = new Random();
		
		int select = rnd.nextInt(enchants.length);
		return enchants[select];
	}
}