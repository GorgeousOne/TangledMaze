package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.clip.ClipFactory;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.maze.Maze;
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

	private Map<UUID, ClipTool> playerClipTools;
	private Map<UUID, ClipShape> clipShapes;

	public ClipToolHandler() {
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

		if(hasClipTool(player))
			Renderer.hideClipboard(getClipTool(player), true);

		UUID uuid = player.getUniqueId();
		playerClipTools.put(uuid, clipTool);
		Renderer.displayClipboard(clipTool);
	}

	public boolean hasClipTool(Player player) {
		return playerClipTools.containsKey(player.getUniqueId());
	}

	//TODO check usages of removeClip and related Renderer usage (move renderer usage to this method)
	public void removeClipTool(Player player) {
		UUID uuid = player.getUniqueId();
		playerClipTools.remove(uuid);
	}

	public ClipShape getClipShape(Player player) {
		return clipShapes.getOrDefault(player.getUniqueId(), ClipShape.RECTANGLE);
	}

	public boolean setClipShape(Player player, ClipShape shape) {

		if (shape == getClipShape(player))
			return false;

		//TODO actually change clip's shape if player has a cliptool
		clipShapes.put(player.getUniqueId(), shape);
		return true;
	}

	public void handleClipInteraction(Player player, Block clickedBlock) {

		if (!hasClipTool(player))
			setClipTool(player, new ClipTool(player, getClipShape(player)));

		ClipTool clipTool = getClipTool(player);

		if (clickedBlock.getWorld() != clipTool.getWorld()) {

			//TODO reset cliptool instead of creating new one or unregister old cliptool from Renderer
			Renderer.hideClipboard(clipTool, true);
			clipTool = new ClipTool(player, getClipShape(player));
			setClipTool(player, clipTool);
		}

		if (clipTool.isBeingReshaped()) {
			completeReshapingClip(clipTool, clickedBlock);
			return;
		}

		if (clipTool.hasClip()) {

			if(clipTool.isVertex(clickedBlock)) {
				clipTool.startShiftingVertex(clipTool.getVertex(clickedBlock));
				return;

			}else {
				Renderer.hideClipboard(clipTool, true);
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

		Renderer.displayClipboard(clipTool);
	}

	private void completeReshapingClip(ClipTool clipTool, Block newVertexBlock) {

		ClipShape clipShape = clipTool.getShape();
		List<BlockVec> vertices = clipTool.getVertices();

		Renderer.hideClipboard(clipTool, true);

		switch (clipShape) {

			case RECTANGLE:
			case ELLIPSE:

				int indexOfOldVertex = vertices.indexOf(clipTool.getShiftedVertex());
				int indexOfVertexOppositeToOldVertex = (indexOfOldVertex + 6) % 4;

				List<BlockVec> newDefiningVertices = new ArrayList<>();
				newDefiningVertices.add(vertices.get(indexOfVertexOppositeToOldVertex));
				newDefiningVertices.add(new BlockVec(newVertexBlock));

				clipTool.setClip(ClipFactory.createClip(clipShape, newDefiningVertices));
				clipTool.setVertices(ClipFactory.createCompleteVertexList(newDefiningVertices, clipShape));
				break;

			default:
				break;
		}

		Renderer.displayClipboard(clipTool);
	}

	public ClipTool requireCompletedClipTool(Player player) {

		if(!hasClipTool(player) || !getClipTool(player).isStarted()) {

			Messages.ERROR_CLIPBOARD_NOT_STARTED.sendTo(player);
			player.sendMessage("/tangledmaze wand");
			return null;
		}

		ClipTool clipTool = getClipTool(player);

		if(!clipTool.hasClip()) {
			Messages.ERROR_CLIPBOARD_NOT_COMPLETED.sendTo(player);
			return null;
		}

		return clipTool;
	}
}
