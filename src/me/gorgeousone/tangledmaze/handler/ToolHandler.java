package me.gorgeousone.tangledmaze.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.tool.Tool;

public abstract class ToolHandler {
	
	private static HashMap<UUID, Tool> tools = new HashMap<>();

	public static boolean hasClipboard(Player p) {
		return
			tools.containsKey(p.getUniqueId()) &&
			tools.get(p.getUniqueId()) instanceof ClippingTool;
	}
	
	public static Tool getTool(Player p) {
		return tools.get(p.getUniqueId());
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
	
	public static void resetToDefaultTool(Player p) {
		
		//TODO low - check practicality
		if(hasClipboard(p)) {
			getClipboard(p).reset();
		
		}else {
			setTool(p, new ClippingTool(p, Shape.RECT));
		}
	}
	
	public static void removeTool(Player p) {
		if(hasClipboard(p))
			Renderer.unregisterShape(getClipboard(p));

		tools.remove(p.getUniqueId());
	}
}