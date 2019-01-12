package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.utils.MazePoint;

public class MazeAction {
	
	private ArrayList<MazePoint> addedFill, removedFill, addedBorder, removedBorder, removedExits;
	
	public MazeAction() {
		addedFill     = new ArrayList<>();
		addedBorder   = new ArrayList<>();
		removedFill   = new ArrayList<>();
		removedBorder = new ArrayList<>();
		removedExits  = new ArrayList<>();
	}
	
	public ArrayList<MazePoint> getAddedFill() {
		return addedFill;
	}

	public ArrayList<MazePoint> getRemovedFill() {
		return removedFill;
	}

	public ArrayList<MazePoint> getAddedBorder() {
		return addedBorder;
	}
	
	public ArrayList<MazePoint> getRemovedBorder() {
		return removedBorder;
	}
	
	public ArrayList<MazePoint> getRemovedExits() {
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
	
	public MazeAction invert() {
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
}