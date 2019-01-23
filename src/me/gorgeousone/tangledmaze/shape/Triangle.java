package me.gorgeousone.tangledmaze.shape;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.MazePoint;

public class Triangle implements Shape {
	
	@Override
	public int getVertexCount() {
		return 3;
	}
	
	@Override
	public Clip createClip(ArrayList<MazePoint> vertices) {
		return null;
	}
}