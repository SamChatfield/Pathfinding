package pathfinding.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pathfinding.exception.InvalidCoordinateException;

/**
 * Code representation of the warehouse.
 * NOTE: (0,0) is the bottom left of the map as seen in ExampleGridMover
 * @author Sam
 */
public class WarehouseMap {
	
	private final int width = 12;
	private final int height = 8;
	private ArrayList<Node> nodes;
	
//	private final Set<Integer> obstacleX, obstacleY;
	private final Set<Point> obs;
	
	
	/**
	 * Create new map object
	 */
	public WarehouseMap() {
//		Integer[] ox = { 2, 5, 6, 9 };
//		obstacleX = new HashSet<>(Arrays.asList(ox));
//		
//		Integer[] oy = { 2, 3, 4, 5 };
//		obstacleY = new HashSet<>(Arrays.asList(oy));
		
		Point[] os = { new Point(2, 2), new Point(2, 3), new Point(2, 4), new Point(2, 5),
				new Point(5, 3), new Point(5, 4), new Point(6, 3), new Point(6, 4),
				new Point(9, 2), new Point(9, 3), new Point(9, 4), new Point(9, 5)};
		obs = new HashSet<>(Arrays.asList(os));
		
		nodes = createNodes();
		addAdjacencies(nodes);
	}
	
	/**
	 * Create the nodes of the map
	 * @return ArrayList of nodes representing the map
	 */
	private ArrayList<Node> createNodes() {
		ArrayList<Node> list = new ArrayList<>();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean obstacle = false;
//				if ((y >= 1 && y <= 5) && (x == 1 || x == 4 || x == 7 || x == 10)) {
				System.out.println();
				if (obs.contains(new Point(x, y))) {
					obstacle = true;
				}
//				System.out.println("Added node: (" + x + "," + y + ") and obstacle = " + obstacle);
				list.add(new Node(x, y, obstacle, new ArrayList<>()));
			}
		}
		
		return list;
	}
	
	/**
	 * Return an ArrayList of nodes with adjacent nodes added in
	 * @param nodes ArrayList of nodes
	 * @return ArrayList with adjacent nodes included
	 */
	private void addAdjacencies(ArrayList<Node> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<Node> adj = nodes.get(i).getNeighbours();
			
			int x = i % 12;
			int y = i / 12;
			
			Node up, down, left, right;
			try { up = nodeAt(x, y + 1); } catch (InvalidCoordinateException e) { up = null; }
			try { down = nodeAt(x, y - 1); } catch (InvalidCoordinateException e) { down = null; }
			try { left = nodeAt(x - 1, y); } catch (InvalidCoordinateException e) { left = null; }
			try { right = nodeAt(x + 1, y); } catch (InvalidCoordinateException e) { right = null; }
			
			if (up != null && !up.isObstacle()) { adj.add(up); }
			if (down != null && !down.isObstacle()) { adj.add(down); }
			if (left != null && !left.isObstacle()) { adj.add(left); }
			if (right != null && !right.isObstacle()) { adj.add(right); }
		}
	}
	
	/**
	 * Return the node object at the given x and y coordinates
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return node at (x,y)
	 * @throws InvalidCoordinateException
	 */
	public Node nodeAt(int x, int y) throws InvalidCoordinateException {
		// Check that the x and y produce a valid node
		if (x >= 0 && x <= 11 && y >= 0 && y <= 7) {
			// Convert the coordinates to a node
			return nodes.get(y * 12 + x);
		} else {
			throw new InvalidCoordinateException("Specified coordinates out of bounds");
		}
	}
	
	/**
	 * Determine if there exists a node at x,y and if so is it a valid node, i.e. not an obstacle
	 * @param x x position
	 * @param y y position
	 * @return is valid
	 */
	public boolean isValidNode(int x, int y) {
		Node n;
		try {
			n = nodeAt(x, y);
		} catch (InvalidCoordinateException e) {
			return false;
		}
		return !n.isObstacle();
	}
	
	/**
	 * Compute the Manhattan distance between two nodes
	 * @param n1
	 *            Node 1
	 * @param n2
	 *            Node 2
	 * @return Manhattan distance from node 1 to node 2
	 */
	public static int mDist(Node n1, Node n2) {
		return Math.abs(n2.getX() - n1.getX()) + Math.abs(n2.getY() - n1.getY());
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public String toString() {
		String out = "";
		
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				try {
					if (nodeAt(x, y).isObstacle()) {
						out += " X";
					} else {
						out += " _";
					}
				} catch (InvalidCoordinateException e) {
					// Unreachable due to limits of the for loop
					e.printStackTrace();
				}
			}
			out += "\n";
		}
		return out;
	}
	
}