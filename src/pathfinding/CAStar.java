package pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;

import pathfinding.data.SpacetimePoint;
import pathfinding.data.WarehouseMap;
import pathfinding.exception.InvalidCoordinateException;
import pathfinding.data.Node;

/**
 * My implementation of Cooperative A* search using Manhattan distance as the heuristic and a hash table for the reservation table storing
 * points with a time dimension and the Agent that is there. The open set is implemented as a priority queue with the priority being the f
 * value of the search node (f(x) = g(x) + h(x) where h(x) is the heuristic value of x to the goal). The closed set is simply a hash set and
 * a hash table is used to store where each search node came from (i.e. which node led to it). As explained in MultiRobotPlanning ideally I
 * would've liked to first calculate the basic A* path and use that as a heuristic as well as for other things.
 * @author Sam
 */
public class CAStar implements SearchStrategy {
	
	private final WarehouseMap map;
	private final Agent agent;
	private Hashtable<Node, Node> cameFrom;
	private HashSet<Node> closed;
	private PriorityQueue<Node> open;
	private Hashtable<SpacetimePoint, Agent> resTable;
	
	/**
	 * Create a new instance of this search strategy with the given map data, Agent, and reservation table. Initialise the data structures
	 * including the priority queue and its comparator
	 * @param map
	 *            map data
	 * @param agent
	 *            Agent to search on
	 * @param resTable
	 *            reservation table
	 */
	public CAStar(WarehouseMap map, Agent agent, Hashtable<SpacetimePoint, Agent> resTable) {
		this.map = map;
		this.agent = agent;
		this.resTable = resTable;
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
	
	/**
	 * Much of the functionality for this is identical to that for A* so see the comments in there and I will add in the things that are
	 * different.
	 */
	@Override
	public ArrayList<SpacetimePoint> pathfind(Node start, Node goal) {
		for (Node n : map.getNodes()) {
			n.setG((int) Double.POSITIVE_INFINITY);
		}
		start.setG(0);
		
		for (Node n : map.getNodes()) {
			n.setF((int) Double.POSITIVE_INFINITY);
		}
		start.setF(WarehouseMap.mDist(start, goal));
		
		open.add(start);
		
		while (!open.isEmpty()) {
			Node current = open.poll();
			
			int timestep = computeTimestep(current);
			
			// Is the current node the goal? If so get the path and terminate
			if (current.getX() == goal.getX() && current.getY() == goal.getY()) {
				return reconstructPath(goal);
			}
			closed.add(current);
			
			// A new boolean that checks that something as been added in this step, this is part of the wait functionality of the robot
			boolean somethingAdded = false;
			
			for (Node neighbour : current.getNeighbours()) {
				int nx = neighbour.getX();
				int ny = neighbour.getY();
				
				// In addition to skipping a neighbour if its already in the closed set, we also skip a neighbour if another robot has
				// reserved it for the next time step, to avoid a collision occurring on the next time step. We also skip a neighbour if
				// another robot has reserved it for this time step (the step before the step where the collision we are trying to avoid
				// would occur) to stop robots both moving towards each other at the same time from skipping through each other out to the
				// other side, again not a perfect way to fix it but I didn't have time to come up with anything more clever.
				if (closed.contains(neighbour)
						|| (resTable.containsKey(new SpacetimePoint(nx, ny, timestep + 1)) && !resTable.get(
								new SpacetimePoint(nx, ny, timestep + 1)).equals(agent))
						|| (resTable.containsKey(new SpacetimePoint(nx, ny, timestep)) && !resTable.get(
								new SpacetimePoint(nx, ny, timestep)).equals(agent))) {
					continue;
				}
				
				int tentativeG = current.getG() + 1;
				
				if (tentativeG >= neighbour.getG()) {
					continue;
				} else if (!open.contains(neighbour)) {
					neighbour.setG(tentativeG);
					neighbour.setF(neighbour.getG() + WarehouseMap.mDist(neighbour, goal));
					cameFrom.put(neighbour, current);
					open.add(neighbour);
					somethingAdded = true;
				}
			}
			
			// If nothing was added then add a new 'wait node'. A wait node is just a new node with identical position and neighbours but
			// has an increased cost from start (g)
			if (!somethingAdded) {
				Node wait = new Node(current.getX(), current.getY(), false, current.getNeighbours(), true);
				wait.setG(current.getG() + 1);
				wait.setF(wait.getG() + WarehouseMap.mDist(wait, goal));
				open.add(wait);
				cameFrom.put(wait, current);
			}
		}
		return null;
	}
	
	@Override
	public ArrayList<SpacetimePoint> reconstructPath(Node current) {
		ArrayList<Node> backwardsPath = new ArrayList<>();
		
		backwardsPath.add(current);
		while (cameFrom.get(current) != null) {
			current = cameFrom.get(current);
			backwardsPath.add(current);
		}
		
		ArrayList<SpacetimePoint> path = new ArrayList<>(backwardsPath.size());
		for (int i = 0; i < backwardsPath.size(); i++) {
			int bi = backwardsPath.size() - 1 - i;
			path.add(new SpacetimePoint(backwardsPath.get(bi), i));
		}
		return path;
	}
	
	/**
	 * Compute the current time step the robot would be at if it was on the current node, we do this by tracing back through the cameFrom
	 * table
	 * @param current
	 *            Node to get time step of
	 * @return time step number
	 */
	private int computeTimestep(Node current) {
		int ts = 0;
		while (cameFrom.get(current) != null) {
			current = cameFrom.get(current);
			ts++;
		}
		return ts;
	}
	
	/**
	 * Helper method to debug search in the early stages before I had the GUI
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
