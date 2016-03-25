package pathfinding;

import java.util.ArrayList;
import java.util.Iterator;

import pathfinding.data.Node;
import pathfinding.data.SpacetimePoint;

/**
 * Representation of a robot with a start (current) node, goal node, priority (index) and a current path
 * @author Sam
 */
public class Agent {
	
	private Node start, goal;
	private int index;
	private ArrayList<SpacetimePoint> path;
	
	/**
	 * Create a new agent at a given start, with a given goal (goal can be null to indicate no movement) and a given priority
	 * @param start
	 *            start (current) node
	 * @param goal
	 *            goal node
	 * @param index
	 *            priority
	 */
	public Agent(Node start, Node goal, int index) {
		this.start = start;
		this.goal = goal;
		this.index = index;
		path = new ArrayList<>();
	}
	
	/**
	 * Get the priority of this Agent
	 * @return priority
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Get the current path of this Agent
	 * @return path
	 */
	public ArrayList<SpacetimePoint> getPath() {
		return path;
	}
	
	/**
	 * Return the path without the start and goal for drawing purposes in MapView
	 * @return path without start or goal node
	 */
	public ArrayList<SpacetimePoint> getPathNoSG() {
		ArrayList<SpacetimePoint> pathNoSG = new ArrayList<>();
		pathNoSG.addAll(path);
		
		Iterator<SpacetimePoint> iter = pathNoSG.iterator();
		while (iter.hasNext()) {
			SpacetimePoint p = iter.next();
			if ((p.getX() == start.getX() && p.getY() == start.getY()) || (p.getX() == goal.getX() && p.getY() == goal.getY()))
				iter.remove();
		}
		return pathNoSG;
	}
	
	/**
	 * Get the path without the start node for animation purposes
	 * @return path without start node
	 */
	public ArrayList<SpacetimePoint> getPathNoS() {
		ArrayList<SpacetimePoint> pathNoS = new ArrayList<>();
		pathNoS.addAll(path);
		
		Iterator<SpacetimePoint> iter = pathNoS.iterator();
		while (iter.hasNext()) {
			SpacetimePoint p = iter.next();
			if (p.getX() == start.getX() && p.getY() == start.getY())
				iter.remove();
		}
		return pathNoS;
	}
	
	/**
	 * Clear the robot's path
	 */
	public void clearPath() {
		path.clear();
	}
	
	/**
	 * Get the start node
	 * @return start
	 */
	public Node getStart() {
		return start;
	}
	
	/**
	 * Get the goal node
	 * @return goal
	 */
	public Node getGoal() {
		return goal;
	}
	
	/**
	 * Set the Agent's current path
	 * @param path
	 *            new path
	 */
	public void setPath(ArrayList<SpacetimePoint> path) {
		this.path = path;
	}
	
	/**
	 * Set the Agent's start node
	 * @param start
	 *            new start
	 */
	public void setStart(Node start) {
		this.start = start;
	}
	
	/**
	 * Set the Agent's goal node
	 * @param goal
	 *            new goal
	 */
	public void setGoal(Node goal) {
		this.goal = goal;
	}
	
	/**
	 * String representation of an Agent (for debugging purposes only)
	 */
	@Override
	public String toString() {
		return "Agent " + (index + 1) + " at s" + start + " g" + goal + " p: " + path;
	}
	
}
