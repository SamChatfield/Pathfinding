package pathfinding;

import java.util.ArrayList;

import pathfinding.data.Node;
import pathfinding.data.SpacetimePoint;

/**
 * Abstract search strategy data type which will have implementations A* and Cooperative A*
 * @author Sam
 */
public interface SearchStrategy {
	
	/**
	 * Find the path from the start node to the goal node using the current search strategy
	 * @param start
	 *            start node
	 * @param goal
	 *            goal node
	 * @return path
	 */
	public abstract ArrayList<SpacetimePoint> pathfind(Node start, Node goal);
	
	/**
	 * Trace through the cameFrom table and reconstruct the path
	 * @param current
	 *            the node to trace back from
	 * @return path from start to current
	 */
	public ArrayList<SpacetimePoint> reconstructPath(Node current);
	
}
