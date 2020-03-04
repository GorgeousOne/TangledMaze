package me.gorgeousone.tangledmaze.tools;

public enum ToolType {
	
	CLIP_TOOL(""),
	BRUSH_TOOL("brush"),
	EXIT_SETTER("exit setter");
	
	private String simpleName;
	
	ToolType(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
}
