package me.gorgeousone.tangledmaze.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.gorgeousone.tangledmaze.generation.WallGenerator;
import me.gorgeousone.tangledmaze.generation.PathGenerator;
import me.gorgeousone.tangledmaze.generation.TerrainEditor;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.commandapi.argument.ArgType;
import me.gorgeousone.tangledmaze.commandapi.argument.ArgValue;
import me.gorgeousone.tangledmaze.commandapi.argument.Argument;
import me.gorgeousone.tangledmaze.commandapi.command.ArgCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.handler.Renderer;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.TextException;

public class BuildCommand extends ArgCommand {

	private PathGenerator pathGenerator;
	private TerrainEditor terrainEditor;
	private WallGenerator wallGenerator;

	public BuildCommand(MazeCommand mazeCommand) {
		super("build", null, mazeCommand);
		
		addArg(new Argument("part", ArgType.STRING, "maze", "floor", "ceiling"));
		addArg(new Argument("blocks...", ArgType.STRING));

		pathGenerator = new PathGenerator();
		terrainEditor = new TerrainEditor();
		wallGenerator = new WallGenerator();
	}

	@Override
	protected boolean onExecute(CommandSender sender, ArgValue[] args) {
		
		Player player = (Player) sender;
		Maze maze = getStartedMaze(player, true, true);
		
		if(maze == null)
			return false;
		
		switch (args[0].getString()) {
		
		case "floor":
			
			if(!maze.isConstructed())
			break;
		
		case "ceiling":

			if(!maze.isConstructed())
			break;
			
		case "maze":
			break;

		default:
			break;
		}
		
		List<Material> wallMaterials;
		
		try {
			wallMaterials = getWallMaterials(Arrays.copyOfRange(args, 1, args.length));
			
		} catch (TextException ex) {
			
			ex.sendTextTo(player);
			return false;
		}

		Renderer.hideMaze(maze);
		BuildHandler.buildMaze(maze, wallMaterials, pathGenerator, terrainEditor, wallGenerator);
		
		Messages.MESSAGE_MAZE_BUILDING_STARTED.sendTo(player);
		ToolHandler.resetToDefaultTool(player);
		return true;
	}
	
	private List<Material> getWallMaterials(ArgValue[] serializedMaterials) throws TextException {
		
		List<Material> wallMaterials = new ArrayList<>();
		
		for(ArgValue materialValue : serializedMaterials) {
			
			String materialString = materialValue.getString();
			Material material = Material.matchMaterial(materialString);
			
			if(material == null || !material.isBlock())
				throw new TextException(Messages.ERROR_NO_MATCHING_BLOCK_TYPE, new PlaceHolder("block", materialString));
			else
				wallMaterials.add(material);
		}
		
		return wallMaterials;
	}
}