package me.gorgeousone.tangledmaze.command.api.argument;

import org.bukkit.ChatColor;

public class ArgValue {

	private static final String argumentTypeException = ChatColor.RED + "'%value%' is not a %type%.";

	private String stringVal;
	private int intVal;
	private double decimalVal;
	
	public ArgValue(Argument argument, String value) {
		this(argument.getType(), value);
	}

	public ArgValue(ArgType type, String value) {
		setValue(type, value);
	}

	public String getString() {
		return stringVal;
	}
	
	public int getInt() {
		return intVal;
	}
	
	public double getDouble() {
		return decimalVal;
	}
	
	protected void setValue(ArgType type, String value) {
		
		try {
			switch (type) {
			
			case INTEGER:
				intVal = Integer.parseInt(value);
			
			case DECIMAL:
				decimalVal = Double.parseDouble(value);
				
			case STRING:
				stringVal = value;
				break;
			}
			
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(argumentTypeException.replace("%value%", value).replace("%type%", type.simpleName()));
		}
	}
}
