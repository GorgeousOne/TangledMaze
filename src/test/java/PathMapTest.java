import me.gorgeousone.tangledmaze.terrainmap.paths.PathMap;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathMapTest {
	
	@Test
	void testPathGridOffset() {
		
		Vec2 clipMin = new Vec2(24, 24);
		Vec2 clipMax = new Vec2(44, 44);
		Vec2 pathStart = new Vec2(25, 25);
		
		int pathWidth = 3;
		int wallWidth = 2;
		
		PathMap testMap = new PathMap(clipMin, clipMax, pathStart, pathWidth, wallWidth);
		Vec2 gridOffset = testMap.getPathMapOffset();
		
		Assertions.assertEquals(20, gridOffset.getX());
		Assertions.assertEquals(20, gridOffset.getZ());
	}
	
	@Test
	void testGridSize() {
		
		Vec2 clipMin = new Vec2(24, 24);
		Vec2 clipMax = new Vec2(44, 44);
		Vec2 pathStart = new Vec2(25, 25);
		
		int pathWidth = 3;
		int wallWidth = 3;
		
		PathMap testMap = new PathMap(clipMin, clipMax, pathStart, pathWidth, wallWidth);
		
		Assertions.assertEquals(4, testMap.getGridWidth());
		Assertions.assertEquals(4, testMap.getGridHeight());
	}
	
//	private Clip createFakeRectClip(int startX, int startZ, int width, int height) {
//
//		Clip clip = new Clip(null);
//
//		for (int x = 0; x < width; x++) {
//			for (int z = 0; z < height; z++) {
//
//				Vec2 point = new Vec2(startX + x, startZ + z);
//				clip.addFill(point, 64);
//
//				if (x == 0 || x == width - 1 || z == 0 || z == height - 1)
//					clip.addBorder(point);
//			}
//		}
//
//		return clip;
//	}
}
