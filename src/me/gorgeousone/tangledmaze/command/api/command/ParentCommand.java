package me.gorgeousone.tangledmaze.command.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class ParentCommand extends BasicCommand {
	
	private List<BasicCommand> children;
	private String childrenType;
	
	public ParentCommand(String name, String permission, String childrenType) {
		this(name, permission, childrenType, null);
	}
	
	public ParentCommand(String name, String permission, String childrenType, ParentCommand parent) {
		
		super(name, permission, parent);

		this.childrenType = "<" + childrenType + ">";
		this.children = new ArrayList<>();
	}
	
	public List<BasicCommand> getChildren() {
		return children;
	}
	
	public void addChild(BasicCommand child) {
		children.add(child);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		if(!super.execute(sender, args))
			return false;
		
		if(args.length == 0) {
			sendUsage(sender);
			return false;
		}
		
		for(BasicCommand child : getChildren()) {
			
			if(child.matches(args[0]))
				return child.execute(sender, Arrays.copyOfRange(args, 1, args.length));
		}
		
		sendUsage(sender);
		return false;
	}
	
	@Override
	public String getUsage() {
		return getParentUsage() + " " + childrenType;
	}
	
	public String getParentUsage() {
		return super.getUsage();
	}

	@Override
	public List<String> getTabList(String[] args) {
		
		if(args.length == 1) {
			
			List<String> tabList = new LinkedList<>();
			
			for(BasicCommand child : getChildren())
				tabList.add(child.getName());
			
			return tabList;
		}
		
		for(BasicCommand child : getChildren()) {
			
			if(child.matches(args[0]))
				return child.getTabList(Arrays.copyOfRange(args, 1, args.length));
		}
		
		return new LinkedList<>();
	}
}