package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.command.api.argument.ArgType;
import me.gorgeousone.tangledmaze.command.api.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.api.argument.Argument;
import me.gorgeousone.tangledmaze.command.api.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LoadMazeCommand extends ArgCommand {


	public LoadMazeCommand(MazeCommand mazeCommand) {
		super("load", Constants.MAZE_SAVE_PERM, mazeCommand);

		addArg(new Argument("file name", ArgType.STRING));
	}

	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] arguments) {

		String dataFileName = arguments[0].getString();
		YamlConfiguration mazeDataFile;

		try {
			mazeDataFile = Utils.loadDataFile("mazes" + File.separator + dataFileName);
		}catch (Exception ex) {
			sender.sendMessage("bleh");
			return false;
		}

		sender.sendMessage(mazeDataFile.getString("id"));
		return false;
	}
}
