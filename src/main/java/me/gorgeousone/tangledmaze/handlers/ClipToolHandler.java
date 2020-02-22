package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.ClipFactory;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.BlockVec;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClipToolHandler {
	
	private Renderer renderer;
	private Map<UUID, ClipTool> playerClipTools;
	private Map<UUID, ClipShape> clipShapes;
	
	public ClipToolHandler(Renderer renderer) {
		
		this.renderer = renderer;
		playerClipTools = new HashMap<>();
		clipShapes = new HashMap<>();
	}
	
	public Collection<ClipTool> getPlayerClipTools() {
		return playerClipTools.values();
	}
	
	public ClipTool getClipTool(Player player) {
		return playerClipTools.get(player.getUniqueId());
	}
	
	public void setClipTool(Player player, ClipTool clipTool) {
		
		if (hasStartedClipTool(player))
			renderer.hideClipboard(getClipTool(player), true);
		
		UUID uuid = player.getUniqueId();
		playerClipTools.put(uuid, clipTool);
		renderer.displayClipboard(clipTool);
	}
	
	public boolean hasClipTool(Player player) {
		return playerClipTools.containsKey(player.getUniqueId());
	}
	
	public boolean hasStartedClipTool(Player player) {
		return playerClipTools.containsKey(player.getUniqueId()) && getClipTool(player).isStarted();
	}
	
	public void removeClipTool(Player player) {
		
		if (hasStartedClipTool(player)) {
			renderer.hideClipboard(getClipTool(player), true);
			renderer.unregisterClipTool(getClipTool(player));
			playerClipTools.remove(player.getUniqueId());
		}
	}
	
	public ClipShape getClipShape(Player player) {
		return clipShapes.getOrDefault(player.getUniqueId(), ClipShape.RECTANGLE);
	}
	
	public boolean setClipShape(Player player, ClipShape shape) {
		
		if (shape == getClipShape(player))
			return false;
		
		//TODO actually change clip's shape if player has a cliptool
		clipShapes.put(player.getUniqueId(), shape);
		
		if(hasClipTool(player))
			switchClipShape(getClipTool(player), shape);
		
		return true;
	}
	
	public void handleClipInteraction(Player player, Block clickedBlock) {
		
		if (!hasClipTool(player))
			setClipTool(player, new ClipTool(player, getClipShape(player)));
		
		ClipTool clipTool = getClipTool(player);
		
		if (clickedBlock.getWorld() != clipTool.getWorld()) {
			
			//TODO reset cliptool instead of creating new one or unregister old cliptool from Renderer
			renderer.hideClipboard(clipTool, true);
			setClipTool(player, new ClipTool(player, getClipShape(player)));
		}
		
		if (clipTool.isBeingReshaped()) {
			completeReshapingClip(clipTool, clickedBlock);
			return;
		}
		
		if (clipTool.hasClip()) {
			
			if (clipTool.isVertex(clickedBlock)) {
				clipTool.startShiftingVertex(clipTool.getVertex(clickedBlock));
				return;
				
			} else {
				renderer.hideClipboard(clipTool, true);
				clipTool = new ClipTool(player, getClipShape(player));
				setClipTool(player, clipTool);
			}
		}
		
		addVertexToClip(clipTool, clickedBlock);
	}
	
	private void addVertexToClip(ClipTool clipTool, Block clickedBlock) {
		
		ClipShape clipShape = getClipShape(clipTool.getPlayer());
		List<BlockVec> vertices = clipTool.getVertices();
		
		vertices.add(new BlockVec(BlockUtils.nearestSurface(clickedBlock.getLocation())));
		
		if (vertices.size() == clipShape.getRequiredVertexCount()) {
			clipTool.setClip(ClipFactory.createClip(clipShape, vertices));
			clipTool.setVertices(ClipFactory.createCompleteVertexList(vertices, clipShape));
		}
		
		renderer.displayClipboard(clipTool);
	}
	
	private void completeReshapingClip(ClipTool clipTool, Block newVertexBlock) {
		
		renderer.hideClipboard(clipTool, true);
		ClipShape clipShape = clipTool.getShape();
		
		switch (clipShape) {
			
			case RECTANGLE:
			case ELLIPSE:
				
				List<BlockVec> newDefiningVertices = createNewDefiningVertices(clipTool, newVertexBlock);
				clipTool.setClip(ClipFactory.createClip(clipShape, newDefiningVertices));
				clipTool.setVertices(ClipFactory.createCompleteVertexList(newDefiningVertices, clipShape));
				break;
			
			default:
				break;
		}
		
		renderer.displayClipboard(clipTool);
	}
	
	//that obviously is limited to rectangles and ellipses...
	private void switchClipShape(ClipTool clipTool, ClipShape newShape) {
		
		renderer.hideClipboard(clipTool, true);
		
		List<BlockVec> vertices = clipTool.getVertices();
		List<BlockVec> definingVertices = new ArrayList<>();
		
		definingVertices.add(vertices.get(0));
		definingVertices.add(vertices.get(2));
		
		clipTool.setClip(ClipFactory.createClip(newShape, definingVertices));
		clipTool.setVertices(ClipFactory.createCompleteVertexList(definingVertices, newShape));
		clipTool.setShape(newShape);
		
		renderer.displayClipboard(clipTool);
	}
	
	private List<BlockVec> createNewDefiningVertices(ClipTool clipTool, Block newVertexBlock) {
		
		List<BlockVec> vertices = clipTool.getVertices();
		List<BlockVec> newDefiningVertices = new ArrayList<>();
		
		int indexOfOldVertex = vertices.indexOf(clipTool.getShiftedVertex());
		int indexOfVertexOppositeToOldVertex = (indexOfOldVertex + 6) % 4;
		
		newDefiningVertices.add(vertices.get(indexOfVertexOppositeToOldVertex));
		newDefiningVertices.add(new BlockVec(BlockUtils.nearestSurface(newVertexBlock.getLocation())));
		
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
}
