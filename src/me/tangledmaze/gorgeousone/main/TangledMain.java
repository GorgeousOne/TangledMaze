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

import me.tangledmaze.gorgeousone.listener.PlayerVanishListener;
import me.tangledmaze.gorgeousone.listener.ToolListener;
import me.tangledmaze.gorgeousone.listener.BlockChangeListener;
import me.tangledmaze.gorgeousone.mazes.MazeBuilder;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class TangledMain extends JavaPlugin {

	private static TangledMain plugin;
	private static ItemStack wand;
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private MazeBuilder mFiller;
	
	@Override
	public void onLoad() {}
	
	@Override
	public void onEnable() {
		plugin = this;
		initWand();
		
		mHandler = new MazeHandler();
		mFiller = new MazeBuilder();
		sHandler = new SelectionHandler();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(sHandler, this);
		pm.registerEvents(new ToolListener(), this);
		pm.registerEvents(new PlayerVanishListener(), this);
		pm.registerEvents(new BlockChangeListener(), this);
		
		getCommand("tangledmaze").setExecutor(new CommandListener());
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}

	@Override
	public void onDisable() {
		sHandler.reload();
		mHandler.reload();
	}
	
	public static TangledMain getPlugin() {
		return plugin;
	}
	
	public static ItemStack getWand() {
		ItemMeta meta = wand.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0, ChatColor.GRAY + getCustomEnchantment());
		meta.setLore(lore);
		wand.setItemMeta(meta);
		return wand;
	}
	
	public SelectionHandler getSelectionHandler() {
		return sHandler;
	}
	
	public MazeHandler getMazeHandler() {
		return mHandler;
	}
	
	public MazeBuilder getMazeBuilder() {
		return mFiller;
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
			"Ignore WorldGuard V",
			"Infinite Maze I",
			"Wubba Lubba Dub Dub IX",
			"Curvy Boi II",
			"Unbreaking ∞"
	};
	
	private static final Random rnd = new Random();
	
	private static String getCustomEnchantment() {
		int select = rnd.nextInt(enchants.length);
		return enchants[select];
	}
}