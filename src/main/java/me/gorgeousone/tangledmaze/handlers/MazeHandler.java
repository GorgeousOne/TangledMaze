package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.utils.RenderUtils;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This class stores mazes in relation to players.
 * Listeners, commands and tools can access a maze by it's owner here.
 */
public class MazeHandler {
	
	private JavaPlugin plugin;
	
	private HashMap<UUID, Maze> playerMazes;
	private HashMap<Maze, Boolean> mazeVisibilities;
	
	private UUID consoleUUID;
	
	public MazeHandler(JavaPlugin plugin) {
		
		this.plugin = plugin;
		
		playerMazes = new HashMap<>();
		mazeVisibilities = new HashMap<>();
		
		consoleUUID = UUID.randomUUID();
	}
	
	private UUID getSenderUUID(CommandSender sender) {
		return isHuman(sender) ? ((Player) sender).getUniqueId() : consoleUUID;
	}
	
	private boolean isHuman(CommandSender sender) {
		return (sender instanceof Player);
	}
	
	public Map<UUID, Maze> getPlayerMazes() {
		return new HashMap<>(playerMazes);
	}
	
	public Maze getMaze(CommandSender sender) {
		
		if (!sender.hasPermission(Constants.BUILD_PERM))
			return null;
		
		UUID uuid = getSenderUUID(sender);
		
		if (isHuman(sender) && !hasMaze(sender))
			playerMazes.put(uuid, new Maze(((Player) sender).getWorld()));
		
		return playerMazes.get(uuid);
	}
	
	public boolean hasMaze(CommandSender sender) {
		return playerMazes.containsKey(getSenderUUID(sender));
	}
	
	public void setMaze(CommandSender sender, Maze newMaze) {
		
		if (isHuman(sender) && hasStartedMaze(sender))
			removeMaze((Player) sender);
		
		playerMazes.put(getSenderUUID(sender), newMaze);
		
		if(isHuman(sender))
			displayMazeOf(sender);
	}
	
	public boolean hasStartedMaze(CommandSender sender) {
		return hasMaze(sender) && getMaze(sender).hasClip();
	}
	
	public void removeMaze(Player player) {
		
		if (!hasMaze(player))
			return;
		
		Maze maze = getMaze(player);
		hideMazeOf(player);
		mazeVisibilities.remove(maze);
		playerMazes.remove(player.getUniqueId());
	}
	
	public void removePlayer(Player player) {
		
		Maze maze = getMaze(player);
		mazeVisibilities.remove(maze);
		this.playerMazes.remove(player.getUniqueId());
	}
	
	public Maze getStartedMaze(CommandSender sender, boolean withExits, boolean notConstructed) {
		
		Maze maze = getMaze(sender);
		
		if (maze == null || !maze.hasClip()) {
			Messages.ERROR_MAZE_NOT_STARTED.sendTo(sender);
			
			if(isHuman(sender))
				sender.sendMessage("/tangledmaze start");
			
			if(sender.hasPermission(Constants.MAZE_BACKUP_PERM))
				sender.sendMessage("/tangledmaze load <file>");
			return null;
		}
		
		if (withExits && !maze.hasExits()) {
			Messages.ERROR_NO_MAZE_EXIT_SET.sendTo(sender);
			sender.sendMessage("/tangledmaze select exit");
			return null;
		}
		
		if (notConstructed && maze.isConstructed()) {
			Messages.ERROR_MAZE_PART_ALREADY_BUILT.sendTo(sender);
			return null;
		}
		
		return maze;
	}
	
	public void processClipChange(CommandSender sender, Maze maze, ClipChange action) {
		maze.processAction(action, true);
		
		if(isHuman(sender))
		displayMazeAction((Player) sender, action);
	}
	
	public boolean isMazeVisible(Maze maze) {
		return mazeVisibilities.getOrDefault(maze, false);
	}
	
	public void displayMazeOf(CommandSender player) {
		
		if (!isHuman(player))
			return;
		
		Maze maze = getMaze(player);
		
		if (maze.isConstructed() || !maze.hasClip())
			return;
		
		Clip mazeClip = maze.getClip();
		Map<Material, Collection<Location>> blocksToDisplay = new LinkedHashMap<>();
		
		blocksToDisplay.put(Constants.MAZE_BORDER, mazeClip.getBlockLocs(mazeClip.getBorder()));
		
		if (maze.hasExits()) {
			blocksToDisplay.put(Constants.MAZE_ENTRANCE, new HashSet<>(Arrays.asList(mazeClip.getBlockLoc(maze.getEntrance()))));
			blocksToDisplay.put(Constants.MAZE_EXIT, mazeClip.getBlockLocs(maze.getSecondaryExits()));
		}
		
		RenderUtils.sendBlocksDelayed((Player) player, blocksToDisplay, plugin);
		mazeVisibilities.put(maze, true);
	}
	
	public void hideMazeOf(CommandSender player) {
		
		if (!isHuman(player))
			return;
		
		Maze maze = getMaze(player);
		
		if (maze.isConstructed() || !maze.hasClip() || !isMazeVisible(maze))
			return;
		
		Clip mazeClip = maze.getClip();
		
		for(Location border : mazeClip.getBlockLocs(mazeClip.getBorder()))
			((Player) player).sendBlockChange(border, border.getBlock().getBlockData());
		
		mazeVisibilities.put(maze, false);
	}
	
	public void redisplayMazeBlock(Player player, Block block) {
		
		if (!hasStartedMaze(player))
			return;
		
		Maze maze = getMaze(player);
		Location blockLoc = block.getLocation();
		Vec2 blockVec = new Vec2(block);
		
		if (blockVec.equals(maze.getEntrance()))
			RenderUtils.sendBlockDelayed(player, blockLoc, Constants.MAZE_ENTRANCE, plugin);
		else if (maze.exitsContain(blockVec))
			RenderUtils.sendBlockDelayed(player, blockLoc, Constants.MAZE_EXIT, plugin);
		else
			RenderUtils.sendBlockDelayed(player, blockLoc, Constants.MAZE_BORDER, plugin);
	}
	
	private void displayMazeAction(Player player, ClipChange action) {
		
		Maze maze = getMaze(player);
		Clip clip = maze.getClip();
		
		for (Vec2 exit : action.getRemovedExits()) {
			
			player.sendBlockChange(clip.getBlockLoc(exit), Constants.MAZE_BORDER.createBlockData());
			List<Vec2> mazeExits = maze.getExits();
			
			if (exit.equals(maze.getEntrance()) && mazeExits.size() > 1)
				player.sendBlockChange(clip.getBlockLoc(mazeExits.get(mazeExits.size() - 2)), Constants.MAZE_ENTRANCE.createBlockData());
		}
		
		for (Vec2 point : action.getAddedBorder()) {
			player.sendBlockChange(action.getBorder(point), Constants.MAZE_BORDER.createBlockData());
		}
		
		for (Vec2 point : action.getRemovedBorder()) {
			Location block = action.getBorder(point);
			player.sendBlockChange(block, block.getBlock().getType().createBlockData());
		}
	}
	
	public void hideAllClues() {
		
		for (Map.Entry<UUID, Maze> entry : playerMazes.entrySet()) {
			if (isMazeVisible(entry.getValue()))
				hideMazeOf(Bukkit.getPlayer(entry.getKey()));
		}
	}
}