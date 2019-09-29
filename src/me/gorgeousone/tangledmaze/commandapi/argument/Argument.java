package me.gorgeousone.tangledmaze.commandapi.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Argument {
	
	private String name;
	
	private ArgType type;
	private List<String> tabList;
	
	public Argument(String name, ArgType type) {
		this(name, type, new String[] {});
	}
	
	public Argument(String name, ArgType type, String... tabList) {
		
		this.name = name;
		this.type = type;
		
		this.tabList = new ArrayList<>();
		this.tabList.addAll(Arrays.asList(tabList));
	}
	
	public String getName() {
		return name;
	}

	public ArgType getType() {
		return type;
	}
	
	public List<String> getTabList() {
		return tabList;
	}
}