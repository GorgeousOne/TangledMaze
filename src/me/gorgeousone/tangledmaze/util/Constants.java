package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.TreeSet;

public class Constants {

	public static final String
			insufficientPerms = ChatColor.RED + "You do not have the Permission for this command.",
			buildPerm  = "tangledmaze.build",
			wandPerm   = "tangledmaze.getwand",
			mazeTpPerm = "tangledmaze.teleport";
	
	public static final String prefix =
			ChatColor.DARK_GREEN + "["  +
			ChatColor.GREEN      + "TM" + 
			ChatColor.DARK_GREEN + "] " +
			ChatColor.YELLOW;
	
	public static final Material
			MAZE_BORDER      = Material.REDSTONE_BLOCK,
			MAZE_EXIT        = Material.EMERALD_BLOCK,
			MAZE_MAIN_EXIT   = Material.DIAMOND_BLOCK,
			CLIPBOARD_CORNER = Material.LAPIS_BLOCK,
			CLIPBOARD_BORDER = Material.GOLD_BLOCK;
	
	public static final int
			MAX_PATH_WIDTH  = 50,
			MAX_WALL_WIDTH  = 50,
			MAX_WALL_HEIGHT = 100;

	private static final TreeSet<Material>
			NOT_SOLIDS = new TreeSet<>(),
			REPLACEABLE_SOLIDS = new TreeSet<>();

	public static void loadMaterialLists() {

		String fileName;

		int bukkitVersion = Utils.getBukkitVersion();

		if(bukkitVersion >= 13) {
			fileName = "1.13_material_lists";

		}else {
			fileName = "1.12_material_lists";
		}

		YamlConfiguration materialLists = Utils.getDefaultConfig(fileName);

		for(String materialName : (List<String>) materialLists.getList("not-solid-materials")) {
			NOT_SOLIDS.add(Material.valueOf(materialName));
		}

		for(String materialName : (List<String>) materialLists.getList("replaceable-solids")) {
			REPLACEABLE_SOLIDS.add(Material.valueOf(materialName));
		}
	}
}