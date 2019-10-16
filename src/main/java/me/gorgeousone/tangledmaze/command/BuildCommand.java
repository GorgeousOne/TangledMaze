package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.gorgeousone.tangledmaze.generation.typechoosing.RandomBlockTypeChooser;

import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.BlockType;
import me.gorgeousone.tangledmaze.util.BlockTypeReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.command.api.argument.ArgType;
import me.gorgeousone.tangledmaze.command.api.argument.ArgValue;
import me.gorgeousone.tangledmaze.command.api.argument.Argument;
import me.gorgeousone.tangledmaze.command.api.command.ArgCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.TextException;

public class BuildCommand extends ArgCommand {

	public BuildCommand(MazeCommand mazeCommand) {
		super("build", null, mazeCommand);
		
		addArg(new Argument("part", ArgType.STRING, "walls", "floor", "roof"));
		addArg(new Argument("blocks...", ArgType.STRING));
	}

	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] arguments) {
		
		Player player = (Player) sender;
		Maze maze = getStartedMaze(player, true, false);
		
		if(maze == null)
			return false;
		
		String mazePart = arguments[0].getString();
		List<BlockType> blockTypeList;

		try {
			blockTypeList = readBlockTypeList(Arrays.copyOfRange(arguments, 1, arguments.length));

		} catch (TextException ex) {
			ex.sendTextTo(player);
			return false;
		}

		switch (mazePart) {
		case "floor":

			if(!maze.isConstructed()) {
				Messages.ERROR_MAZE_NOT_BUILT.sendTo(player);
				return false;
			}

			if(BuildHandler.hasFloor(maze)) {
				Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
				player.sendMessage("/tangledmaze unbuild floor");
				return false;
			}

			BuildHandler.buildFloor(maze, blockTypeList, new RandomBlockTypeChooser());
			break;
		
		case "roof":

			if(!maze.isConstructed()) {
				Messages.ERROR_MAZE_NOT_BUILT.sendTo(player);
				return false;
			}

			if(BuildHandler.hasRoof(maze)) {
				Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
				player.sendMessage("/tangledmaze unbuild roof");
				return false;
			}

			BuildHandler.buildRoof(maze, blockTypeList, new RandomBlockTypeChooser());
			break;
			
		case "walls":
		case "maze":

			if(maze.isConstructed()) {
				Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(player);
				return false;
			}

			Renderer.hideMaze(maze);
			ToolHandler.removeTool(maze.getPlayer());
			BuildHandler.buildWalls(maze, blockTypeList, new RandomBlockTypeChooser());
			break;

		default:
			Messages.ERROR_INVALID_MAZE_PART.sendTo(player, new PlaceHolder("mazepart", mazePart));
			return false;
		}
		
		return true;
	}

	private List<BlockType> readBlockTypeList(ArgValue[] arguments) throws TextException {

		List<BlockType> blockTypeList = new ArrayList<>();

		for(ArgValue argument : arguments) {

			String[] blockArgument = argument.getString().split("\\*");

			if (blockArgument.length == 1) {
				blockTypeList.add(BlockTypeReader.read(blockArgument[0]));

			} else {
				int multiplier = new ArgValue(ArgType.INTEGER, blockArgument[0]).getInt();
				BlockType blockType = BlockTypeReader.read(blockArgument[1]);

				for (int k = 0; k < multiplier; k++)
					blockTypeList.add(blockType.clone());
			}
		}
		return blockTypeList;
	}
}