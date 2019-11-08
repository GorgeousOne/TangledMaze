package me.gorgeousone.tangledmaze.command.framework.argument;

public enum ArgType {

	STRING("string"),
	INTEGER("integer"),
	DECIMAL("number");
	
	private String simpleName;
	
	ArgType(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public String simpleName() {
		return simpleName;
	}
}