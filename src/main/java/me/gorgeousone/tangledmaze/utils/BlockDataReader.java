package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.data.Messages;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public final class BlockDataReader {

	private BlockDataReader() {
	}

	public static BlockData read(String argument) throws TextException {

		String[] split = argument.split(":");
		String stringMat = split[0];
		Material material = Material.matchMaterial(stringMat);

		if (material == null || !material.isBlock())
			throw new TextException(
					Messages.ERROR_INVALID_BLOCK_NAME,
					new PlaceHolder("block", stringMat));

		BlockData blockData = material.createBlockData();

		if (material.name().contains("LEAVES"))
			blockData = blockData.merge(material.createBlockData("[persistent=true]"));

		if (split.length < 2)
			return blockData;

		for (int i = 1; i < split.length; i++) {

			String blockProperty = split[i];

			try {
				blockData = blockData.merge(material.createBlockData("[" + blockProperty + "]"));
			} catch (IllegalArgumentException ex) {
				createPlayerMessageFromException(ex.getCause().getLocalizedMessage(), stringMat, blockProperty.split("="));
			}
		}

		return blockData;
	}

	private static void createPlayerMessageFromException(String exceptionMessage, String material,
	                                                     String[] blockProperty) throws TextException {

		if (exceptionMessage.contains("does not have property"))
			throw new TextException(
					Messages.ERROR_INVALID_BLOCK_PROPERTY,
					new PlaceHolder("block", material),
					new PlaceHolder("property", blockProperty[0]));

		else if (exceptionMessage.contains("Expected value for property"))
			throw new TextException(
					Messages.ERROR_MISSING_BLOCK_PROPERTY_VALUE,
					new PlaceHolder("property", blockProperty[0]));

		else if (exceptionMessage.contains("does not accept"))
			throw new TextException(
					Messages.ERROR_INVALID_BLOCK_PROPERTY_VALUE,
					new PlaceHolder("block", material),
					new PlaceHolder("property", blockProperty[0]),
					new PlaceHolder("value", blockProperty[1]));
	}
}
