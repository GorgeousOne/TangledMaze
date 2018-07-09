package me.tangledmaze.gorgeousone.mazes.generators;

import java.util.ArrayList;

import org.bukkit.util.Vector;

public class MazeSegment {
	
	public static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3,
		EXIT = 4;
	
	private Vector relativeMin, start, end;
	private int facingX, facingZ, deltaX, deltaZ;
	
	public MazeSegment(int startX, int startZ, int facingX, int facingZ, int length, int width, boolean isExit) {
		
		this.facingX = facingX;
		this.facingZ = facingZ;
		
		start = new Vector(startX, 0, startZ);
		end = start.clone();
		relativeMin = new Vector();
		
		if(facingX != 0) {
			
			deltaX = Math.abs(length);
			deltaZ = Math.abs(width);
			
			//move the end into the facing direction
			//this should not be confused with a maximum or minimum. the end is followed by a width-sized square ending the path  
			end.add(new Vector(facingX * (length - width), 0, 0));
			
			if(facingX == -1) {
				relativeMin = new Vector(-length + width, 0, 0);

				if(isExit)
					move(-width+1, -width+1);
			}
			
		}else {
			
			deltaZ = Math.abs(length);
			deltaX = Math.abs(width);
			
			end.add(new Vector(0, 0, facingZ * (length - width)));
			
			if(facingZ == -1) {
				relativeMin = new Vector(0, 0, -length + width);
				
				if(isExit)
					move(0, -width+1);
			
			}else if(isExit)
				move(-width+1, 0);
		}
	}
	
	private void move(int dx, int dz) {
		start.add(new Vector(dx, 0, dz));
		end.add(new Vector(dx, 0, dz));
	}
	
	public void expand(int dLength) {

		if(facingX != 0) {
			deltaX += dLength; 
			end.add(new Vector(facingX * dLength, 0, 0));
			
			if(facingX == -1)
				relativeMin.add(new Vector(-dLength, 0, 0));
			
		}else {
			deltaZ += dLength;
			end.add(new Vector(facingZ * dLength, 0, 0));
			
			if(facingZ == -1)
				relativeMin.add(new Vector(0, 0, -dLength));
		}
	}
	
	public Vector getStart() {
		return start.clone();
	}
	
	public Vector getEnd() {
		return end.clone();
	}
	
	public ArrayList<Vector> getFill() {
		ArrayList<Vector> fill = new ArrayList<>();
		
		Vector min = start.clone().add(relativeMin);
		Vector max = min.clone().add(new Vector(deltaX, 0, deltaZ));

		for(int x = min.getBlockX(); x < max.getBlockX(); x++)
			for(int z = min.getBlockZ(); z < max.getBlockZ(); z++)
				fill.add(new Vector(x, 0, z));
		
		return fill;
	}
}