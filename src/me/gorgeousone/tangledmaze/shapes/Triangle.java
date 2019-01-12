package me.gorgeousone.tangledmaze.shapes;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.tools.Clip;
import me.gorgeousone.tangledmaze.utils.MazePoint;

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