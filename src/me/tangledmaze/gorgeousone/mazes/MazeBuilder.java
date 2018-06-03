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
	private Vector start;
	
	public MazeBuilder() {
		mazeQueue = new ArrayList<>();
	}
	
	public boolean isInQueue(Player p) {
		for(Maze maze : mazeQueue)
			if(p.equals(maze.getPlayer()))
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
		mazeQueue.remove(maze);
	}
	
	private void prepareNextMaze() {
		
		if(mazeQueue.isEmpty())
			return;
		
		currentMaze = mazeQueue.get(0);
		
		createMazeMaps();
		generateExits();
		generatePaths();
		buildMaze();
		
		if(currentMaze.getPlayer() != null)
			currentMaze.getPlayer().sendMessage(Constants.prefix + "Your maze has been finished!");
		
		mazeQueue.remove(currentMaze);
		prepareNextMaze();
	}
	
	private void createMazeMaps() {
		
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
	}
	
	private void generateExits() {
		start = null;
		
		int pathWidth = currentMaze.getDimensions().getBlockX(),
			wallWidth = currentMaze.getDimensions().getBlockZ();
		
		int
			defOffsetX = 0,
			defOffsetZ = 0;
		
		//mark exits in mazeMap as available for paths again
		for(Location exit : currentMaze.getExits()) {
			
			for(Vector dir : Utils.cardinalDirs()) {
				Location exit2 = exit.clone().add(new Vector(-mazeMinX, 0, -mazeMinZ)).add(dir);
				
				if(exit2.getBlockX() < 0 || exit2.getBlockX() >= mazeMap.length ||
				   exit2.getBlockZ() < 0 || exit2.getBlockZ() >= mazeMap[0].length)
					continue;
				
				if(mazeMap[exit2.getBlockX()][exit2.getBlockZ()] == UNDEFINED) {
					
					int
						endX = exit.getBlockX() - mazeMinX,
						endZ = exit.getBlockZ() - mazeMinZ;
						
					//make width for exit always "grow" inside the maze
					if(dir.getBlockX() < 0)
						endX -= pathWidth - 1;
					//center exit as good as possible on outline (horizontally)
					if(dir.getBlockX() == 0)
						if(pathWidth % 2 == 0)
							endX -= (pathWidth + dir.getBlockZ()) / 2;
						else
							endX -= (pathWidth - 1) / 2;
					
					if(dir.getBlockZ() < 0)
						endZ -= pathWidth-1;
					if(dir.getBlockZ() == 0)
						if(pathWidth % 2 == 0)
							endZ -= (pathWidth - dir.getBlockX()) / 2;
						else
							endZ -= (pathWidth - 1) / 2;
					
					int offsetX = endX - defOffsetX,
						offsetZ = endZ - defOffsetZ;
					
					if(offsetX < 0)
						offsetX += (pathWidth + wallWidth);
					if(offsetZ < 0)
						offsetZ += (pathWidth + wallWidth);
					
					offsetX %= (pathWidth + wallWidth);
					offsetZ %= (pathWidth + wallWidth);
					
					int extraX = 0,
						extraZ = 0;

					int exitType = EXIT;
					
					//true in the first loop for the main exit 
					if(start == null) {
						start = new Vector(
								endX + dir.getBlockX() * pathWidth, 0,
								endZ + dir.getBlockZ() * pathWidth);
					
						defOffsetX = offsetX + dir.getBlockX()*pathWidth;
						defOffsetZ = offsetZ + dir.getBlockZ()*pathWidth;

						if(defOffsetX < 0)
							defOffsetX += (pathWidth + wallWidth);
						if(defOffsetZ < 0)
							defOffsetZ += (pathWidth + wallWidth);
						
						defOffsetX %= (pathWidth + wallWidth);
						defOffsetZ %= (pathWidth + wallWidth);
						
						if(dir.getBlockX() != 0)
							extraX = pathWidth;
						else
							extraZ = pathWidth;
						
						exitType = PATH;
						
					}else {
						if(offsetX == 0)
							offsetX = pathWidth + wallWidth;
						if(offsetZ == 0)
							offsetZ = pathWidth + wallWidth;
						
						if(dir.getBlockX() != 0)
							extraX = offsetX;
						else
							extraZ = offsetZ;
					}
				
					MazePath firstEnd = new MazePath(
							endX - (dir.getX() < 0 ? extraX : 0),
							endZ - (dir.getZ() < 0 ? extraZ : 0),
							pathWidth + extraX,
							pathWidth + extraZ);

					for(Vector point : firstEnd.getFill())
						if(point.getBlockX() >= 0 && point.getBlockX() < mazeMap.length ||
						   point.getBlockZ() >= 0 && point.getBlockZ() < mazeMap[0].length)
							mazeMap[point.getBlockX()][point.getBlockZ()] = exitType;
				}
			}
		}
	}
	
	private void generatePaths() {
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
					state.update(true, false);
					
					if(System.currentTimeMillis() - timer >= 40)
						return;
				}
				
				this.cancel();
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 1);
	}
}