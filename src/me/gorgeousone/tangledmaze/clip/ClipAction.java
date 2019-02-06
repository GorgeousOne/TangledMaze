package me.gorgeousone.tangledmaze.clip;

import java.util.HashSet;
import java.util.TreeSet;

import me.gorgeousone.tangledmaze.util.MazePoint;

public class ClipAction {
	
	private TreeSet<MazePoint>
		addedFill,
		removedFill,
		addedBorder,
		removedBorder,
		removedExits;
	
	public ClipAction() {
		addedFill     = new TreeSet<>();
		addedBorder   = new TreeSet<>();
		removedFill   = new TreeSet<>();
		removedBorder = new TreeSet<>();
		removedExits  = new TreeSet<>();
	}
	
	public TreeSet<MazePoint> getAddedFill() {
		return addedFill;
	}

	public TreeSet<MazePoint> getRemovedFill() {
		return removedFill;
	}

	public TreeSet<MazePoint> getAddedBorder() {
		return addedBorder;
	}
	
	public TreeSet<MazePoint> getRemovedBorder() {
		return removedBorder;
	}
	
	public TreeSet<MazePoint> getRemovedExits() {
		return  removedExits;
	}
	
	public void addFill(MazePoint point) {
		addedFill.add(point);
	}
	
	public void removeFill(MazePoint point) {
		removedFill.add(point);
	}
	
	public void addBorder(MazePoint point) {
		addedBorder.add(point);
	}
	
	public void removeBorder(MazePoint point) {
		removedBorder.add(point);
	}
	
	public void removeExit(MazePoint point) {
		removedExits.add(point);
	}
	
	public ClipAction invert() {
		HashSet<MazePoint> temporaryHolder = new HashSet<>(addedFill);
		
		addedFill.clear();
		addedFill.addAll(removedFill);
		removedFill.clear();
		removedFill.addAll(temporaryHolder);
		
		temporaryHolder = new HashSet<>(addedBorder);
		
		addedBorder.clear();
		addedBorder.addAll(removedBorder);
		removedBorder.clear();
		removedBorder.addAll(temporaryHolder);
		
		getRemovedExits().clear();
		return this;
	}
	
	public boolean clipWillContain(Clip clip, MazePoint point) {
		return (clip.contains(point) || addedFill.contains(point)) && !removedFill.contains(point);
	}
	
	public boolean clipBorderWillContain(Clip clip, MazePoint point) {
		return (clip.borderContains(point) || addedBorder.contains(point)) && !removedBorder.contains(point);
	}

}