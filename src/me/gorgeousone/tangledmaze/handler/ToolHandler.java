package me.gorgeousone.tangledmaze.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.tool.Tool;

public abstract class ToolHandler {
	
	private static HashMap<UUID, Tool> tools = new HashMap<>();

	public static boolean hasClipboard(Player player) {
		return
			tools.containsKey(player.getUniqueId()) &&
			tools.get(player.getUniqueId()) instanceof ClippingTool;
	}
	
	public static Tool getTool(Player player) {
		return tools.get(player.getUniqueId());
	}
	
	public static ArrayList<Tool> getTools() {
		return new ArrayList<>(tools.values());
	}
	
	public static ClippingTool getClipboard(Player p) {
		Tool sel = tools.get(p.getUniqueId());
		return sel instanceof ClippingTool? (ClippingTool) sel : null;
	}
	
	public static void setTool(Player p, Tool tool) {
		tools.put(p.getUniqueId(), tool);
		
		if(tool instanceof ClippingTool)
			Renderer.registerClip((ClippingTool) tool);
	}
	
	public static void resetToDefaultTool(Player player) {
		
		if(hasClipboard(player)) {
			
			ClippingTool clipboard = getClipboard(player);
			
			if(clipboard.isStarted()) {
				
				Renderer.hideClipboard(clipboard, true);
				clipboard.reset();
			}
		
		}else {
			setTool(player, new ClippingTool(player, Shape.RECT));
		}
	}
	
	public static void removeTool(Player player) {
		if(hasClipboard(player))
			Renderer.unregisterShape(getClipboard(player));

		tools.remove(player.getUniqueId());
	}
}