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
	public boolean execute(CommandSender sender, String[] stringArgs) {
		
		if(!super.execute(sender, stringArgs))
			return false;
		
		if(stringArgs.length < stringArgs.length) {
			sendUsage(sender);
			return false;
		}
		
		if(stringArgs.length < arguments.size()) {
			sendUsage(sender);
			return false;
		}
			
		ArgValue[] values = new ArgValue[stringArgs.length];
		
		try {
			
			for(int i = 0; i < stringArgs.length; i++) {
				
				if(i < arguments.size())
					values[i] = new ArgValue(arguments.get(i), stringArgs[i]);
				else
					values[i] = new ArgValue(ArgType.STRING, stringArgs[i]);
			}
			
		} catch (IllegalArgumentException ex) {
			
			sender.sendMessage(ex.getMessage());
			return false;
		}
		
		onExecute(sender, values);
		return true;
	}
	
	protected abstract boolean onExecute(CommandSender sender, ArgValue[] values);
	
	@Override
	public String getUsage() {
		
		StringBuilder usage = new StringBuilder(super.getUsage());
		
		for(Argument arg : getArgs())
			usage.append(" <" + arg.getName() + ">");
		
		return usage.toString();
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		
		if(arguments.size() < args.length)
			return new LinkedList<>();
		
		return arguments.get(args.length-1).getTabList();
	}
}