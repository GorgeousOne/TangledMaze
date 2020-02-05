package me.gorgeousone.tangledmaze.clip;

public enum ClipShape {

	RECTANGLE(2, "rectangle"),
	ELLIPSE(2, "circle");

	private int requiredVertexCount;
	private String simpleName;

	ClipShape(int requiredVertexCount, String simpleName) {
		this.requiredVertexCount = requiredVertexCount;
		this.simpleName = simpleName;
	}

	public int getRequiredControlPointCount() {
		return requiredVertexCount;
	}

	public String getSimpleName() {
		return simpleName;
	}
}
