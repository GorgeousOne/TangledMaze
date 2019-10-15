package me.gorgeousone.tangledmaze.command.api.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.gorgeousone.tangledmaze.command.api.argument.ArgType;
import me.gorgeousone.tangledmaze.command.api.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.api.argument.Argument;

public abstract class ArgCommand extends BasicCommand {

	private List<Argument> arguments;

	public ArgCommand(String name, String permission) {
		this(name, permission, null);
	}

	public ArgCommand(String name, String permission, ParentCommand parent) {

		super(name, permission, parent);
		this.arguments = new ArrayList<>();
	}

	public List<Argument> getArgs() {
		return arguments;
	}

	protected void addArg(Argument arg) {
		arguments.add(arg);
	}

	@Override
	public String getUsage() {

		StringBuilder usage = new StringBuilder(super.getUsage());

		for (Argument arg : getArgs()) {
			usage.append(" <");
			usage.append(arg.getName());
			usage.append(">");
		}

		return usage.toString();
	}

	@Override
	public List<String> getTabList(String[] arguments) {

		if (this.arguments.size() < arguments.length)
			return new LinkedList<>();

		return this.arguments.get(arguments.length - 1).getTabList();
	}

	protected abstract boolean onExecute(CommandSender sender, ArgValue[] arguments);

	@Override
	protected boolean onExecute(CommandSender sender, String[] stringArgs) {

		int argsSize = getArgs().size();
		int stringArgsLength = stringArgs.length;

		ArgValue[] values = new ArgValue[Math.max(argsSize, stringArgsLength)];

		try {
			if (stringArgsLength >= argsSize)
				createMoreValuesThanArgs(values, stringArgs);
			else
				createMoreValuesThanInput(values, stringArgs);

		} catch (IllegalArgumentException ex) {

			sender.sendMessage(ex.getMessage());
			return false;
		}

		onExecute(sender, values);
		return true;
	}

	protected void createMoreValuesThanArgs(ArgValue[] values, String[] stringArgs) {

		for (int i = 0; i < values.length; i++) {

			values[i] = i < getArgs().size() ?
					new ArgValue(getArgs().get(i), stringArgs[i]) :
					new ArgValue(ArgType.STRING, stringArgs[i]);
		}
	}

	protected void createMoreValuesThanInput(ArgValue[] values, String[] stringArgs) {

		for (int i = 0; i < values.length; i++) {
			Argument arg = getArgs().get(i);

			if (i < stringArgs.length) {
				values[i] = new ArgValue(getArgs().get(i), stringArgs[i]);
				continue;
			}

			if (arg.hasDefault())
				values[i] = arg.getDefault();
			else
				throw new IllegalArgumentException(getUsage());
		}
	}
}