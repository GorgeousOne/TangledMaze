package me.gorgeousone.tangledmaze.command.api.argument;

public enum ArgType {

	STRING("string"),
	INTEGER("integer"),
	DECIMAL("number");
	
	private String simpleName;
	
	private ArgType(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public String simpleName() {
		return simpleName;
	}
}