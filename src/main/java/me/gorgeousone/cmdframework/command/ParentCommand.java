package me.gorgeousone.cmdframework.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class ParentCommand extends BasicCommand {

	private List<BasicCommand> children;
	private String childrenType;

	protected ParentCommand(String name, String permission, boolean isPlayerRequired, String childrenType) {
		this(name, permission, isPlayerRequired, childrenType, null);
	}

	protected ParentCommand(String name, String permission, boolean isPlayerRequired, String childrenType,
	                        ParentCommand parent) {

		super(name, permission, isPlayerRequired, parent);

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
	public boolean onCommand(CommandSender sender, String[] arguments) {

		if (arguments.length == 0) {
			sendUsage(sender);
			return false;
		}

		for (BasicCommand child : getChildren()) {

			if (child.matches(arguments[0]))
				return child.onCommand(sender, Arrays.copyOfRange(arguments, 1, arguments.length));
		}

		sendUsage(sender);
		return false;
	}

	@Override
	public String getUsage() {
		return super.getUsage() + " " + childrenType;
	}

	public String getParentUsage() {
		return super.getUsage();
	}

	@Override
	public List<String> getTabList(String[] arguments) {

		if (arguments.length == 1) {

			List<String> tabList = new LinkedList<>();

			for (BasicCommand child : getChildren()) {
				tabList.add(child.getName());
			}

			return tabList;
		}

		for (BasicCommand child : getChildren()) {

			if (child.matches(arguments[0]))
				return child.getTabList(Arrays.copyOfRange(arguments, 1, arguments.length));
		}

		return new LinkedList<>();
	}
}