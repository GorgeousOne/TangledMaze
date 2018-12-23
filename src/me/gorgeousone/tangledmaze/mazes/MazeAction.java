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
	
	public void addFill(MazePoint l) {
		addedFill.add(l);
	}
	
	public void removeFill(MazePoint l) {
		removedFill.add(l);
	}
	
	public void addBorder(MazePoint l) {
		addedBorder.add(l);
	}
	
	public void removeBorder(MazePoint l) {
		removedBorder.add(l);
	}
	
	public void removeExit(MazePoint l) {
		removedExits.add(l);
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