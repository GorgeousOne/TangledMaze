package me.tangledmaze.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.spthiel.main.TangledMain_sp;

public class TangledMain extends JavaPlugin {

	public static ArrayList<IMain> mains = new ArrayList<>();
	public static TangledMain plugin;
	
	private static ItemStack wand;
	
	public static void addMain(IMain... main) {
		mains.addAll(Arrays.asList(main));
	}
	
	@Override
	public void onLoad() {
		plugin = this;
		addMain(new TangledMain_sp(), new TangledMain_go());
		
		mains.forEach(main -> main.onLoad(this));
	}
	
	@Override
	public void onEnable() {
		mains.forEach(main -> main.onEnable(this));
		
		String NAME = "§dSelection Shovel";
		
		wand = new ItemStack(Material.GOLD_SPADE);
		wand.setAmount(1);
		ItemMeta wandmeta = wand.getItemMeta();
		wandmeta.setDisplayName(NAME);
		wandmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		wandmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("");
		lore.add("§bUltimate tool for maze selections.");
		lore.add("§bUse right click to set or move points.");
		wandmeta.setLore(lore);
		wand.setItemMeta(wandmeta);
	}
	
	@Override
	public void onDisable() {
		mains.forEach(main -> main.onDisable(this));
	}
	
	public static ItemStack getWand() {
		
		ItemMeta meta = wand.getItemMeta();
		List<String> s = meta.getLore();
		s.set(0,"§7" + getCustomEnchantment());
		meta.setLore(s);
		wand.setItemMeta(meta);
		return wand;
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