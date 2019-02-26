package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class MaterialReader {

	public static Material readMaterial(String materialName) {

		if(Constants.BUKKIT_VERSION <= 12) {
			return ReflectionMaterials.getMaterial(materialName);

		}else {
			return Material.matchMaterial(materialName);
		}
	}

	@SuppressWarnings("deprecation")
	public static MaterialData readMaterialData(String materialData) {
		
		Material type;
		byte data;
		String typeString, dataString;
		
		if(materialData.contains(":")) {

			String[] split = materialData.split(":");
			typeString = split[0];
			dataString = split[1];
			
		}else {

			typeString = materialData;
			dataString = "0";
		}
		
		type = readMaterial(typeString);
		
		if(type == null) {
			throw new IllegalArgumentException(ChatColor.RED + "\"" + typeString + "\" does not match any block.");
		}
		
		try {
			data = Byte.parseByte(dataString);
		} catch (Exception e) {
			throw new IllegalArgumentException(ChatColor.RED + "\"" + dataString + "\" is not a valid number.");
		}
		
		return new MaterialData(type, data);
	}
}