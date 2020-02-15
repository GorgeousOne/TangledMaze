package me.gorgeousone.tangledmaze.terrainmap;

import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.HashSet;
import java.util.Set;

public class PathSegment {

	private Vec2 start;
	private Vec2 end;
	private Vec2 size;
	private Vec2 relativeMin;

	private Directions facing;

	public PathSegment(Vec2 start, int length, int width, Directions facing, boolean isExit) {

		//the segment can either face aligned to x or z axis. in positive or negative direction
		this.start = start;
		this.facing = facing;

		calculateDimensions(length, width, isExit);
	}

	public Vec2 getStart() {
		return start.clone();
	}

	public Vec2 getEnd() {
		return end.clone();
	}

	public Directions getFacing() {
		return facing;
	}

	public Set<Vec2> getFill() {

		Set<Vec2> fill = new HashSet<>();

		Vec2 min = start.clone().add(relativeMin);
		Vec2 max = min.clone().add(size);

		for (int x = min.getX(); x < max.getX(); x++) {
			for (int z = min.getZ(); z < max.getZ(); z++) {
				fill.add(new Vec2(x, z));
			}
		}

		return fill;
	}

	public void expand(int blocks) {

		Vec2 expansion = facing.getVec2().clone().mult(blocks);

		size.add(expansion.getAbs());
		end.add(expansion);

		if (!facing.isPositive())
			relativeMin.add(expansion);
	}

	private void calculateDimensions(int length, int width, boolean isExit) {

		Vec2 deltaStartToEnd = facing.getVec2().clone().mult(length - width);

		end = start.clone().add(deltaStartToEnd);
		relativeMin = new Vec2();

		if (facing.isXAligned()) {

			size = new Vec2(length, width);

			if (!facing.isPositive()) {
				relativeMin = deltaStartToEnd;

				if (isExit)
					translate(-width + 1, -width + 1);
			}

		} else {

			size = new Vec2(width, length);

			if (!facing.isPositive()) {
				relativeMin = deltaStartToEnd;

				if (isExit)
					translate(0, -width + 1);

			} else if (isExit)
				translate(-width + 1, 0);
		}
	}

	public void translate(int blocksX, int blocksZ) {

		start.add(blocksX, blocksZ);
		end.add(blocksX, blocksZ);
	}
}