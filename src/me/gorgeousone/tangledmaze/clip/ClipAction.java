package me.gorgeousone.tangledmaze.clip;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import java.util.HashMap;

import me.gorgeousone.tangledmaze.util.Vec2;

public class ClipAction {
	
	private Clip clip;
	
	private Set<Vec2>
		addedBorder,
		removedBorder,
		removedExits;
		
	private Map<Vec2, Integer>
		addedFill,
		removedFill;
	
	public ClipAction(Clip clip) {
		
		this.clip = clip;
		addedFill     = new HashMap<>();
		removedFill   = new HashMap<>();

		addedBorder   = new HashSet<>();
		removedBorder = new HashSet<>();
		removedExits  = new HashSet<>();
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
		return  removedExits;
	}
	
	public void addFill(Vec2 loc, int height) {
		addedFill.put(loc, height);
	}
	
	public void removeFill(Vec2 loc, int height) {
		removedFill.put(loc, height);
	}
	
	public void addBorder(Vec2 loc) {
		addedBorder.add(loc);
	}
	
	public void removeBorder(Vec2 loc) {
		removedBorder.add(loc);
	}
	
	public void removeExit(Vec2 loc) {
		removedExits.add(loc);
	}
	
	public Location getBorder(Vec2 loc) {
		
		int height;
		
		if(removedBorder.contains(loc)) {
			
			if(removedFill.containsKey(loc))
				height = removedFill.get(loc);
			else
				height = getClip().getHeight(loc);
			
		}else if(addedBorder.contains(loc)) {
			
			if(addedFill.containsKey(loc))
				height = addedFill.get(loc);
			else
				height = getClip().getHeight(loc);
		
		}else
			return null;
		
		return new Location(getClip().getWorld(), loc.getX(), height, loc.getZ());
	}

	public ClipAction invert() {
		
		HashMap<Vec2, Integer> temporaryHolder = new HashMap<>(addedFill);
		
		addedFill.clear();
		addedFill.putAll(removedFill);
		removedFill.clear();
		removedFill.putAll(temporaryHolder);
		
		HashSet<Vec2> temporaryHolder2= new HashSet<>(addedBorder);
		
		addedBorder.clear();
		addedBorder.addAll(removedBorder);
		removedBorder.clear();
		removedBorder.addAll(temporaryHolder2);
		
		getRemovedExits().clear();
		return this;
	}
	
	public boolean clipWillContain(Vec2 loc) {
		return getClip().contains(loc) && !getRemovedFill().containsKey(loc) || getAddedFill().containsKey(loc);
	}
	
	public boolean clipBorderWillContain(Clip clip, Vec2 loc) {
		return getAddedBorder().contains(loc) || !getRemovedBorder().contains(loc) && getClip().borderContains(loc);
	}
}