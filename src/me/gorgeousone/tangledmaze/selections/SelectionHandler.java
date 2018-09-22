package me.gorgeousone.tangledmaze.selections;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shapes.Shape;

public abstract class SelectionHandler {
	
	private static HashMap<UUID, Selection> selections = new HashMap<>();

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
	
	public static void setSelection(Player p, Selection sselection) {
		selections.put(p.getUniqueId(), sselection);
		
		if(sselection instanceof ShapeSelection)
			Renderer.registerShape((ShapeSelection) sselection);
	}
	
	public static void resetToDefaultSel(Player p) {
		
		if(hasShapeSel(p)) {
			getShapeSel(p).reset();
		
		}else {
			setSelection(p, new ShapeSelection(p, Shape.RECT));
		}
	}
	
	public static void removeSelection(Player p) {
		if(hasShapeSel(p))
			Renderer.unregisterShape(getShapeSel(p));

		selections.remove(p.getUniqueId());
	}
}