package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
	
	public int enqueueMaze(Maze maze) {
		mazeQueue.add(maze);
		maze.hide();
		
		if(mazeQueue.size() == 1)
			buildNextMaze();
		
		return mazeQueue.indexOf(maze);
	}
	
	public void discard(Player p) {
		for(Maze maze : mazeQueue) {
			if(p.equals(maze.getPlayer())) {
				mazeQueue.remove(maze);
				return;
			}
		}
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
		
		generatePaths(maze, mazeMap, start, minX, minZ);
	}
	
	private void generatePaths(Maze maze, int[][] mazeMap, Vector start, int shiftX, int shiftZ) {
		
		BukkitRunnable pathGenerator = new BukkitRunnable() {
			@Override
			public void run() {
				
				Stack<Vector> openEnds = new Stack<>();
				openEnds.push(start);
				
				boolean isFirstLoop = true;
				
				mazefilling:
				while(!openEnds.isEmpty()) {
					Vector lastEnd = openEnds.peek();
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
							openEnds.push(path);
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
						
						openEnds.push(newEnd);
						continue mazefilling;
					}

					openEnds.pop();
				}
				MazeBuilder.this.showMaze(maze, mazeMap, shiftX, shiftZ);
			}
		};
		pathGenerator.runTaskAsynchronously(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public void showMaze(Maze maze, int[][] mazeMap, int shiftX, int shiftZ) {
		
		BukkitRunnable builder = new BukkitRunnable() {
			@Override
			public void run() {
				
				Player p = maze.getPlayer();
				Random rnd = new Random();
				
				for(int x = 0; x < mazeMap.length; x++)
					for(int z = 0; z < mazeMap[0].length; z++) {
						
						if(mazeMap[x][z] != WALL && mazeMap[x][z] != UNDEFINED)
							continue;
						
						Location point = Utils.getNearestSurface(new Location(p.getWorld(), x+shiftX, maze.getY(x+shiftX,z+shiftZ), z+shiftZ));
						
						Block wall0 = point.clone().add(0, 1, 0).getBlock(),
							  wall1 = point.clone().add(0, 2, 0).getBlock();
						
						if(Utils.canBeReplaced(wall0)) {
							wall0.setType(Material.LEAVES);
							wall0.setData((byte) rnd.nextInt(5));
						}
						
						if(Utils.canBeReplaced(wall1)) {
							wall1.setType(Material.LEAVES);
							wall1.setData((byte) rnd.nextInt(5));
						}
						
						p.sendBlockChange(point, Material.LEAVES, (byte) (Math.random() * 4));
					}
				
				mazeQueue.remove(maze);
				buildNextMaze();				
			}
		};
		
		builder.runTask(TangledMain.getPlugin());
	}
}