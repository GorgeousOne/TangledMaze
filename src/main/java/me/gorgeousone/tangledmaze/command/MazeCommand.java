package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.command.framework.command.ParentCommand;
import me.gorgeousone.tangledmaze.data.Constants;

public class MazeCommand extends ParentCommand {

	public MazeCommand() {
		super("tangledmaze", Constants.BUILD_PERM, false, "help");
		addAlias("maze");
		addAlias("tm");
	}
}