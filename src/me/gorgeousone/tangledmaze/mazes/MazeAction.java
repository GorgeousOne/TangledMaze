package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;

import org.bukkit.Location;

public class MazeAction {
	
	private ArrayList<Location> addedFill, removedFill, addedBorder, removedBorder, removedExits;
	
	public MazeAction() {
		addedFill     = new ArrayList<>();
		addedBorder   = new ArrayList<>();
		removedFill   = new ArrayList<>();
		removedBorder = new ArrayList<>();
		removedExits  = new ArrayList<>();
	}
	
	public ArrayList<Location> getAddedFill() {
		return addedFill;
	}

	public ArrayList<Location> getRemovedFill() {
		return removedFill;
	}

	public ArrayList<Location> getAddedBorder() {
		return addedBorder;
	}
	
	public ArrayList<Location> getRemovedBorder() {
		return removedBorder;
	}
	
	public ArrayList<Location> getRemovedExits() {
		return  removedExits;
	}
	
	public void addFill(Location l) {
		addedFill.add(l);
	}
	
	public void removeFill(Location l) {
		removedFill.add(l);
	}
	
	public void addBorder(Location l) {
		addedBorder.add(l);
	}
	
	public void removeBorder(Location l) {
		removedBorder.add(l);
	}
	
	public void removeExit(Location l) {
		removedExits.add(l);
	}
	
	public MazeAction invert() {
		ArrayList<Location> temp = new ArrayList<Location>(addedFill);
		
		addedFill.clear();
		addedFill.addAll(removedFill);
		removedFill.clear();
		removedFill.addAll(temp);
		
		temp = new ArrayList<>(addedBorder);
		
		addedBorder.clear();
		addedBorder.addAll(removedBorder);
		removedBorder.clear();
		removedBorder.addAll(temp);
		
		getRemovedExits().clear();
		
		return this;
	}
}