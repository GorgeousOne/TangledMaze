package me.gorgeousone.tangledmaze.clip;

import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClipChange {
	
	private Clip clip;
	
	private Set<Vec2>
			addedBorder,
			removedBorder,
			removedExits;
	
	private Map<Vec2, Integer>
			addedFill,
			removedFill;
	
	public ClipChange(Clip clip) {
		
		this.clip = clip;
		addedFill = new HashMap<>();
		removedFill = new HashMap<>();
		
		addedBorder = new HashSet<>();
		removedBorder = new HashSet<>();
		removedExits = new HashSet<>();
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public Map<Vec2, Integer> getAddedFill() {
		return addedFill;
	}
	
	public Map<Vec2, Integer> getRemovedFill() {
		return removedFill;
	}
	
	public Set<Vec2> getAddedBorder() {
		return addedBorder;
	}
	
	public Set<Vec2> getRemovedBorder() {
		return removedBorder;
	}
	
	public Set<Vec2> getRemovedExits() {
		return removedExits;
	}
	
	public void addFill(Vec2 point, int height) {
		addedFill.put(point, height);
	}
	
	public void removeFill(Vec2 point, int height) {
		removedFill.put(point, height);
		
		if (clip.borderContains(point))
			removeBorder(point);
	}
	
	public void addBorder(Vec2 point) {
		addedBorder.add(point);
	}
	
	public void removeBorder(Vec2 point) {
		removedBorder.add(point);
	}
	
	public void removeExit(Vec2 point) {
		removedExits.add(point);
	}
	
	public Location getBorder(Vec2 point) {
		
		int height;
		
		if (removedBorder.contains(point)) {
			
			if (removedFill.containsKey(point))
				height = removedFill.get(point);
			else
				height = getClip().getHeight(point);
			
		} else if (addedBorder.contains(point)) {
			
			if (addedFill.containsKey(point))
				height = addedFill.get(point);
			else
				height = getClip().getHeight(point);
			
		} else
			return null;
		
		return new Location(getClip().getWorld(), point.getX(), height, point.getZ());
	}
	
	public ClipChange invert() {
		
		HashMap<Vec2, Integer> temporaryHolder = new HashMap<>(addedFill);
		
		addedFill.clear();
		addedFill.putAll(removedFill);
		removedFill.clear();
		removedFill.putAll(temporaryHolder);
		
		HashSet<Vec2> temporaryHolder2 = new HashSet<>(addedBorder);
		
		addedBorder.clear();
		addedBorder.addAll(removedBorder);
		removedBorder.clear();
		removedBorder.addAll(temporaryHolder2);
		
		getRemovedExits().clear();
		return this;
	}
	
	public boolean clipWillContain(Vec2 point) {
		return getClip().contains(point) && !getRemovedFill().containsKey(point) || getAddedFill().containsKey(point);
	}
	
	public boolean clipBorderWillContain(Vec2 point) {
		return getAddedBorder().contains(point) || !getRemovedBorder().contains(point) && getClip().borderContains(point);
	}
}