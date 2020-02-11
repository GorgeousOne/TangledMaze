package me.gorgeousone.tangledmaze.handler;

import me.gorgeousone.tangledmaze.clip.ClipFactory;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.tool.ClipTool;
import me.gorgeousone.tangledmaze.util.BlockUtils;
import me.gorgeousone.tangledmaze.util.BlockVec;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClipToolHandler {

	private Map<UUID, ClipTool> playerClips;
	private Map<UUID, ClipShape> clipShapes;

	public ClipToolHandler() {
		playerClips = new HashMap<>();
		clipShapes = new HashMap<>();
	}

	public ClipTool getClipTool(Player player) {
		return playerClips.get(player.getUniqueId());
	}

	public void setClipTool(Player player, ClipTool clipTool) {
		UUID uuid = player.getUniqueId();
		playerClips.put(uuid, clipTool);
	}

	public boolean hasClipTool(Player player) {
		return playerClips.containsKey(player.getUniqueId());
	}

	//TODO check usages of removeClip and related Renderer usage (move renderer usage to this method)
	public void removeClipTool(Player player) {
		UUID uuid = player.getUniqueId();
		playerClips.remove(uuid);
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

			Renderer.hideClipboard(clipTool, true);
			clipTool = new ClipTool(player, getClipShape(player));
			setClipTool(player, clipTool);
		}

		if (clipTool.isBeingReshaped()) {
			completeReshapingClip(clipTool, clickedBlock);
			return;
		}

		if (clipTool.hasClip() && clipTool.isVertex(clickedBlock)) {
			clipTool.startShiftingVertex(clipTool.getVertex(clickedBlock));
			return;
		}

		addVertexToClip(clipTool, clickedBlock);
	}

	private void addVertexToClip(ClipTool clipTool, Block clickedBlock) {

		ClipShape clipShape = clipTool.getShape();
		List<BlockVec> vertices = clipTool.getVertices();

		vertices.add(new BlockVec(BlockUtils.nearestSurface(clickedBlock.getLocation())));

		Bukkit.broadcastMessage(ChatColor.GRAY + "" + vertices.size() + " vertices out of " + clipShape.getRequiredVertexCount());
		Bukkit.broadcastMessage(ChatColor.GRAY + "y: " + new BlockVec(BlockUtils.nearestSurface(clickedBlock.getLocation())).getY());

		if (vertices.size() == clipShape.getRequiredVertexCount()) {
			clipTool.setClip(ClipFactory.createClip(clipShape, vertices));
			clipTool.setVertices(ClipFactory.createCompleteVertexList(vertices, clipShape));
			Bukkit.broadcastMessage(ChatColor.GRAY + "Creating clip");
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
}
