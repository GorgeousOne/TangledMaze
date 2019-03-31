package me.gorgeousone.tangledmaze.util;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;

@SuppressWarnings("deprecation")
public class BlockTypeReader {

	public static Material readMaterial(String materialName) {

		if(Constants.BUKKIT_VERSION <= 12) {
			return MaterialReflection.getMaterial(materialName);

		}else {
			return Material.matchMaterial(materialName);
		}
	}

	public static MaterialData readMaterialData(String materialData) throws TextException {
		
		Material type;
		byte data;
		
		String typeString;
		String dataString;
		
		if(materialData.contains(":")) {

			String[] split = materialData.split(":");
			typeString = split[0];
			dataString = split[1];
			
		}else {

			typeString = materialData;
			dataString = "0";
		}
		
		type = readMaterial(typeString);
		
		if(type == null || !type.isBlock()) {
			throw new TextException(Messages.ERROR_NO_MATCHING_BLOCK_TYPE, new PlaceHolder("block", typeString));
		}
		
		try {
			data = Byte.parseByte(dataString);
		
		} catch (NumberFormatException ex) {
			throw new TextException(Messages.ERROR_NUMBER_NOT_VALID, new PlaceHolder("number", dataString));
		}
		
		return new MaterialData(type, data);
	}
}