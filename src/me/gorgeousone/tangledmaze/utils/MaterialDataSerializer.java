package me.gorgeousone.tangledmaze.utils;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class MaterialDataSerializer {

	@SuppressWarnings("deprecation")
	public static MaterialData deserializeMaterialData(String materialData) {
		
		Material type;
		byte data;
		String typeString, dataString;
		
		if(materialData.contains(":")) {
			typeString = materialData.split(":")[0];
			dataString = materialData.split(":")[1];
			
		}else {
			typeString = materialData;
			dataString = "0";
		}

		type = Material.matchMaterial(typeString);
		
		if(type == null) {
			throw new IllegalArgumentException(ChatColor.RED + "\"" + typeString + "\" does not match any block type.");
		}
		
		try {
			data = Byte.parseByte(dataString);
		} catch (Exception e) {
			throw new IllegalArgumentException(ChatColor.RED + "\"" + dataString + "\" is not a valid number");
		}
		
		if(!Utils.canBeBuiltWith(type)) {
			throw new IllegalArgumentException(ChatColor.RED + "It could get difficult to build a maze out of \"" + typeString + "\".");
		}
		
		return new MaterialData(type, data);
	}
}