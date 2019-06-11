package me.gorgeousone.tangledmaze.generation.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PathSection {
	
	private Random random;
	private List<PathSegment> openPathEnds;
	private Map<PathSegment, Integer> pathLengths;
	
	public PathSection(PathSegment start, Random random) {
		
		this.random = random;
		
		openPathEnds = new ArrayList<>();
		pathLengths = new HashMap<>();
		
		openPathEnds.add(start);
		pathLengths.put(start, 1);
	}
	
	public boolean hasOpenPathEnds() {
		return !openPathEnds.isEmpty();
	}
	
	public PathSegment getLastOpenPath() {
		return openPathEnds.get(openPathEnds.size()-1);
	}
	
	public PathSegment getRandomOpenPath() {
		return openPathEnds.get(random.nextInt(openPathEnds.size()));
	}
	
	public void addPath(PathSegment newPath, PathSegment parentPath) {
		
		openPathEnds.add(newPath);
		pathLengths.put(newPath, pathLengths.get(parentPath) + 1);
	}
	
	public void closePath(PathSegment path) {
		openPathEnds.remove(path);
	}
}