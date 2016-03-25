package pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Set;

import pathfinding.data.SpacetimePoint;
import pathfinding.data.WarehouseMap;
import pathfinding.data.Node;
import pathfinding.exception.InvalidCoordinateException;

/**
 * My implementation of A* search using Manhattan distance as the heuristic. The open set is implemented as a priority queue with the
 * priority being the f value of the search node (f(x) = g(x) + h(x) where h(x) is the heuristic value of x to the goal). The closed set is
 * simply a hash set and a hash table is used to store where each search node came from (i.e. which node led to it).
 * @author Sam
 */
public class AStar implements SearchStrategy {
	
	private WarehouseMap map;
	private Hashtable<Node, Node> cameFrom;
	private Set<Node> closed;
	private PriorityQueue<Node> open;
	
	/**
	 * Create a new instance of A* search using the given map data. Initialise the data structures and set the comparator for the open set
	 * (queue) to the f value.
	 * @param map
	 */
	public AStar(WarehouseMap map) {
		this.map = map;
		cameFrom = new Hashtable<>();
		closed = new HashSet<>();
		
		open = new PriorityQueue<>(map.getNodes().size(), new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.getF() > n2.getF()) {
					return 1;
				} else if (n1.getF() < n2.getF()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
	}
	
	@Override
	public ArrayList<SpacetimePoint> pathfind(Node start, Node goal) {
		// Iterate through the search nodes of the map and set initial g values for undiscovered nodes to infinity and set the g value of
		// the start node to 0
		for (Node n : map.getNodes()) {
			n.setG((int) Double.POSITIVE_INFINITY);
		}
		start.setG(0);
		
		// Iterate through the search nodes of the map and set initial f values for undiscovered nodes to infinity and set the f value of
		// the start node to the heuristic (Manhattan) distance from the start to the goal
		for (Node n : map.getNodes()) {
			n.setF((int) Double.POSITIVE_INFINITY);
		}
		start.setF(WarehouseMap.mDist(start, goal));
		
		// Add the start node to the open queue
		open.add(start);
		
		// System.out.println("A* progress:\n" + "_ = blank space\n" + "X = inner wall\n" + "@ = goal node (before it is in the frontier)\n"
		// + "O = current node being considered\n" + "+ = nodes in the frontier\n" + ". = explored nodes:\n");
		
		// The main loop of the search, keep searching while the open set contains nodes
		while (!open.isEmpty()) {
			// printSearchMap(goal);
			
			// Take the front of the open queue
			Node current = open.poll();
			
			// If the front of the open queue is the goal node then reconstruct the path from the start to the goal and return it
			if (current.getX() == goal.getX() && current.getY() == goal.getY()) {
				return reconstructPath(goal);
			}
			
			// Add the current node to the closed set
			closed.add(current);
			
			// Iterate through the neighbours of the current node
			for (Node neighbour : current.getNeighbours()) {
				// If the neighbour is in the closed set then ignore it
				if (closed.contains(neighbour)) {
					continue;
				}
				
				// Set a tentativeG value of the neighbour but don't assign it
				int tentativeG = current.getG() + 1;
				
				// If the new g value of the neighbour would be greater than the one it's already got then ignore this neighbour
				if (tentativeG >= neighbour.getG()) {
					continue;
				}
				// Otherwise, add the neighbour to the open queue if its not already there after assigning its g value, f value and creating
				// the relevant entry in the cameFrom table
				else if (!open.contains(neighbour)) {
					neighbour.setG(tentativeG);
					neighbour.setF(neighbour.getG() + WarehouseMap.mDist(neighbour, goal));
					cameFrom.put(neighbour, current);
					open.add(neighbour);
				}
			}
		}
		// If this point is reached then the search has failed so return null. It's more likely, however, that even if search fails it won't
		// reach this code because it'll get stuck in an infinite loop
		// somewhere.
		return null;
	}
	
	@Override
	public ArrayList<SpacetimePoint> reconstructPath(Node current) {
		ArrayList<Node> backwardsPath = new ArrayList<>();
		System.out.println("recons");
		backwardsPath.add(current);
		System.out.println("cur: " + current + " cf " + cameFrom.get(current));
		while (cameFrom.get(current) != null) {
			current = cameFrom.get(current);
			backwardsPath.add(current);
		}
		ArrayList<SpacetimePoint> path = new ArrayList<>(backwardsPath.size());
		for (int i = 0; i < backwardsPath.size(); i++) {
			int bi = backwardsPath.size() - 1 - i;
			path.add(new SpacetimePoint(backwardsPath.get(bi), i));
		}
		System.out.println(path);
		return path;
	}
	
	/**
	 * Helper method for debugging that prints the state of the search map, including the node currently being considered, the obstacles,
	 * the closed set, the open set and the goal
	 * @param goal
	 *            goal node
	 */
	public void printSearchMap(Node goal) {
		for (int y = map.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < map.getWidth(); x++) {
				Node n = null;
				try {
					n = map.nodeAt(x, y);
				} catch (InvalidCoordinateException e) {
					// Unreachable since for loops bounded by map dimensions
					e.printStackTrace();
				}
				
				if (n.isObstacle() && (open.contains(n) || closed.contains(n))) {
					throw new RuntimeException("ERROR IN A*, OBSTACLE IN FRONTIER OR EXPLORED");
				} else if (n.getX() == open.peek().getX() && n.getY() == open.peek().getY()) {
					System.out.print(" O");
				} else if (closed.contains(n)) {
					System.out.print(" .");
				} else if (open.contains(n)) {
					System.out.print(" +");
				} else if (n.isObstacle()) {
					System.out.print(" X");
				} else if (n.getX() == goal.getX() && n.getY() == goal.getY()) {
					System.out.print(" @");
				} else {
					System.out.print(" -");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
}
