package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.utils.Constants;
import me.gorgeousone.tangledmaze.utils.NmsProvider;
import me.gorgeousone.tangledmaze.utils.Utils;

public class WallComposer {

	@SuppressWarnings("deprecation")
	public static ArrayList<MaterialData> deserializeComposition(Player p, ArrayList<String> serializedMaterialData) {
		
		ArrayList<MaterialData> composition = new ArrayList<>();
		
		for(String matData : serializedMaterialData) {
			Material material;
			byte data = 0;
			
			if(matData.contains(":")) {
				material = NmsProvider.getMaterial(matData.split(":")[0]);
				
				try {
					data = Byte.parseByte(matData.split(":")[1]);
				} catch (NumberFormatException e) {
					p.sendMessage(ChatColor.RED + "Something is weird about \"" + matData + "\"...");
					return null;
				}
				
			}else
				material = NmsProvider.getMaterial(matData);
			
			
			if(material == Material.AIR && !matData.equalsIgnoreCase("air")) {
				p.sendMessage(ChatColor.RED + "\"" + matData + "\" does not match any block type.");
				return null;
			}
			
			if(!Utils.canBeBuiltWith(material)) {
				p.sendMessage(Constants.prefix + "It could get difficult to build a maze out of \"" + matData + "\".");
				return null;
			}

			composition.add(new MaterialData(material, data));
		}
		
		return composition;
	}
}
