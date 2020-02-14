package me.gorgeousone.tangledmaze.tools;

public enum MazeToolType {
	
	CLIP_TOOL(""),
	BRUSH_TOOL("brush"),
	EXIT_SETTER("exit setter");
	
	private String simpleName;
	
	MazeToolType(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
}
