package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipFactory;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.RenderUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClipToolHandler {
	
	private JavaPlugin plugin;
	private MazeHandler mazeHandler;
	
	private Map<UUID, ClipTool> playerClipTools;
	private Map<UUID, ClipShape> playerClipShapes;
	private HashMap<ClipTool, Boolean> clipVisibilities;
	
	public ClipToolHandler(JavaPlugin plugin, MazeHandler mazeHandler) {
		
		this.plugin = plugin;
		this.mazeHandler = mazeHandler;
		
		playerClipTools = new HashMap<>();
		playerClipShapes = new HashMap<>();
		clipVisibilities = new HashMap<>();
	}
	
	public Map<UUID, ClipTool> getPlayerClipTools() {
		return new HashMap<>(playerClipTools);
	}
	
	public ClipTool getClipTool(Player player) {
		return playerClipTools.get(player.getUniqueId());
	}
	
	public boolean hasClipTool(Player player) {
		return playerClipTools.containsKey(player.getUniqueId());
	}
	
	public boolean hasStartedClipTool(Player player) {
		return playerClipTools.containsKey(player.getUniqueId()) && getClipTool(player).isStarted();
	}
	
	public void setClipTool(Player player, ClipTool clipTool) {
		
		if (hasStartedClipTool(player)) {
			hideClipToolOf(player, true);
			clipVisibilities.remove(getClipTool(player));
		}
		
		UUID uuid = player.getUniqueId();
		playerClipTools.put(uuid, clipTool);
		displayClipToolOf(player);
	}
	
	public void removeClipTool(Player player) {
		
		if (hasStartedClipTool(player)) {
			hideClipToolOf(player, true);
			clipVisibilities.remove(getClipTool(player));
			playerClipTools.remove(player.getUniqueId());
		}
	}
	
	public void removePlayer(Player player) {
		
		if (hasClipTool(player))
			clipVisibilities.remove(getClipTool(player));
		
		playerClipShapes.remove(player.getUniqueId());
		playerClipTools.remove(player.getUniqueId());
	}
	
	public ClipShape getClipShape(Player player) {
		return playerClipShapes.getOrDefault(player.getUniqueId(), ClipShape.RECTANGLE);
	}
	
	public boolean setClipShape(Player player, ClipShape shape) {
		
		if (shape == getClipShape(player))
			return false;
		
		playerClipShapes.put(player.getUniqueId(), shape);
		
		if (hasClipTool(player))
			switchClipShape(player, shape);
		
		return true;
	}
	
	//that obviously is limited to rectangles and ellipses...
	private void switchClipShape(Player player, ClipShape newShape) {
		
		ClipTool clipTool = getClipTool(player);
		hideClipToolOf(player, true);
		
		List<Location> vertices = clipTool.getVertices();
		List<Location> definingVertices = new ArrayList<>();
		
		definingVertices.add(vertices.get(0));
		definingVertices.add(vertices.get(2));
		
		clipTool.setClip(ClipFactory.createClip(newShape, definingVertices));
		clipTool.setVertices(ClipFactory.createCompleteVertexList(definingVertices, newShape));
		clipTool.setShape(newShape);
		
		displayClipToolOf(player);
	}
	
	public void handleClipInteraction(Player player, Block clickedBlock) {
		
		if (!hasClipTool(player))
			setClipTool(player, new ClipTool(player, getClipShape(player)));
		
		ClipTool clipTool = getClipTool(player);
		
		if (clickedBlock.getWorld() != clipTool.getWorld()) {
			clipVisibilities.remove(clipTool);
			clipTool = new ClipTool(player, getClipShape(player));
			setClipTool(player, clipTool);
		}
		
		if (clipTool.isBeingReshaped()) {
			completeReshapingClip(clipTool, clickedBlock);
			return;
		}
		
		if (clipTool.hasClip()) {
			
			if (clipTool.isVertexBlock(clickedBlock)) {
				clipTool.startShiftingVertex(clipTool.getVertex(clickedBlock));
				return;
				
			} else {
				hideClipToolOf(player, true);
				clipTool = new ClipTool(player, getClipShape(player));
				setClipTool(player, clipTool);
			}
		}
		
		addVertexToClipTool(clipTool, clickedBlock);
	}
	
	private void addVertexToClipTool(ClipTool clipTool, Block clickedBlock) {
		
		ClipShape clipShape = getClipShape(clipTool.getPlayer());
		List<Location> vertices = clipTool.getVertices();
		
		vertices.add(BlockUtils.nearestSurface(clickedBlock.getLocation()).getLocation());
		
		if (vertices.size() == clipShape.getRequiredVertexCount()) {
			clipTool.setClip(ClipFactory.createClip(clipShape, vertices));
			clipTool.setVertices(ClipFactory.createCompleteVertexList(vertices, clipShape));
		}
		
		displayClipToolOf(clipTool.getPlayer());
	}
	
	private void completeReshapingClip(ClipTool clipTool, Block newVertexBlock) {
		
		Player player = clipTool.getPlayer();
		hideClipToolOf(player, true);
		ClipShape clipShape = clipTool.getShape();
		
		switch (clipShape) {
			
			case RECTANGLE:
			case ELLIPSE:
				
				List<Location> newDefiningVertices = createNewDefiningVertices(clipTool, newVertexBlock);
				clipTool.setClip(ClipFactory.createClip(clipShape, newDefiningVertices));
				clipTool.setVertices(ClipFactory.createCompleteVertexList(newDefiningVertices, clipShape));
				break;
			
			default:
				break;
		}
		
		displayClipToolOf(player);
	}
	
	private List<Location> createNewDefiningVertices(ClipTool reshapedClipTool, Block newVertexBlock) {
		
		List<Location> vertices = reshapedClipTool.getVertices();
		List<Location> newDefiningVertices = new ArrayList<>();
		
		int indexOfOldVertex = vertices.indexOf(reshapedClipTool.getShiftedVertex());
		int indexOfVertexOppositeToOldVertex = (indexOfOldVertex + 6) % 4;
		
		newDefiningVertices.add(vertices.get(indexOfVertexOppositeToOldVertex));
		newDefiningVertices.add(BlockUtils.nearestSurface(newVertexBlock.getLocation()).getLocation());
		
		return newDefiningVertices;
	}
	
	public ClipTool requireCompletedClipTool(Player player) {
		
		if (!hasStartedClipTool(player)) {
			Messages.ERROR_CLIPBOARD_NOT_STARTED.sendTo(player);
			player.sendMessage("/tangledmaze wand");
			return null;
		}
		
		ClipTool clipTool = getClipTool(player);
		
		if (!clipTool.hasClip()) {
			Messages.ERROR_CLIPBOARD_NOT_COMPLETED.sendTo(player);
			return null;
		}
		
		return clipTool;
	}
	
	public boolean isClipToolVisible(ClipTool clipTool) {
		return clipVisibilities.getOrDefault(clipTool, false);
	}
	
	public void displayClipToolOf(Player player) {
		
		if (!hasStartedClipTool(player))
			return;
		
		ClipTool clipTool = getClipTool(player);
		
		Map<Material, Collection<Location>> blocksToDisplay = new LinkedHashMap<>();
		
		if (clipTool.hasClip()) {
			Clip clip = clipTool.getClip();
			blocksToDisplay.put(Constants.CLIPBOARD_BORDER, clip.getBlockLocs(clip.getBorder()));
		}
		
		blocksToDisplay.put(Constants.CLIPBOARD_VERTEX, clipTool.getVertices());
		RenderUtils.sendBlocksDelayed(player, blocksToDisplay, plugin);
		clipVisibilities.put(clipTool, true);
	}
	
	//hides a clipboard completely with the option to redisplay previously covered maze parts
	public void hideClipToolOf(Player player, boolean updateMaze) {
		
		if (!hasStartedClipTool(player))
			return;
		
		ClipTool clipTool = getClipTool(player);
		
		if(!isClipToolVisible(clipTool))
			return;
		
		for (Location vertex : clipTool.getVertices())
			player.sendBlockChange(vertex, vertex.getBlock().getBlockData());
		
		if (clipTool.hasClip()) {
			for (Location border : clipTool.getClip().getBlockLocs(clipTool.getClip().getBorder()))
				player.sendBlockChange(border, border.getBlock().getBlockData());
		}
		
		if (updateMaze && mazeHandler.hasStartedMaze(player))
			redisplayMaze(clipTool, player);
		
		clipVisibilities.put(clipTool, false);
	}
	
	public void redisplayClipToolBlock(Player player, Block block) {
		
		ClipTool clipTool = getClipTool(player);
		Location blockLoc = block.getLocation();
		
		if (clipTool.isVertexBlock(block))
			RenderUtils.sendBlockDelayed(player, blockLoc, Constants.CLIPBOARD_VERTEX, plugin);
		else
			RenderUtils.sendBlockDelayed(player, blockLoc, Constants.CLIPBOARD_BORDER, plugin);
	}
	
	private void redisplayMaze(ClipTool clipTool, Player player) {
		
		Maze maze = mazeHandler.getMaze(player);
		
		if (!mazeHandler.isMazeVisible(mazeHandler.getMaze(player)))
			return;
		
		Clip clip = clipTool.getClip();
		Clip mazeClip = maze.getClip();
		
		for (Location border : clip.getBlockLocs(clip.getBorder())) {
			if (mazeClip.isBorderBlock(border.getBlock()))
				mazeHandler.redisplayMazeBlock(player, border.getBlock());
		}
		
		for (Location vertex : clipTool.getVertices()) {
			if (mazeClip.isBorderBlock(vertex.getBlock()))
				mazeHandler.redisplayMazeBlock(player, vertex.getBlock());
		}
	}
	
	public void hideAllClues() {
		
		for (Map.Entry<UUID, ClipTool> entry : playerClipTools.entrySet()) {
			if (isClipToolVisible(entry.getValue()))
				hideClipToolOf(Bukkit.getPlayer(entry.getKey()), false);
			
		}
	}
}