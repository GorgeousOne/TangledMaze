package me.gorgeousone.tangledmaze.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.tools.MazeToolType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.tools.Tool;
import org.bukkit.event.block.Action;

/**
 * This class stores tools in relation to players.
 * Listeners and commands can get information about what kind of tool a player is using.
 * There is an unsafe shortcut method for getting clipboards :)
 */
//TODO make a singelton out of this? Create one instance handed around by TangledMain
public class ToolHandler {

	private ClipToolHandler clipHandler;
	private Renderer renderer;

	private Map<UUID, MazeToolType> playersTools = new HashMap<>();

	public ToolHandler(Renderer renderer, ClipToolHandler clipHandler) {
		this.renderer = renderer;
		this.clipHandler = clipHandler;
	}

	//	public boolean hasClipboard(Player player) {
//		return
//			tools.containsKey(player.getUniqueId()) &&
//			tools.get(player.getUniqueId()) instanceof ClipTool;
//	}
	
	public MazeToolType getToolType(Player player) {
		
		if(!player.hasPermission(Constants.BUILD_PERM))
			return null;
		
		UUID uuid = player.getUniqueId();
		
		if(!playersTools.containsKey(uuid))
			setToolType(player, MazeToolType.CLIP_TOOL);

		return playersTools.get(player.getUniqueId());
	}
	
	public Collection<MazeToolType> getPlayersTools() {
		return playersTools.values();
	}
	
//	public ClipTool getClipboard(Player p) {
//		Tool clipboard = tools.get(p.getUniqueId());
//		return clipboard instanceof ClipTool ? (ClipTool) clipboard : null;
//	}
	
	public void setToolType(Player player, MazeToolType toolType) {
		playersTools.put(player.getUniqueId(), toolType);
	}

//	TODO check how to replace resetToDefaultTool correctly

	public void resetToDefaultTool(Player player) {

		if(clipHandler.hasClipTool(player))
			clipHandler.setClipShape(player, ClipShape.RECTANGLE);
		else
			clipHandler.setClipTool(player, new ClipTool(player, ClipShape.RECTANGLE));

		setToolType(player, MazeToolType.CLIP_TOOL);
	}

	public void removeTool(Player player) {

//		if(hasClipboard(player))
//			renderer.unregisterShape(getClipboard(player));

		playersTools.remove(player.getUniqueId());
	}

	public void handleToolInteraction(Player player, Block clickedBlock, Action action) {

//		Tool tool = getTool(player);
//
//		//TODO handle player interaction in this class, not in the tool classes separately.
//		if(clipHandler.hasClipTool(player))
//			clipHandler.handleClipInteraction(player, clickedBlock);
//		else
//			tool.interact(clickedBlock, action);

		switch (getToolType(player)) {



		}
	}
}