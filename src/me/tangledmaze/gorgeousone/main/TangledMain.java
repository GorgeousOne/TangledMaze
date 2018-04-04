package me.tangledmaze.gorgeousone.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmaze.gorgeousone.listener.MazeHandler;
import me.tangledmaze.gorgeousone.listener.Deselecter;
import me.tangledmaze.gorgeousone.listener.SelectionHandler;
import me.tangledmaze.gorgeousone.listener.ToolListener;
import me.tangledmaze.gorgeousone.mazes.MazeFiller;

public class TangledMain extends JavaPlugin {

	public static TangledMain plugin; 
	private static ItemStack wand;
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private MazeFiller mFiller;
	
	@Override
	public void onLoad() {
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		createWand();
		
		sHandler = new SelectionHandler();
		mHandler = new MazeHandler();
		mFiller = new MazeFiller();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(sHandler, this);
		pm.registerEvents(new ToolListener(this), this);
		pm.registerEvents(new Deselecter(this), this);
		
		getCommand("tangledmaze").setExecutor(new CommandListener(this));
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}

	@Override
	public void onDisable() {
		sHandler.reload();
		mHandler.reload();
	}
	
	public static ItemStack getWand() {
		
		ItemMeta meta = wand.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0,"§7" + getCustomEnchantment());
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
	
	public MazeFiller getMazeFiller() {
		return mFiller;
	}
	
	private void createWand() {
		wand = new ItemStack(Material.GOLD_SPADE);
		wand.setAmount(1);
		ItemMeta meta = wand.getItemMeta();
		meta.setDisplayName("§dSelection Shovel");
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("");
		lore.add("§bThe tool to create mazes.");
		lore.add("§bUse left click to begin a selection.");
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
			"Selector I",
			"Selector II",
			"Selector III",
			"Infinite Maze I",
			"Ban hammer I",
			"Force OP I",
			"Java.exe II",
			"Ignore WorldGuard IV"
	};
	
	private static final Random rnd = new Random();
	
	private static String getCustomEnchantment() {
		int select = rnd.nextInt(enchants.length);
		return enchants[select];
	}
}