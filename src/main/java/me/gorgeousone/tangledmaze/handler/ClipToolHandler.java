package me.gorgeousone.tangledmaze.handler;

import me.gorgeousone.tangledmaze.clip.ClipFactory;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.tool.ClipTool;
import me.gorgeousone.tangledmaze.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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

		if(shape == getClipShape(player))
			return false;

		//TODO actually change clip's shape if player has a cliptool
		clipShapes.put(player.getUniqueId(), shape);
		return true;
	}

	public void handleClipInteraction(Player player, Block clickedBlock) {

		if (!hasClipTool(player))
			setClipTool(player, new ClipTool(player, getClipShape(player)));

		ClipTool clipTool = getClipTool(player);
		ClipShape clipShape = clipTool.getShape();

		if (clickedBlock.getWorld() != clipTool.getWorld() || clipTool.hasClip()) {
			Renderer.hideClipboard(clipTool, true);

			clipTool = new ClipTool(player, getClipShape(player));
			setClipTool(player, clipTool);
		}

		List<Location> controlPoints = clipTool.getControlPoints();
		controlPoints.add(BlockUtils.nearestSurface(clickedBlock.getLocation()));

		Bukkit.broadcastMessage(ChatColor.GRAY + "" + controlPoints.size() + " points out of " + clipShape.getRequiredControlPointCount());

		if (controlPoints.size() == clipShape.getRequiredControlPointCount()) {
			clipTool.setClip(ClipFactory.createClip(clipShape, controlPoints));
			clipTool.setControlPoints(ClipFactory.createCompleteControlPointsList(controlPoints, clipShape));
			Bukkit.broadcastMessage(ChatColor.GRAY + "Creating clip");
		}

		Renderer.displayClipboard(clipTool);
//		else {
//
//			if (clipTool.isBeingResized()) {
//				resizeShape(clickedBlock);
//
//			} else if (clipTool.isVertex(clickedBlock)) {
//
//				clipTool.setIndexOfResizedVertex(clipTool.indexOfVertex(clickedBlock);
//				clipTool.setBeingResizing(true);
//				return;
//
//			} else {
//
//				Renderer.hideClipboard(this, true);
//				reset();
//				controlPoints.add(BlockUtils.nearestSurface(clickedBlock.getLocation()));
//			}
//		}
	}

	//	private void createNewClipTool(Player player, Block clickedBlock) {
	//		ClipTool clipTool = new ClipTool(player);
	//		clipTool.addControlPoint(BlockUtils.nearestSurface(clickedBlock.getLocation()));
	//	}
	//
	//	private void resetClipTool(ClipTool clipTool) {
	//		clipTool.setClip(null);
	//		clipTool.setControlPoints(new ArrayList<>());
	//	}

//	private void resizeShape(ClipTool clipTool, Block block) {
//
//		Renderer.hideClipboard(clipTool, true);
//		Location oppositeVertex = vertices.get((indexOfResizedVertex + 2) % 4);
//
//		vertices.clear();
//		vertices.add(oppositeVertex);
//		vertices.add(BlockUtils.nearestSurface(block.getLocation()));
//
//		calculateShape();
//	}

}
