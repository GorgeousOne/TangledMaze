package me.gorgeousone.tangledmaze.maze;

import java.util.Arrays;

public enum MazeDimension {

	WALL_HEIGHT(2, 255),
	WALL_WIDTH(1, 128),
	PATH_WIDTH(1, 128),
	PATH_LENGTH(5, 10),
	ROOF_WIDTH(1, 128);
	
	private int defaultValue;
	private int maxValue;
	
	MazeDimension(int defaultValue, int maxValue) {
		this.defaultValue = defaultValue;
		this.maxValue = maxValue;
	}
	
	public int getDefault() {
		return defaultValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	@Override
	public String toString() {
		return name().toLowerCase().replaceAll("_", " ");
	}
	
	public String commandName() {
		return name().toLowerCase().replaceAll("_", "");
	}
	
	public static MazeDimension match(String playerInput) {
		
		for(MazeDimension dimension : MazeDimension.values()) {
			if(dimension.commandName().equalsIgnoreCase(playerInput))
				return dimension;
		}
		return null;
	}
	
	public static String[] getCommandNames() {
		return Arrays.stream(values()).map(MazeDimension::commandName).toArray(String[]::new);
	}
}