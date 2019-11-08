package me.gorgeousone.tangledmaze.command.framework.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Argument {
	
	private String name;
	
	private ArgType type;
	private List<String> tabList;
	private ArgValue defValue;

	public Argument(String name, ArgType type) {
		this(name, type, null, new String[] {});
	}

	public Argument(String name, ArgType type, String... tabList) {
		this(name, type, null, tabList);
	}

	public Argument(String name, ArgType type, ArgValue defValue) {
		this(name, type, defValue, new String[] {});
	}

	public Argument(String name, ArgType type, ArgValue defValue, String... tabList) {
		
		this.name = name;
		this.type = type;
		this.defValue = defValue;

		this.tabList = new ArrayList<>();
		this.tabList.addAll(Arrays.asList(tabList));
	}

	public boolean hasDefault() {
		return getDefault() != null;
	}

	public ArgValue getDefault() {
		return defValue;
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