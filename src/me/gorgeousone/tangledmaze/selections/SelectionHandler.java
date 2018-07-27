package me.gorgeousone.tangledmaze.selections;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Renderer;

public abstract class SelectionHandler {
	
	private static HashMap<UUID, Selection> selections = new HashMap<>();

	public static boolean hasSelection(Player p) {
		return selections.containsKey(p.getUniqueId());
	}
	
	public static boolean hasShapeSel(Player p) {
		return
			selections.containsKey(p.getUniqueId()) &&
			selections.get(p.getUniqueId()) instanceof ShapeSelection;
	}
	
	public static Selection getSelection(Player p) {
		return selections.get(p.getUniqueId());
	}
	
	public static ShapeSelection getShapeSel(Player p) {
		Selection sel = selections.get(p.getUniqueId());
		return sel instanceof ShapeSelection? (ShapeSelection) sel : null;
	}
	
	public static void setSelection(Player p, Selection shape) {
		selections.put(p.getUniqueId(), shape);
		
		if(shape instanceof ShapeSelection)
			Renderer.registerShape((ShapeSelection) shape);
	}
	
	public static void resetSelection(Player p) {
		
	}
}