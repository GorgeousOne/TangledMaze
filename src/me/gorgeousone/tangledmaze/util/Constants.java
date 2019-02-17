package me.gorgeousone.tangledmaze.util;

import me.gorgeousone.tangledmaze.core.TangledMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
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

	private static final TreeSet<Material>
			NOT_SOLIDS = new TreeSet<>(),
			REPLACEABLE_SOLIDS = new TreeSet<>();

	public static void loadMaterialLists() {

		YamlConfiguration materialLists;


		if(Utils.getBukkitVersion() >= 13) {
			materialLists = Utils.getDefaultConfig("1.13_material_lists.yml");

		}else {
			materialLists = Utils.getDefaultConfig("1.12_material_lists.yml");
		}

		for(String materialName : (List<String>) materialLists.getList("not-solid-materials")) {

			try {
				NOT_SOLIDS.add(Material.valueOf(materialName));
			}catch (IllegalArgumentException e) {}
		}

		for(String materialName : (List<String>) materialLists.getList("replaceable-solid-materials")) {

			try {
				REPLACEABLE_SOLIDS.add(Material.valueOf(materialName));
			}catch (IllegalArgumentException e) {}
		}
	}
}