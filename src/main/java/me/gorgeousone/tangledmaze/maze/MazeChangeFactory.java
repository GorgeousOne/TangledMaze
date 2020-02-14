package me.gorgeousone.tangledmaze.maze;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public abstract class MazeChangeFactory {
	
	private MazeChangeFactory() {
	}
	
	/**
	 * Creates a {@link ClipChange} of the part of the passed clip that will be added to the maze.
	 * Returns null if worlds are not matching or the clip is completely covered by the maze.
	 * To apply the action use {@link me.gorgeousone.tangledmaze.handlers.MazeHandler#processClipChange(Maze, ClipChange)}
	 *
	 * @param clip Clip to be added to the maze
	 * @return ClipAction of the addition
	 */
	public static ClipChange createAddition(Maze maze, Clip clip) {
		
		if (!maze.getWorld().equals(clip.getWorld()))
			return null;
		
		Clip mazeClip = maze.getClip();
		ClipChange addition = new ClipChange(maze.getClip());
		
		addProtrudingClipFillToMaze(mazeClip, clip, addition);
		
		//return null if the clip is totally covered by the maze
		if (addition.getAddedFill().isEmpty())
			return null;
		
		addProtrudingClipBorderToMaze(mazeClip, clip, addition);
		removeEnclosedMazeBorder(mazeClip, addition);
		removeThickEnclosedMazeBorder(addition);
		removeMazeExitsInsideClip(maze.getExits(), clip, addition);
		return addition;
	}
	
	//every fill not included by this maze yet is being added
	private static void addProtrudingClipFillToMaze(Clip mazeClip, Clip otherClip, ClipChange addition) {
		
		for (Map.Entry<Vec2, Integer> otherFill : otherClip.getFillEntries()) {
			if (!mazeClip.contains(otherFill.getKey()))
				addition.addFill(otherFill.getKey(), otherFill.getValue());
		}
	}
	
	//right afterwards also the border of the other clip is being added
	private static void addProtrudingClipBorderToMaze(Clip mazeClip, Clip otherClip, ClipChange addition) {
		
		for (Vec2 otherBorder : otherClip.getBorder()) {
			if (!mazeClip.contains(otherBorder))
				addition.addBorder(otherBorder);
		}
	}
	
	//then own outdated border is being removed. there are also cases where thicker border next to the actual clip has to be removed
	private static void removeEnclosedMazeBorder(Clip mazeClip, ClipChange addition) {
		
		for (Vec2 ownBorder : mazeClip.getBorder()) {
			if (!touchesExternal(ownBorder, addition, Directions.values()))
				addition.removeBorder(ownBorder);
		}
	}
	
	//now the recently added border needs undergo another check, if it is actually sufficient and also not too thick
	private static void removeThickEnclosedMazeBorder(ClipChange addition) {
		addition.getAddedBorder().removeIf(newBorder -> !touchesExternal(newBorder, addition, Directions.values()));
	}
	
	//TODO switch exits's type to BlockVec (also in ClipChange
	private static void removeMazeExitsInsideClip(Stack<Vec2> exits, Clip otherClip, ClipChange changes) {
		
		for (Vec2 exit : exits) {
			if (otherClip.contains(exit))
				changes.removeExit(exit);
		}
	}
	
	/**
	 * Creates a {@link ClipChange} of the part of the passed clip that will be removed from the maze.
	 * Returns null if worlds are not matching or the clip is not intersecting the maze.
	 * To apply the action use {@link me.gorgeousone.tangledmaze.handlers.MazeHandler#processClipChange(Maze, ClipChange)}
	 *
	 * @param otherClip Clip to be removed from the maze
	 * @return ClipAction of the deletion
	 */
	public static ClipChange createDeletion(Maze maze, Clip otherClip) {
		
		if (!maze.getWorld().equals(otherClip.getWorld()))
			return null;
		
		Clip mazeClip = maze.getClip();
		ClipChange deletion = new ClipChange(mazeClip);
		
		removeOtherOverlappingClip(mazeClip, otherClip, deletion);
		
		if (deletion.getRemovedFill().isEmpty())
			return null;
		
		addIntersectingClipBorderToMaze(mazeClip, otherClip, deletion);
		removeExcludedMazeBorder(mazeClip, otherClip, deletion);
		removeMazeExitsInsideClip(maze.getExits(), otherClip, deletion);
		return deletion;
	}
	
	//every fill of the other clip overlapping the maze is being removed from the maze
	private static void removeOtherOverlappingClip(Clip mazeClip, Clip otherClip, ClipChange deletion) {
		
		for (Map.Entry<Vec2, Integer> otherFill : otherClip.getFillEntries()) {
			if (!otherClip.borderContains(otherFill.getKey()) && mazeClip.contains(otherFill.getKey()))
				deletion.removeFill(otherFill.getKey(), otherFill.getValue());
		}
	}
	
	private static void addIntersectingClipBorderToMaze(Clip mazeClip, Clip otherClip, ClipChange deletion) {
		
		for (Vec2 otherBorder : otherClip.getBorder()) {
			if (!mazeClip.borderContains(otherBorder) && mazeClip.contains(otherBorder))
				deletion.addBorder(otherBorder);
		}
		
		//remove every part of the new added border, which is not functional border anyway
		Iterator<Vec2> iterator = deletion.getAddedBorder().iterator();
		
		while (iterator.hasNext()) {
			Vec2 newBorder = iterator.next();
			
			if (!touchesFill(newBorder, deletion, Directions.values())) {
				iterator.remove();
				deletion.removeFill(newBorder, mazeClip.getHeight(newBorder));
			}
		}
	}
	
	private static void removeExcludedMazeBorder(Clip mazeClip, Clip otherClip, ClipChange deletion) {
		
		for (Vec2 ownBorder : mazeClip.getBorder()) {
			
			if (otherClip.contains(ownBorder) && !touchesFill(ownBorder, deletion, Directions.values())) {
				deletion.removeBorder(ownBorder);
				deletion.removeFill(ownBorder, mazeClip.getHeight(ownBorder));
			}
		}
	}
	
	/**
	 * Creates a {@link ClipChange} with information about blocks in order to expand the maze border at the given block.
	 * Returns null if the block is not part of the maze border (see {@link Clip#isBorderBlock(Block)}).
	 * To apply the action use {@link me.gorgeousone.tangledmaze.handlers.MazeHandler#processClipChange(Maze, ClipChange)}
	 *
	 * @param block the block where the maze border should be expanded
	 * @return ClipAction of the expansion
	 */
	public static ClipChange createExpansion(Maze maze, Block block) {
		
		Clip mazeClip = maze.getClip();
		
		if (!mazeClip.isBorderBlock(block))
			return null;
		
		Vec2 blockVec = new Vec2(block);
		ClipChange expansion = new ClipChange(mazeClip);
		
		extendMazeBorderBySurroundingBlocks(maze, blockVec, expansion);
		removeIntrusiveMazeBorder(mazeClip, blockVec, expansion);
		return expansion;
	}
	
	private static void extendMazeBorderBySurroundingBlocks(Maze maze, Vec2 point, ClipChange expansion) {
		
		Clip mazeClip = maze.getClip();
		expansion.removeBorder(point);
		
		for (Directions dir : Directions.values()) {
			
			Vec2 neighbor = point.clone().add(dir.getVec2());
			int height = BlockUtils.nearestSurfaceY(neighbor, mazeClip.getHeight(point), mazeClip.getWorld());
			
			if (!mazeClip.contains(neighbor)) {
				
				expansion.addFill(neighbor, height);
				expansion.addBorder(neighbor);
				
			} else if (maze.exitsContain(neighbor) && !sealsClipBorder(neighbor, expansion, Directions.cardinalValues()))
				expansion.removeExit(neighbor);
		}
	}
	
	//look for neighbors, that are now intruding the border unnecessarily around the expanded block
	private static void removeIntrusiveMazeBorder(Clip mazeClip, Vec2 point, ClipChange expansion) {
	
		for (Directions dir : Directions.values()) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (mazeClip.borderContains(neighbor) && !sealsClipBorder(neighbor, expansion, Directions.values()))
				expansion.removeBorder(neighbor);
		}
	}
	
	/**
	 * Creates a {@link ClipChange} with information about blocks in order to reduce the maze border at the given block.
	 * Returns null if the block is not part of the maze border (see {@link Clip#isBorderBlock(Block)}).
	 * To apply the action use {@link me.gorgeousone.tangledmaze.handlers.MazeHandler#processClipChange(Maze, ClipChange)}
	 *
	 * @param block the block where the maze border should be reduced/erased
	 * @return ClipAction of the expansion
	 */
	public static ClipChange createErasure(Maze maze, Block block) {
		
		Clip mazeClip = maze.getClip();
		
		if (!mazeClip.isBorderBlock(block))
			return null;
		
		Vec2 blockVec = new Vec2(block);
		ClipChange action = new ClipChange(mazeClip);
		
		action.removeBorder(blockVec);
		
		reduceBorderAroundBlock(maze, blockVec, action);
		removeProtrusiveBorder(mazeClip, blockVec, action);
		return action;
	}
	
	private static void reduceBorderAroundBlock(Maze maze, Vec2 point, ClipChange erasure) {
		
		Clip mazeClip = maze.getClip();
		
		if (maze.exitsContain(point))
			erasure.removeExit(point);
		
		erasure.removeBorder(point);
		erasure.removeFill(point, mazeClip.getHeight(point));
		
		if (!sealsClipBorder(point, erasure, Directions.values()))
			return;
		
		for (Directions dir : Directions.values()) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (mazeClip.contains(neighbor) && !mazeClip.borderContains(neighbor))
				erasure.addBorder(neighbor);
			
			if (maze.exitsContain(neighbor) && !sealsClipBorder(neighbor, erasure, Directions.cardinalValues()))
				erasure.removeExit(neighbor);
		}
	}
	
	private static void removeProtrusiveBorder(Clip mazeClip, Vec2 point, ClipChange erasure) {
		//detect outstanding neighbor borders of the block
		for (Directions dir : Directions.values()) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			//remove the neighbor if it still stands out
			if (mazeClip.borderContains(neighbor) && !sealsClipBorder(neighbor, erasure, Directions.values())) {
				
				int height = mazeClip.getHeight(neighbor);
				erasure.removeFill(neighbor, height);
			}
		}
	}
	
	public static boolean sealsClipBorder(Vec2 point, ClipChange changes, Directions[] directions) {
		
		boolean touchesFill = false;
		boolean touchesExternal = false;
		
		for (Directions dir : directions) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (!changes.clipWillContain(neighbor))
				touchesExternal = true;
			
			else if (!changes.clipBorderWillContain(neighbor))
				touchesFill = true;
			
			if (touchesFill && touchesExternal)
				return true;
		}
		
		return false;
	}
	
	public static boolean touchesFill(Vec2 point, ClipChange changes, Directions[] directions) {
		
		for (Directions dir : directions) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (!changes.clipBorderWillContain(neighbor) && changes.clipWillContain(neighbor))
				return true;
		}
		
		return false;
	}
	
	public static boolean touchesExternal(Vec2 point, ClipChange changes, Directions[] directions) {
		
		for (Directions dir : directions) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (!changes.clipWillContain(neighbor))
				return true;
		}
		
		return false;
	}
}
