package me.gorgeousone.tangledmaze.core;

public enum MazeDimension {

	WALL_HEIGHT(2, 255),
	WALL_WIDTH(1, 128),
	PATH_WIDTH(1, 128),
	PATH_LENGTH(5, 10),
	CEILING_WIDTH(1, 128);
	
	private int defaultValue;
	private int maxValue;
	
	private MazeDimension(int defaultValue, int maxValue) {
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
		return super.toString().toLowerCase().replaceAll("_", " ");
	}
	
	public String toCommandArg() {
		return super.toString().toLowerCase().replaceAll("_", "");
	}
	
	public static MazeDimension match(String playerInput) {
		
		for(MazeDimension dimension : MazeDimension.values()) {
			
			if(dimension.toCommandArg().equalsIgnoreCase(playerInput))
				return dimension;
		}
		
		return null;
	}
}