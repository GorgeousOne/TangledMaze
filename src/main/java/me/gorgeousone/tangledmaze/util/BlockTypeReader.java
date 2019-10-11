package me.gorgeousone.tangledmaze.util;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class BlockTypeReader {

	private static HashMap<String, String> blockDataTypes = new HashMap<>();

	static {
		blockDataTypes.put("east", "facing");
		blockDataTypes.put(	"south", "facing");
		blockDataTypes.put(	"west", "facing");
		blockDataTypes.put(	"north", "facing");
		blockDataTypes.put(	"top", "half");
		blockDataTypes.put(	"bottom", "half");
		blockDataTypes.put(	"x", "axis");
		blockDataTypes.put(	"y", "axis");
		blockDataTypes.put(	"z", "axis");
	}

	private BlockTypeReader() {}

	public static BlockTypeWrapper readBlockType(String  argument) {

		String[] split = argument.split(":");
		Material material = Material.matchMaterial(split[0]);

		if(material == null || !material.isBlock())
			throw new IllegalArgumentException("false material");

		BlockData blockData = material.createBlockData();
		Set<String> usedDataTypes = new HashSet<>();

		if(split.length < 2)
			return null;

		System.out.println(" --- test --- ");

		for(int i = 1; i < split.length; i++) {

			String dataValue = split[i];
			String dataType = blockDataTypes.get(dataValue);
			System.out.println(dataValue);

			blockData = blockData.merge(material.createBlockData("[" + dataType + "=" + dataValue + "]"));
			usedDataTypes.add(dataType);
		}

		System.out.println(blockData.getAsString());
		return null;
	}

//	private static BlockFace readCardinalFace(String face) {
//
//		if(face.length() == 1 && cardinalBlockFaces.containsKey(face.charAt(0)))
//			return cardinalBlockFaces.get(face.charAt(0));
//		else
//			throw new IllegalArgumentException("false facing");
//	}
//
//	private static BlockFace readHalfFace(String face) {
//
//		if(face.length() == 1 && halfBlockFaces.containsKey(face.charAt(0)))
//			return halfBlockFaces.get(face.charAt(0));
//		else
//			throw new IllegalArgumentException("false half");
//	}
}
