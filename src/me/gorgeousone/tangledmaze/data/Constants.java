package me.gorgeousone.tangledmaze.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import me.gorgeousone.tangledmaze.util.Utils;

import java.util.List;
import java.util.TreeSet;

public class Constants {
	
	public static int BUKKIT_VERSION;
	
	public static final String
			INSUFFICIENT_PERMS = ChatColor.RED + "You do not have the Permission for this command.",
			RELOAD_PERM = "tangledmaze.reload",
			BUILD_PERM  = "tangledmaze.build",
			WAND_PERM   = "tangledmaze.getwand",
			MAZE_TP_PERM = "tangledmaze.teleport";
	
	public static final String prefix =
			ChatColor.DARK_GREEN + "["  +
			ChatColor.GREEN      + "TM" + 
			ChatColor.DARK_GREEN + "] " +
			ChatColor.YELLOW;
	
	public static final Material
			MAZE_BORDER      = Material.REDSTONE_BLOCK,
			MAZE_MAIN_EXIT   = Material.DIAMOND_BLOCK,
			MAZE_EXIT        = Material.EMERALD_BLOCK,
			CLIPBOARD_BORDER = Material.GOLD_BLOCK,
			CLIPBOARD_CORNER = Material.LAPIS_BLOCK;

	public static final TreeSet<Material>
			NOT_SOLIDS = new TreeSet<>(),
			REPLACEABLE_SOLIDS = new TreeSet<>();
	
	public static final String[] MAZE_WAND_ENCHANTS = {
			"Difficult Handling II",
			"Would Recommend X/X",
			"Unbreaking ∞",
			"Overpowered X",
			"Tangly III",
			"Wow I",
			"Ignoring WorldGuard V",
			"Wubba Lubba Dub Dub IV",
			"Artifact Lv. XCIX"
	};
	
	@SuppressWarnings("unchecked")
	public static void loadConstants() {
		
		BUKKIT_VERSION = Integer.valueOf(Bukkit.getBukkitVersion().split("\\.")[1]);

		YamlConfiguration materialLists;

		if(BUKKIT_VERSION < 13) {
			materialLists = Utils.getDefaultConfig("1.12_material_lists.yml");

		}else {
			materialLists = Utils.getDefaultConfig("1.13_material_lists.yml");
		}

		for(String materialName : (List<String>) materialLists.getList("not-solid-materials")) {

			try {
				NOT_SOLIDS.add(Material.valueOf(materialName));
			}catch (IllegalArgumentException ignored) {}
		}

		for(String materialName : (List<String>) materialLists.getList("replaceable-solid-materials")) {

			try {
				REPLACEABLE_SOLIDS.add(Material.valueOf(materialName));
			}catch (IllegalArgumentException ignored) {}
		}
	}
}