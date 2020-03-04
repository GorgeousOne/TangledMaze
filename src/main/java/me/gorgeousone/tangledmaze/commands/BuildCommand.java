package me.gorgeousone.tangledmaze.commands;

import me.gorgeousone.cmdframework.argument.ArgType;
import me.gorgeousone.cmdframework.argument.ArgValue;
import me.gorgeousone.cmdframework.argument.Argument;
import me.gorgeousone.cmdframework.command.ArgCommand;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.generation.MazePart;
import me.gorgeousone.tangledmaze.generation.blockdatapickers.RandomBlockDataPicker;
import me.gorgeousone.tangledmaze.generation.blocklocators.AbstractBlockLocator;
import me.gorgeousone.tangledmaze.generation.blocklocators.FloorBlockLocator;
import me.gorgeousone.tangledmaze.generation.blocklocators.HollowWallLocator;
import me.gorgeousone.tangledmaze.generation.blocklocators.RoofBlockLocator;
import me.gorgeousone.tangledmaze.generation.blocklocators.WallBlockLocator;
import me.gorgeousone.tangledmaze.handlers.BuildHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.ToolHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.utils.BlockDataReader;
import me.gorgeousone.tangledmaze.messages.PlaceHolder;
import me.gorgeousone.tangledmaze.messages.TextException;
import me.gorgeousone.tangledmaze.utils.MathHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BuildCommand extends ArgCommand {
	
	private ToolHandler toolHandler;
	private MazeHandler mazeHandler;
	private BuildHandler buildHandler;
	
	public BuildCommand(MazeCommand mazeCommand, ToolHandler toolHandler, MazeHandler mazeHandler,
	                    BuildHandler buildHandler) {
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
		
		if (maze == null)
			return false;
		
		String settingsString = arguments[0].getString();
		Map.Entry<MazePart, AbstractBlockLocator> buildSettings = readBuildSettings(settingsString);
		
		if (buildSettings == null) {
			Messages.ERROR_INVALID_MAZE_PART.sendTo(sender, new PlaceHolder("mazepart", arguments[0].getString()));
			return false;
		}
		
		MazePart mazePart = buildSettings.getKey();
		AbstractBlockLocator blockSelector = buildSettings.getValue();
		
		if (buildHandler.hasBlockBackup(maze) && buildHandler.getBlockBackup(maze).hasBackup(mazePart)) {
			
			Messages.ERROR_MAZE_PART_ALREADY_BUILT.sendTo(sender);
			sender.sendMessage("/tangledmaze unbuild " + mazePart.name().toLowerCase());
			return false;
		}
		
		try {
			maze.setBlockComposition(readBlockTypeList(Arrays.copyOfRange(arguments, 1, arguments.length)));
			
		} catch (TextException textEx) {
			textEx.sendTextTo(player);
			return false;
			
		}
		
		if (!maze.isConstructed()) {
			
			if (!mazePart.isMazeBuiltBefore()) {
				toolHandler.removePlayer(maze.getPlayer());
				
			} else {
				Messages.ERROR_MAZE_NOT_BUILT.sendTo(sender);
				return false;
			}
			
		} else if (!mazePart.isMazeBuiltBefore()) {
			
			Messages.ERROR_MAZE_PART_ALREADY_BUILT.sendTo(sender);
			return false;
		}
		
		buildHandler.buildMazePart(
				maze,
				mazePart,
				blockSelector,
				new RandomBlockDataPicker());
		
		return true;
	}
	
	@Override
	public List<String> getTabList(String[] arguments) {
		
		if (arguments.length < getArgs().size())
			return super.getTabList(arguments);
		
		List<String> tabList = new LinkedList<>();
		
		String tabbedArg = arguments[arguments.length - 1];
		String materialString = "";
		String restString = "";
		
		if (tabbedArg.endsWith("*"))
			restString = tabbedArg;
		
		else if (!tabbedArg.equals("")) {
			
			String[] argParts = (tabbedArg).split("\\*");
			materialString = argParts[argParts.length - 1];
			
			if (argParts.length > 1)
				restString = String.join("*", Arrays.copyOfRange(argParts, 0, argParts.length - 1)) + "*";
		}
		
		for (Material material : Material.values()) {
			
			if (!material.isBlock())
				continue;
			
			String materialName = material.name().toLowerCase();
			
			if (materialName.startsWith(materialString))
				tabList.add(restString + materialName);
		}
		
		return tabList;
	}
	
	private Map.Entry<MazePart, AbstractBlockLocator> readBuildSettings(String playerInput) {
		
		MazePart mazePart;
		AbstractBlockLocator blockSelector;
		
		switch (playerInput) {
			case "floor":
				
				mazePart = MazePart.FLOOR;
				blockSelector = new FloorBlockLocator();
				break;
			
			case "roof":
				
				mazePart = MazePart.ROOF;
				blockSelector = new RoofBlockLocator();
				break;
			
			case "walls-h":
				
				mazePart = MazePart.WALLS;
				blockSelector = new HollowWallLocator();
				break;
			
			case "walls":
			case "maze":
				
				mazePart = MazePart.WALLS;
				blockSelector = new WallBlockLocator();
				break;
			
			default:
				return null;
		}
		
		return new AbstractMap.SimpleEntry<>(mazePart, blockSelector);
	}
	
	private BlockComposition readBlockTypeList(ArgValue[] arguments) throws TextException {
		
		BlockComposition composition = new BlockComposition();
		
		for (ArgValue argument : arguments) {
			String[] blockArgument = argument.getString().split("\\*");
			
			if (blockArgument.length == 1) {
				composition.addBlock(BlockDataReader.read(blockArgument[0]), 1);
				
			} else {
				int amount = new ArgValue(ArgType.INTEGER, blockArgument[0]).getInt();
				composition.addBlock(BlockDataReader.read(blockArgument[1]), MathHelper.clamp(amount, 1, 1000));
			}
		}
		return composition;
	}
	
}