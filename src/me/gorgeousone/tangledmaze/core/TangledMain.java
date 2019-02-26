package me.gorgeousone.tangledmaze.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;
import me.gorgeousone.tangledmaze.util.Settings;
import me.gorgeousone.tangledmaze.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.gorgeousone.tangledmaze.handler.CommandHandler;
import me.gorgeousone.tangledmaze.listener.BlockUpdateListener;
import me.gorgeousone.tangledmaze.listener.PlayerListener;
import me.gorgeousone.tangledmaze.listener.ToolActionListener;

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

	private ItemStack mazeTool;

	@Override
	public void onEnable() {
		plugin = this;
		
		loadConfig();

		Constants.loadConstants();
		Settings.loadSettings(getConfig());

		loadLanguage();
		createMazeWand();
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
	}

	private void loadLanguage() {

		File langFolder =  new File(getDataFolder() + File.separator + "languages");

		File englishFile = new File(langFolder + File.separator + "english.yml");
		YamlConfiguration defEnglish = Utils.getDefaultConfig("english.yml");

		if(!englishFile.exists()) {
			Utils.saveConfig(defEnglish, englishFile);
		}

		YamlConfiguration langConfig;
		File langFile = new File(langFolder + File.separator + Settings.LANGUAGE + ".yml");


		if(langFile.exists()) {

			langConfig = YamlConfiguration.loadConfiguration(langFile);
			langConfig.setDefaults(defEnglish);
			langConfig.options().copyDefaults(true);
			Utils.saveConfig(langConfig, langFile);
			getLogger().info("Loaded " + Settings.LANGUAGE + " successfully.");

		}else {
			langConfig = defEnglish;
			getLogger().info("Unable to find language file: " + Settings.LANGUAGE + ".yml. Loading default english.");
		}

		Messages.loadLanguage(langConfig);
	}

	private void createMazeWand() {
		mazeTool = new ItemStack(Settings.MAZE_WAND_ITEM);
		
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
		
		pm.registerEvents(new ToolActionListener(this), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockUpdateListener(), this);
		
		getCommand("tangledmaze").setExecutor(new CommandHandler(this));
		getCommand("tangledmaze").setTabCompleter(new TangledCompleter());
	}
}
