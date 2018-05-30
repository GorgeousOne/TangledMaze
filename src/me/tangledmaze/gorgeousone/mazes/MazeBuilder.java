package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;

public class MazeBuilder {

	private static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3,
		EXIT = 4;
	
	private ArrayList<Maze> mazeQueue;
	private Maze currentMaze;
	
	private int[][] mazeMap, heightMap;
	private int mazeMinX, mazeMinZ;
	
	public MazeBuilder() {
		mazeQueue = new ArrayList<>();
	}
	
	public boolean isInQueue(Player p) {
		for(Maze maze : mazeQueue)
			if(p.equals(maze.getOwner()))
				return true;
		return false;
	}
	
	public int enqueueMaze(Maze maze) {
		
		if(mazeQueue.contains(maze))
			return -1;
		
		mazeQueue.add(maze);
		
		if(mazeQueue.size() == 1)
			prepareNextMaze();
		
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
	
	private void prepareNextMaze() {
		
		if(mazeQueue.isEmpty())
			return;
		
		currentMaze = mazeQueue.get(0);
		ArrayList<Chunk> chunks = currentMaze.getChunks();
		
		//select a probably random chunk from the maze's chunk list
		Chunk randomChunk = chunks.get(0);
		
		//initialize the maze's minimal and maximal coordinates with the chunk
		mazeMinX = randomChunk.getX();
		mazeMinZ = randomChunk.getZ();
		
		int
			mazeMaxX = mazeMinX,
			mazeMaxZ = mazeMinZ;
		
		//look for chunks with greater/smaller coordinates
		for(Chunk c : chunks) {
			if(c.getX() < mazeMinX)
				mazeMinX = c.getX();
			else if(c.getX() > mazeMaxX)
				mazeMaxX = c.getX();

			if(c.getZ() < mazeMinZ)
				mazeMinZ = c.getZ();
			else if(c.getZ() > mazeMaxZ)
				mazeMaxZ = c.getZ();
		}
		
		//multiply with a chunk's length to get to normal location coordinates
		mazeMinX *= 16;
		mazeMinZ *= 16;
		mazeMaxX *= 16;
		mazeMaxZ *= 16;
		
		//create two 2D-arrays, one for locations occupied by the maze, another one for the y-coordinates there
		mazeMap   = new int[mazeMaxX - mazeMinX + 16][mazeMaxZ - mazeMinZ + 16];
		heightMap = new int[mazeMaxX - mazeMinX + 16][mazeMaxZ - mazeMinZ + 16];
		
		//mark the maze's area in mazeMap as undefined area (open for path or walls), the will stay untouched
		for(ArrayList<Location> chunk : currentMaze.getFill().values())
			for(Location point : chunk) {
				mazeMap [point.getBlockX() - mazeMinX][point.getBlockZ() - mazeMinZ] = UNDEFINED;
				heightMap[point.getBlockX() - mazeMinX][point.getBlockZ() - mazeMinZ] = point.getBlockY();
			}
		
		//mark the border in mazeMap as reserved for walls
		for(ArrayList<Location> chunk : currentMaze.getBorder().values())
			for(Location point : chunk)
				mazeMap[point.getBlockX() - mazeMinX][point.getBlockZ() - mazeMinZ] = WALL;
		
		
		Vector start = null;
		int pathWidth = currentMaze.getDimensions().getBlockX();
		
		//mark exits in mazeMap as available for paths again
		for(Location exit : currentMaze.getExits()) {
			
			MazePath startEnd = new MazePath(
					exit.getBlockX() - mazeMinX - (pathWidth-1)/2,
					exit.getBlockZ() - mazeMinZ - (pathWidth-1)/2,
					pathWidth,
					pathWidth);
			
			for(Vector point : startEnd.getFill())
				if(point.getBlockX() >= 0 && point.getBlockX() < mazeMap.length ||
				   point.getBlockZ() >= 0 && point.getBlockZ() < mazeMap[0].length)
					mazeMap[point.getBlockX()][point.getBlockZ()] = EXIT;
			
			//if we are iterating over the first and main exit set it to start
			if(start == null)
				start = startEnd.getCorner();
		}

		generatePaths(start);
	}
	
	private void generatePaths(Vector start) {
		BukkitRunnable pathGenerator = new BukkitRunnable() {
			@Override
			public void run() {
				
				Random rnd = new Random();
				ArrayList<Vector> openEnds = new ArrayList<>();
				openEnds.add(start);
				
				int pathLength = 0;
				
				ArrayList<Vector> directions = Utils.cardinalDirs();
				Vector lastEnd;
				
				int
					pathWidth = currentMaze.getDimensions().getBlockX(),
					wallWidth = currentMaze.getDimensions().getBlockZ();
				
				mazefilling:
				while(!openEnds.isEmpty()) {
					
					if(pathLength < 3)
						lastEnd = openEnds.get(openEnds.size()-1);
					else {
						lastEnd = openEnds.get(rnd.nextInt(openEnds.size()));
						pathLength = 0;
					}
					
					Collections.shuffle(directions);
					
					directionsloop:
					for(Vector dir : directions) {
						
						MazePath
							path = new MazePath(
								lastEnd.getBlockX() + (dir.getX() > 0 ? pathWidth : 0) - (dir.getX() < 0 ? wallWidth : 0),
								lastEnd.getBlockZ() + (dir.getZ() > 0 ? pathWidth : 0) - (dir.getZ() < 0 ? wallWidth : 0),
								(dir.getX() == 0 ? pathWidth : wallWidth),
								(dir.getZ() == 0 ? pathWidth : wallWidth)),
								
							newEnd = new MazePath(
								lastEnd.getBlockX() + dir.getBlockX() * (pathWidth + wallWidth),
								lastEnd.getBlockZ() + dir.getBlockZ() * (pathWidth + wallWidth),
								pathWidth,
								pathWidth);
						
						for(Vector point : path.getFill()) {
							if(point.getBlockX() < 0 || point.getBlockX() >= mazeMap.length ||
							   point.getBlockZ() < 0 || point.getBlockZ() >= mazeMap[0].length)
								continue directionsloop;
							
							
							if(mazeMap[point.getBlockX()][point.getBlockZ()] != UNDEFINED &&
							   mazeMap[point.getBlockX()][point.getBlockZ()] != EXIT)
								continue directionsloop;
						}

						for(Vector point : newEnd.getFill()) {
							
							if(point.getBlockX() < 0 || point.getBlockX() >= mazeMap.length ||
							   point.getBlockZ() < 0 || point.getBlockZ() >= mazeMap[0].length)
								continue directionsloop;
	

							if(mazeMap[point.getBlockX()][point.getBlockZ()] != UNDEFINED &&
							   mazeMap[point.getBlockX()][point.getBlockZ()] != EXIT)
								continue directionsloop;
						}

						for(Vector point : path.getFill())
							mazeMap[point.getBlockX()][  point.getBlockZ()] = PATH;
						for(Vector point : newEnd.getFill())
							mazeMap[point.getBlockX()][point.getBlockZ()] = PATH;
						
						openEnds.add(newEnd.getCorner());
						pathLength++;
						continue mazefilling;
					}

					openEnds.remove(lastEnd);
					pathLength = 0;
				}
				MazeBuilder.this.buildMaze();
			}
		};
		pathGenerator.runTaskAsynchronously(TangledMain.getPlugin());
	}
	
	private void buildMaze() {
		
		ArrayList<Vector> directions = Utils.directions();
		ArrayList<MaterialData> composition = currentMaze.getWallComposition();
		
		int wallHeight = currentMaze.getDimensions().getBlockY();
		
		ArrayList<Block> placeables = new ArrayList<>();
		int pointY, maxY;
		
		for(int x = 0; x < mazeMap.length; x++) {
			for(int z = 0; z < mazeMap[0].length; z++) {
				
				if(mazeMap[x][z] != WALL && mazeMap[x][z] != UNDEFINED)
					continue;
				
				pointY = heightMap[x][z];

				ArrayList<Integer> neighborYs = new ArrayList<>();
				neighborYs.add(pointY);
				
				for(Vector dir : directions) {
					int x2 = x + dir.getBlockX(),
						z2 = z + dir.getBlockZ();
					
					if(x2 < 0 || x2 >= mazeMap.length ||
					   z2 < 0 || z2 >= mazeMap[0].length)
						continue;
					
					neighborYs.add(heightMap[x + dir.getBlockX()][z + dir.getBlockZ()]);
				}
				
				maxY = Utils.getMax(neighborYs);
				
				for(int i = pointY+1; i <= maxY + wallHeight; i++) {
					Block b = (new Location(currentMaze.getWorld(), x+mazeMinX, i, z+mazeMinZ)).getBlock();
					
					if(Utils.canBeReplaced(b.getType()))
						placeables.add(b);
				}
			}
		}

		Random rnd = new Random();
		
		BukkitRunnable builder = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				MaterialData rndMatData;
				
				while(!placeables.isEmpty()) {
					
					BlockState state = placeables.get(0).getState();
					placeables.remove(0);
					
					rndMatData = composition.get(rnd.nextInt(composition.size()));
					state.setType(rndMatData.getItemType());
					state.setRawData(rndMatData.getData());
					
					state.update(true, true);
					
					//idk... half a tick of every... is that ok? can i take all 50 ms?
					if(System.currentTimeMillis() - timer >= 40)
						return;
				}
				
				this.cancel();
				
				if(currentMaze.getOwner() != null)
					currentMaze.getOwner().sendMessage(Constants.prefix + "Your maze has been finished!");
				
				mazeQueue.remove(0);
				prepareNextMaze();
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 1);
	}
}