package me.gorgeousone.tangledmaze.core;

public enum MazeDimension {

	WALL_HEIGHT(2),
	WALL_WIDTH(1),
	PATH_WIDTH(1),
	PATH_LENGTH(5),
	CEILING_WIDTH(1);
	
	private int defaultValue;
	
	private MazeDimension(int defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public int getDefault() {
		return defaultValue;
	}
}
