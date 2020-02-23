package me.gorgeousone.tangledmaze.terrainmap;

public final class PathSegmentFactory {
	
	private PathSegmentFactory() {}
	//
	//	private void calculateDimensions(Vec2 start, Direction facing, int length, int width, boolean isExit) {
	//
	//
	//		Vec2 deltaStartToEnd = facing.getVec2().clone().mult(length - width);
	//		Vec2 end = start.clone().add(deltaStartToEnd);
	//
	//		relativeMin = new Vec2();
	//
	//		if (facing.isXAligned()) {
	//
	//			size = new Vec2(length, width);
	//
	//			if (!facing.isPositive()) {
	//				relativeMin = deltaStartToEnd;
	//
	//				if (isExit)
	//					translate(-width + 1, -width + 1);
	//			}
	//
	//		} else {
	//
	//			size = new Vec2(width, length);
	//
	//			if (!facing.isPositive()) {
	//				relativeMin = deltaStartToEnd;
	//
	//				if (isExit)
	//					translate(0, -width + 1);
	//
	//			} else if (isExit)
	//				translate(-width + 1, 0);
	//		}
	//	}
	//
	//	public static PathSegment createExit() {
	//
	//
	//	}
}
