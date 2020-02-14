package me.gorgeousone.tangledmaze.mapmaking;

import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Vec2;

public class TerrainEditor {

	public void editTerrain(TerrainMap terrainMap) {

		levelOffSpikes(terrainMap);
		raiseLowWalls(terrainMap);
	}

	protected void levelOffSpikes(TerrainMap terrainMap) {

		for (int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if (terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
					continue;

				int floorHeight = terrainMap.getFloorHeight(x, z);
				int maxNeighborFloorHeight = terrainMap.getFloorHeight(getHighestNeighborFloor(x, z, terrainMap));

				if (floorHeight >= maxNeighborFloorHeight + 2)
					terrainMap.setFloorHeight(x, z, floorHeight + getAverageHeightDiffToNeighborFloors(terrainMap, x, z));
			}
		}
	}

	protected void raiseLowWalls(TerrainMap terrainMap) {

		int wallHeight = terrainMap.getMaze().getDimension(MazeDimension.WALL_HEIGHT);

		for (int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if (terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
					continue;

				Vec2 maxNeighborPath = getHighestNeighborFloor(x, z, terrainMap, MazeAreaType.PATH);

				if (maxNeighborPath == null)
					continue;

				int floorHeight = terrainMap.getFloorHeight(x, z);
				int maxNeighborFloorHeight = terrainMap.getFloorHeight(maxNeighborPath);

				if (maxNeighborFloorHeight > floorHeight)
					terrainMap.setWallHeight(x, z, wallHeight + maxNeighborFloorHeight - floorHeight);
			}
		}
	}

	protected Vec2 getHighestNeighborFloor(int x, int z, TerrainMap terrainMap) {
		return getHighestNeighborFloor(x, z, terrainMap, null);
	}

	protected Vec2 getHighestNeighborFloor(int x, int z, TerrainMap terrainMap, MazeAreaType areaType) {

		Vec2 maxNeighborFloor = null;
		int maxNeighborFloorHeight = 0;

		for (Directions dir : Directions.values()) {

			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());

			if (!terrainMap.contains(neighbor) || areaType != null && terrainMap.getAreaType(neighbor) != areaType)
				continue;

			int neighborFloorHeight = terrainMap.getFloorHeight(neighbor);

			if (neighborFloorHeight > maxNeighborFloorHeight) {

				maxNeighborFloor = neighbor;
				maxNeighborFloorHeight = neighborFloorHeight;
			}
		}
		return maxNeighborFloor;
	}

	protected int getAverageHeightDiffToNeighborFloors(TerrainMap terrainMap, int x, int z) {

		int floorHeight = terrainMap.getFloorHeight(x, z);
		int heightDiff = 0;
		int neighborsCount = 0;

		for (Directions dir : Directions.values()) {

			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());

			if (!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) == MazeAreaType.NOT_MAZE)
				continue;

			heightDiff += terrainMap.getFloorHeight(neighbor) - floorHeight;
			neighborsCount++;
		}
		return heightDiff / neighborsCount;
	}
}