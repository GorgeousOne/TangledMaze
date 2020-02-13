package me.gorgeousone.tangledmaze.clip;

public enum ClipShape {

	RECTANGLE("rectangle", 2),
	ELLIPSE("circle", 2);

	private int requiredVertexCount;
	private String simpleName;

	ClipShape(String simpleName, int requiredVertexCount) {

		this.requiredVertexCount = requiredVertexCount;
		this.simpleName = simpleName;
	}

	public int getRequiredVertexCount() {
		return requiredVertexCount;
	}

	public String getSimpleName() {
		return simpleName;
	}
}
