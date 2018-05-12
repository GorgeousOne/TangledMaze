package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Entry;
import me.tangledmaze.gorgeousone.utils.Utils;

public class MazeBuilder {

	private static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3,
		EXIT = 4;
	
	private ArrayList<Maze> mazeQueue;
	private MazeHandler mHandler;
	
	public MazeBuilder() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
		mazeQueue = new ArrayList<>();
	}
	
	public boolean isInQueue(Player p) {
		for(Maze maze : mazeQueue)
			if(p.equals(maze.getOwner()))
				return true;
		return false;
	}
	
	public int enqueueMaze(Maze maze) {
		mazeQueue.add(maze);
		
		if(mHandler.isVisible(maze))
			mHandler.hide(maze);
		
		if(mazeQueue.size() == 1)
			buildNextMaze();
		
		return mazeQueue.indexOf(maze);
	}
	
	public void discard(Maze maze) {
		for(Maze maze2 : mazeQueue) {
			if(maze2.equals(maze)) {
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

		int[][] mazeMap = new int[maxX - minX + 16][maxZ - minZ + 16],
				mazeYMap  = new int[maxX - minX + 16][maxZ - minZ + 16];
			
		for(ArrayList<Location> chunk : maze.getFill().values())
			for(Location point : chunk) {
				mazeMap [point.getBlockX() - minX][point.getBlockZ() - minZ] = UNDEFINED;
				mazeYMap[point.getBlockX() - minX][point.getBlockZ() - minZ] = point.getBlockY();
			}
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				mazeMap[point.getBlockX() - minX][point.getBlockZ() - minZ] = WALL;
		
		for(Location exit : maze.getExits()) {
			mazeMap[exit.getBlockX() - minX][exit.getBlockZ() - minZ] = EXIT;
			
			for(Vector dir : Utils.cardinalDirs()) {
				Location exit2 = exit.clone().add(dir);
				
				if(maze.contains(exit2) && !maze.borderContains(exit2)) {
					mazeMap[exit2.getBlockX() - minX][exit2.getBlockZ() - minZ] = EXIT;
					break;
				}
			}
		}
		
		Vector start = maze.getExits().get(0).toVector().add(new Vector(-minX, 0, -minZ));
		
		generatePaths(maze, mazeMap, mazeYMap, start, minX, minZ);
	}
	
	private void generatePaths(Maze maze, int[][] mazeMap, int[][] mazeYMap, Vector start, int shiftX, int shiftZ) {
		
		BukkitRunnable pathGenerator = new BukkitRunnable() {
			@Override
			public void run() {
				
				Random rnd = new Random();
				ArrayList<Vector> openEnds = new ArrayList<>();
				openEnds.add(start);
				
				int pathLength = 0;
				boolean isFirstLoop = true;
				ArrayList<Vector> directions = Utils.cardinalDirs();
				
				mazefilling:
				while(!openEnds.isEmpty()) {
					
					Vector lastEnd;
					
					if(pathLength < 3)
						lastEnd = openEnds.get(openEnds.size()-1);
					else {
						lastEnd = openEnds.get(rnd.nextInt(openEnds.size()));
						pathLength = 0;
					}
					
					Collections.shuffle(directions);
					
					for(Vector dir : directions) {
						
						Vector path = lastEnd.clone().add(dir),
							   newEnd = path.clone().add(dir);
						
						if(path.getBlockX() < 0 || path.getBlockX() >= mazeMap.length ||
						   path.getBlockZ() < 0 || path.getBlockZ() >= mazeMap[0].length)
							continue;

						if(mazeMap[path.getBlockX()][path.getBlockZ()] != UNDEFINED &&
						   mazeMap[path.getBlockX()][path.getBlockZ()] != EXIT)
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
						   mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] != EXIT)
							continue;
						
						mazeMap[  path.getBlockX()][  path.getBlockZ()] = PATH;
						mazeMap[newEnd.getBlockX()][newEnd.getBlockZ()] = PATH;
						
						openEnds.add(newEnd);
						pathLength++;
						continue mazefilling;
					}

					openEnds.remove(lastEnd);
					pathLength = 0;
				}
				MazeBuilder.this.showMaze(maze, mazeMap, mazeYMap, shiftX, shiftZ);
			}
		};
		pathGenerator.runTaskAsynchronously(TangledMain.getPlugin());
	}
	
	public void showMaze(Maze maze, int[][] mazeMap, int[][] mazeYMap, int shiftX, int shiftZ) {
		
		ArrayList<Vector> directions = Utils.directions();
		ArrayList<Entry<Material, Byte>> composition = maze.getWallComposition();
		
		Random rnd = new Random();
		int wallHeight = maze.getWallHeight();
		
		ArrayList<Block> placeables = new ArrayList<>();
		int pointY, maxY;
		
		for(int x = 0; x < mazeMap.length; x++) {
			for(int z = 0; z < mazeMap[0].length; z++) {
				
				if(mazeMap[x][z] != WALL && mazeMap[x][z] != UNDEFINED)
					continue;
				
				pointY = mazeYMap[x][z];

				ArrayList<Integer> neighborYs = new ArrayList<>();
				neighborYs.add(pointY);
				
				for(Vector dir : directions) {
					int x2 = x + dir.getBlockX(),
						z2 = z + dir.getBlockZ();
					
					if(x2 < 0 || x2 >= mazeMap.length ||
					   z2 < 0 || z2 >= mazeMap[0].length)
						continue;
					
					neighborYs.add(mazeYMap[x + dir.getBlockX()][z + dir.getBlockZ()]);
				}
				
				maxY = Utils.getMax(neighborYs);
				
				for(int i = pointY+1; i <= maxY + wallHeight; i++) {
					Block b = (new Location(maze.getWorld(), x+shiftX, i, z+shiftZ)).getBlock();
					
					if(Utils.canBeReplaced(b.getType()))
						placeables.add(b);
				}
			}
		}

		BukkitRunnable builder = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				Entry<Material, Byte> rndBlockType;
				
				while(!placeables.isEmpty()) {
					
					Block b = placeables.get(0);
					placeables.remove(b);
					
					rndBlockType = composition.get(rnd.nextInt(composition.size()));
					b.setType(rndBlockType.getKey());
					b.setData(rndBlockType.getValue());
					
					if(System.currentTimeMillis() - timer >= 10)
						return;
				}
						
				this.cancel();
				
				if(maze.getOwner() != null)
					maze.getOwner().sendMessage(Constants.prefix + "Your maze has been finished!");
				
				mazeQueue.remove(maze);
				buildNextMaze();				
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 2);
	}
}