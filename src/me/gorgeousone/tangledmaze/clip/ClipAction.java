package me.gorgeousone.tangledmaze.clip;

import java.util.ArrayList;
import java.util.HashSet;

import me.gorgeousone.tangledmaze.util.MazePoint;

public class ClipAction {
	
	private HashSet<MazePoint> addedFill, removedFill, addedBorder, removedBorder, removedExits;
	
	public ClipAction() {
		addedFill     = new HashSet<>();
		addedBorder   = new HashSet<>();
		removedFill   = new HashSet<>();
		removedBorder = new HashSet<>();
		removedExits  = new HashSet<>();
	}
	
	public HashSet<MazePoint> getAddedFill() {
		return addedFill;
	}

	public HashSet<MazePoint> getRemovedFill() {
		return removedFill;
	}

	public HashSet<MazePoint> getAddedBorder() {
		return addedBorder;
	}
	
	public HashSet<MazePoint> getRemovedBorder() {
		return removedBorder;
	}
	
	public HashSet<MazePoint> getRemovedExits() {
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
		ArrayList<MazePoint> temporaryHolder = new ArrayList<MazePoint>(addedFill);
		
		addedFill.clear();
		addedFill.addAll(removedFill);
		removedFill.clear();
		removedFill.addAll(temporaryHolder);
		
		temporaryHolder = new ArrayList<>(addedBorder);
		
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