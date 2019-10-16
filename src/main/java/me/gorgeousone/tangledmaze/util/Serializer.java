package me.gorgeousone.tangledmaze.util;

import java.util.List;

import me.gorgeousone.tangledmaze.maze.MazeDimension;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.core.Maze;

public final class Serializer {

	private Serializer() {}
	
	public static void saveMaze(Maze maze, FileConfiguration config, String path) {
		
		config.set(path + ".world", maze.getWorld());
		saveClip(maze.getClip(), config, path + ".clip");
		config.set(path + ".exits", maze.getExits());
		
		for(MazeDimension dimension : MazeDimension.values())
			config.set(path + "." + dimension.name().toLowerCase().replace("_", "-"), maze.getDimension(dimension));
	}
	
	public static void saveClip(Clip clip, FileConfiguration config, String path) {
		
		config.set(path + ".fill", clip.getFillSet());
		config.set(path + ".border", clip.getBorder());
	}
	
	@SuppressWarnings("unchecked")
	public static Maze loadMaze(FileConfiguration config, String path) {
		
		World mazeWorld = Bukkit.getWorld(config.getString(path + ".world"));
		Maze maze = new Maze(mazeWorld);
		
		maze.setClip(loadClip(config, path + ".clip"));
		maze.getExits().addAll((List<Vec2>) config.getList(path + ".exits"));
		
		for(MazeDimension dimension : MazeDimension.values())
			maze.setDimension(dimension, config.getInt(path + "." + dimension.name().toLowerCase().replace("_", "-"), dimension.getDefault()));
		
		return maze;
	}

	public static Clip loadClip(FileConfiguration config, String path) {
		
		
		return null;
	}
}