package me.gorgeousone.tangledmaze.util;

import java.util.List;

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

		config.set(path + ".path-width", maze.getPathWidth());
		config.set(path + ".wall-width", maze.getWallWidth());
		config.set(path + ".wall-height", maze.getWallHeight());
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
		
		maze.setPathWidth(config.getInt(path + ".path-width", 1));
		maze.setWallWidth(config.getInt(path + ".wall-width", 1));
		maze.setWallHeight(config.getInt(path + ".wall-height", 2));
		
		return maze;
	}

	public static Clip loadClip(FileConfiguration config, String path) {
		
		
		return null;
	}
}