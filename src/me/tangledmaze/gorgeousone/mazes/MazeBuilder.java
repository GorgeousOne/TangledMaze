package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.main.Utils;

public class MazeBuilder {

	private static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3,
		ENTRANCE = 4;
	
	private ArrayList<Maze> mazeQueue;
	
	public MazeBuilder() {
		mazeQueue = new ArrayList<>();
	}
	
	public int queueMaze(Maze maze) {
		mazeQueue.add(maze);

		if(mazeQueue.size() == 1)
			buildNextMaze();
		
		return mazeQueue.indexOf(maze);
	}
	
	private void buildNextMaze() {
		if(mazeQueue.isEmpty())
			return;
		
		Maze maze = mazeQueue.get(0);
		
		ArrayList<Chunk> chunks = maze.getChunks();
		Chunk first = chunks.get(0);
		
		int minX = first.getX(),
			minZ = first.getZ(),
			maxX = minX,
			maxZ = minZ;
		
		for(Chunk c : chunks) {
			if(c.getX() < minX)
				minX = c.getX();
			else if(c.getX() > maxX)
				maxX = c.getX();

			if(c.getZ() < minZ)
				minZ = c.getZ();
			else if(c.getZ() > maxZ)
				maxZ = c.getZ();
		}
		
		minX *= 16;
		maxX *= 16;
		minZ *= 16;
		maxZ *= 16;

		int[][] mazeMap = new int[maxX - minX + 16][maxZ - minZ + 16];
			
		for(ArrayList<Location> chunk : maze.getFill().values())
			for(Location point : chunk)
				mazeMap[point.getBlockX() - minX][point.getBlockZ() - minZ] = UNDEFINED;
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				mazeMap[point.getBlockX() - minX][point.getBlockZ() - minZ] = WALL;
		
		for(Location entrance : maze.getExits())
			mazeMap[entrance.getBlockX() - minX][entrance.getBlockZ() - minZ] = ENTRANCE;
		
		Vector start = maze.getExits().get(0).toVector().add(new Vector(-minX, 0, -minZ));
		
		maze.hide();
		generatePaths(maze, mazeMap, start, minX, minZ);
	}
	
	private void generatePaths(Maze maze, int[][] mazeMap, Vector start, int shiftX, int shiftZ) {
		BukkitRunnable pathGenerator = new BukkitRunnable() {
			@Override
			public void run() {
				
				ArrayList<Vector> openEnds = new ArrayList<>();
				openEnds.add(start);
				boolean isFirstLoop = true;
				
				mazefilling:
				while(!openEnds.isEmpty()) {
					Vector lastEnd = openEnds.get(openEnds.size()-1);
					
					for(Vector dir : Utils.shuffledCardinalDirs()) {
						
						Vector path = lastEnd.clone().add(dir),
							   newEnd = path.clone().add(dir);
						
						if(path.getBlockX() < 0 || path.getBlockX() >= mazeMap.length ||
						   path.getBlockZ() < 0 || path.getBlockZ() >= mazeMap[0].length)
							continue;

						if(mazeMap[path.getBlockX()][path.getBlockZ()] != UNDEFINED &&
						   mazeMap[path.getBlockX()][path.getBlockZ()] != ENTRANCE)
							continue;

						if(isFirstLoop) {
							mazeMap[path.getBlockX()][  path.getBlockZ()] = PATH;
							
							isFirstLoop = false;
							openEnds.add(path);
							continue mazefilling;
						}
						
						if(newEnd.getBlockX() < 0 || newEnd.getBlockX() >= mazeMap.length ||
						   newEnd.getBlockZ() < 0 || newEnd.getBlockZ() >= mazeMap[0].length)
							continue;

						if(mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] != UNDEFINED &&
						   mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] != ENTRANCE)
							continue;
						
						mazeMap[  path.getBlockX()][  path.getBlockZ()] = PATH;
						mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] = PATH;
						
						openEnds.add(newEnd);
						continue mazefilling;
					}

					openEnds.remove(lastEnd);
				}
				
				MazeBuilder.this.showMaze(maze, mazeMap, shiftX, shiftZ);
			}
		};
		pathGenerator.runTaskAsynchronously(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public void showMaze(Maze maze, int[][] mazeMap, int shiftX, int shiftZ) {
		Player p = maze.getPlayer();
		
		for(int x = 0; x < mazeMap.length; x++)
			for(int z = 0; z < mazeMap[0].length; z++) {
				
				if(mazeMap[x][z] != WALL && mazeMap[x][z] != UNDEFINED)
					continue;
				
				Location l = Utils.getNearestSurface(new Location(p.getWorld(), x+shiftX, maze.getY(x+shiftX,z+shiftZ), z+shiftZ));
				p.sendBlockChange(l, Material.LEAVES, (byte) (Math.random() * 4));
			}
		
		mazeQueue.remove(maze);
		buildNextMaze();
	}
}