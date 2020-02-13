package me.gorgeousone.tangledmaze.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.generation.blockselector.AbstractBlockSelector;
import me.gorgeousone.tangledmaze.generation.blockselector.FloorBlockSelector;
import me.gorgeousone.tangledmaze.generation.blockselector.HollowWallSelector;
import me.gorgeousone.tangledmaze.generation.blockselector.RoofBlockSelector;
import me.gorgeousone.tangledmaze.generation.blockselector.WallBlockSelector;
import me.gorgeousone.tangledmaze.generation.datapicker.RandomBlockDataPicker;

import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.maze.MazePart;
import me.gorgeousone.tangledmaze.utils.BlockDataReader;
import me.gorgeousone.tangledmaze.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commands.framework.argument.ArgType;
import me.gorgeousone.tangledmaze.commands.framework.argument.ArgValue;
import me.gorgeousone.tangledmaze.commands.framework.argument.Argument;
import me.gorgeousone.tangledmaze.commands.framework.command.ArgCommand;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.utils.PlaceHolder;
import me.gorgeousone.tangledmaze.utils.TextException;

public class BuildCommand extends ArgCommand {

	private ToolHandler toolHandler;
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;

	public BuildCommand(MazeCommand mazeCommand, ToolHandler toolHandler, MazeHandler mazeHandler, BuildHandler buildHandler) {
		super("build", null, true, mazeCommand);

		addArg(new Argument("part", ArgType.STRING, "walls", "walls-h", "floor", "roof").setDefaultTo("walls"));
		addArg(new Argument("blocks...", ArgType.STRING).setDefaultTo("stone"));

		this.toolHandler = toolHandler;
		this.mazeHandler = mazeHandler;
		this.buildHandler = buildHandler;
	}

	@Override
	protected boolean onCommand(CommandSender sender, ArgValue[] arguments) {

		Player player = (Player) sender;
		Maze maze = mazeHandler.getStartedMaze(player, true, false);
		
		if(maze == null)
			return false;
		
		String stringMazePart = arguments[0].getString();

		MazePart mazePart;
		AbstractBlockSelector blockSelector;

		switch (stringMazePart) {
		case "floor":

			mazePart = MazePart.FLOOR;
			blockSelector = new FloorBlockSelector();
			break;
		
		case "roof":

			mazePart = MazePart.ROOF;
			blockSelector = new RoofBlockSelector();
			break;

		case "walls-h":

			mazePart = MazePart.WALLS;
			blockSelector = new HollowWallSelector();
			break;

		case "walls":
		case "maze":

			mazePart = MazePart.WALLS;
			blockSelector = new WallBlockSelector();
			break;

		default:
			Messages.ERROR_INVALID_MAZE_PART.sendTo(sender, new PlaceHolder("mazepart", stringMazePart));
			return false;
		}

		if(buildHandler.hasBlockBackup(maze) && buildHandler.getBlockBackup(maze).hasBackup(mazePart)) {

			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(sender);
			sender.sendMessage("/tangledmaze unbuild " + mazePart.name().toLowerCase());
			return false;
		}

		try {
			maze.setBlockComposition(readBlockTypeList(Arrays.copyOfRange(arguments, 1, arguments.length)));

		}catch(TextException textEx) {
			textEx.sendTextTo(player);
			return false;

		}catch (Exception ex) {
			player.sendMessage(ex.getMessage());
			return false;
		}

		if(!maze.isConstructed()) {

			if(!mazePart.isMazeBuiltBefore()) {
				toolHandler.removeTool(maze.getPlayer());

			}else {
				Messages.ERROR_MAZE_NOT_BUILT.sendTo(sender);
				return false;
			}

		}else if(!mazePart.isMazeBuiltBefore()) {

			Messages.ERROR_MAZE_ALREADY_BUILT.sendTo(sender);
			return false;
		}

		buildHandler.buildMazePart(
				maze,
				mazePart,
				blockSelector,
				new RandomBlockDataPicker());
		
		return true;
	}

	private BlockComposition readBlockTypeList(ArgValue[] arguments) throws TextException {

		BlockComposition composition = new BlockComposition();

		for(ArgValue argument : arguments) {
			String[] blockArgument = argument.getString().split("\\*");

			if(blockArgument.length == 1) {
				composition.addBlock(BlockDataReader.read(blockArgument[0]), 1);

			}else {
				int amount = new ArgValue(ArgType.INTEGER, blockArgument[0]).getInt();
				composition.addBlock(BlockDataReader.read(blockArgument[1]), Utils.clamp(amount, 1, 1000));
			}
		}
		return composition;
	}

	@Override
	public List<String> getTabList(String[] arguments) {

		if(arguments.length < getArgs().size())
			return super.getTabList(arguments);

		List<String> tabList =new LinkedList<>();

		String tabbedArg = arguments[arguments.length-1];
		String materialString = "";
		String restString = "";

		if(tabbedArg.endsWith("*"))
			restString = tabbedArg;

		else if(!tabbedArg.equals("")) {

			String[] argParts = (tabbedArg).split("\\*");
			materialString = argParts[argParts.length-1];

			if(argParts.length > 1)
				restString = String.join("*", Arrays.copyOfRange(argParts, 0, argParts.length-1)) + "*";
		}

		for(Material material : Material.values()) {

			if(!material.isBlock())
				continue;

			String materialName = material.name().toLowerCase();

			if(materialName.startsWith(materialString))
				tabList.add(restString + materialName);
		}

		return tabList;
	}
}