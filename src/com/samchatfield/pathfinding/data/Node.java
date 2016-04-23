package com.samchatfield.pathfinding.data;

import java.util.ArrayList;

/**
 * Map / search Node representation used in route planning. Contains an x,y position, an obstacle flag and a list of the node's non-obstacle
 * neighbours, and a flag indicating whether this node is a 'wait Node'. Override Object's hashcode and equals methods to define equality of
 * Nodes so that A* search using these Nodes works correctly. Has implications for uses of contains() on the HashSets and Hashtables etc. in
 * the search classes.
 * @author Sam
 */
public class Node {
	
	private final int x, y;
	private boolean obstacle;
	private ArrayList<Node> neighbours;
	private int gScore, fScore;
	private boolean waitNode;
	
	/**
	 * Create a new node at given x,y position, obstacle value, list of neighbours and a default waitNode flag of false.
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param obstacle
	 *            is this Node an obstacle?
	 * @param neighbours
	 *            list of non-obstacle neighbours of this Node
	 */
	public Node(int x, int y, boolean obstacle, ArrayList<Node> neighbours) {
		this(x, y, obstacle, neighbours, false);
	}
	
	/**
	 * Create a new node at given x,y position, obstacle status, list of neighbours and waitNode status.
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param obstacle
	 *            is this Node an obstacle?
	 * @param neighbours
	 *            list of non-obstacle neighbours
	 * @param waitNode
	 *            is this Node a 'wait Node'?
	 */
	public Node(int x, int y, boolean obstacle, ArrayList<Node> neighbours, boolean waitNode) {
		this.x = x;
		this.y = y;
		this.obstacle = obstacle;
		this.neighbours = neighbours;
		this.waitNode = waitNode;
	}
	
	/**
	 * Get the x position
	 * @return x pos
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the y position
	 * @return y pos
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Return if the Node is an obstacle or not
	 * @return is obstacle?
	 */
	public boolean isObstacle() {
		return obstacle;
	}
	
	/**
	 * Get the list of non-obstacle neighbours
	 * @return neighbours
	 */
	public ArrayList<Node> getNeighbours() {
		return neighbours;
	}
	
	/**
	 * Get the Node's g score. I.e. the cost from start to current.
	 * @return g score
	 */
	public int getG() {
		return gScore;
	}
	
	/**
	 * Get the Node's f score. I.e. the g cost + heuristic cost to goal
	 * @return f score
	 */
	public int getF() {
		return fScore;
	}
	
	/**
	 * Set the Node's g score
	 * @param gScore
	 *            new g score
	 */
	public void setG(int gScore) {
		this.gScore = gScore;
	}
	
	/**
	 * Set the Node's f score
	 * @param fScore
	 *            new f score
	 */
	public void setF(int fScore) {
		this.fScore = fScore;
	}
	
	@Override
	public String toString() {
		if (waitNode) {
			return fScore + "w(" + x + ", " + y + ")";
		}
		return fScore + "(" + x + ", " + y + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Node node = (Node) o;
		
		if (x != node.x)
			return false;
		if (y != node.y)
			return false;
		if (fScore != node.fScore)
			return false;
		if (waitNode != node.waitNode)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		// Hash on all the ways that a Node could be different that have implications on search. The x position, y position, f score, and if
		// it's a 'wait Node'
		int result = x;
		result = 31 * result + y;
		result = 31 * result + fScore;
		result = 31 * result + (waitNode ? 1 : 0);
		return result;
	}
	
}
