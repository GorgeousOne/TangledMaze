package me.gorgeousone.tangledmaze.maze;

public enum MazePart {
	WALLS(false),
	FLOOR(true),
	ROOF(true);
	
	private boolean isMazeBuiltBefore;
	
	MazePart(boolean isMazeBuiltBefore) {
		this.isMazeBuiltBefore = isMazeBuiltBefore;
	}
	
	public boolean isMazeBuiltBefore() {
		return isMazeBuiltBefore;
	}
}
