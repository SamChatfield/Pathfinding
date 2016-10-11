package com.samchatfield.pathfinding.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import com.samchatfield.pathfinding.Agent;
import com.samchatfield.pathfinding.data.Node;
import com.samchatfield.pathfinding.data.SpacetimePoint;
import com.samchatfield.pathfinding.data.WorldMap;
import com.samchatfield.pathfinding.exception.InvalidCoordinateException;

/**
 * The view for the GUI representation of the warehouse map This code is not the most optimal GUI code in the world since it redraws things
 * it really doesn't need to redraw on updates.
 * @author Sam
 */
@SuppressWarnings("serial")
public class MapView extends JPanel implements Observer {
	
	private final PathfindingModel model;
	private final WorldMap map;
	private Point2D topLeft, botLeft, topRight, botRight;
	private ArrayList<Point2D> nodes;
	private double sf;
	private int w, h;
	
	/**
	 * Create a new instance of this view of the map
	 * @param model
	 *            model object which this observes
	 * @param map
	 *            map representation data
	 */
	public MapView(PathfindingModel model, WorldMap map) {
		this.model = model;
		this.map = map;
		nodes = new ArrayList<>(map.getNodes().size());
		
		// Add a mouse listener to the view to handle mouse clicks and allow you to place goals using mouse clicks
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// The screen coordinates where the mouse click event occurred
				int sx = e.getX();
				int sy = e.getY();
				
				// The map x coordinates corresponding to these screen coordinates
				int x = Math.round((float) ((int) sx / sf - 2));
				int y = Math.round((float) (7 - ((int) sy / sf - 2)));
				
				// TODO fix this
				// If this x and y constitute an illegal node (i.e. an obstacle or outside of the map) then ignore the click
				if (!model.getMap().isValidNode(x, y))
					return;
				
				// If it was a valid mouse click then if the focused agent either doesn't have a goal or does have a goal but the click
				// isn't on it's start node or goal node then set the agent's goal to this new node where the click occurred
				Agent a = model.getFocusedAgent();
				Node aStart = a.getStart();
				Node aGoal = a.getGoal();
				if (a.getPath() != null) {
					if (aGoal == null || !(x == aGoal.getX() && y == aGoal.getY()) && !(x == aStart.getX() && y == aStart.getY())) {
						try {
							model.setGoal(a, map.nodeAt(x, y));
						} catch (InvalidCoordinateException e1) {
							// Shouldn't be thrown as we checked that it was a valid node earlier
							e1.printStackTrace();
							System.err.println("Invalid coord in click on a node set goal");
						}
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		// Create the corners of the warehouse (not nodes/junctions just corners the corners of the warehouse)
		topLeft = new Point2D.Double(1, 10);
		botLeft = new Point2D.Double(1, 1);
		topRight = new Point2D.Double(14, 10);
		botRight = new Point2D.Double(14, 1);
		
		// Iterate through the search nodes and create a new awt Point based on this and add it to the ArrayList of them
		for (Node n : map.getNodes()) {
			nodes.add(new Point2D.Double(n.getX() + 2, n.getY() + 2));
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		w = getWidth();
		h = getHeight();
		
		// Create a scale factor for the view given its width and height in order to scale the map coordinates to pixel values
		sf = Math.min(w / 15, h / 11);
		
		// Draw warehouse white background
		g2d.setColor(Color.WHITE);
		g2d.fillRect((int) (sf), (int) (sf), (int) (13 * sf), (int) (9 * sf));
		
		// Draw borders of warehouse
		g2d.setColor(Color.BLACK);
		g2d.draw(new Line2D.Double(topLeft.getX() * sf, topLeft.getY() * sf, topRight.getX() * sf, topRight.getY() * sf)); // top border
		g2d.draw(new Line2D.Double(botLeft.getX() * sf, botLeft.getY() * sf, botRight.getX() * sf, botRight.getY() * sf)); // bottom
		g2d.draw(new Line2D.Double(topLeft.getX() * sf, topLeft.getY() * sf, botLeft.getX() * sf, botLeft.getY() * sf)); // left
		g2d.draw(new Line2D.Double(topRight.getX() * sf, topRight.getY() * sf, botRight.getX() * sf, botRight.getY() * sf)); // right
		
		// Draw nodes
		for (Point2D p : nodes) {
			// Check that the node exists, otherwise there is a bug
			Node current = null;
			try {
				current = map.nodeAt((int) (p.getX() - 2), (int) (p.getY() - 2));
			} catch (InvalidCoordinateException e) {
				// Should never be reached as iterating through nodes already created
				e.printStackTrace();
				System.err.println("Node at for drawing obstacles threw exception");
			}
			
			// Draw obstacle or normal node
			if (current.isObstacle()) {
				g2d.setColor(Color.GRAY);
				g2d.fillRect((int) ((p.getX() - 0.5) * sf), screenY(p.getY() + 0.5), (int) sf, (int) sf);
			} else {
				g2d.setColor(Color.BLACK);
				String coordText = "(" + (int) (p.getX() - 2) + "," + (int) (p.getY() - 2) + ")";
				g2d.drawString(coordText, (int) (p.getX() * sf - 25), screenY(p.getY()) + 25);
				int d = 7;
				
				g2d.fillOval((int) (p.getX() * sf - 0.5 * d), screenY(p.getY()) - (int) (0.5 * d), d, d);
				g2d.setColor(Color.BLACK);
			}
			
			// Draw the borders around each node
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int) ((p.getX() - 0.5) * sf), screenY(p.getY() + 0.5), (int) sf, (int) sf);
		}
		
		// Compute the array of greyscale colours that will be used for the paths
		int numAgents = model.getAgents().size();
		int grNum = 255 / (numAgents + 1); // 3 agents => 255 / 4 => 1..3
		int[] gs = new int[numAgents];
		for (int i = 0; i < numAgents; i++) {
			gs[i] = grNum * (i + 1);
		}
		
		// Draw the paths of the agents except the focused agent going from highest
		// priority on the bottom to lowest on the top
		for (int i = 0; i < numAgents; i++) {
			Agent curAgent = model.getAgents().get(i);
			if (curAgent.getIndex() != model.getFocusedAgent().getIndex()) {
				Color greyGrad = new Color(gs[i], gs[i], gs[i]);
				
				Agent a = model.getAgents().get(i);
				ArrayList<SpacetimePoint> path = a.getPathNoSG();
				for (int j = 0; j < path.size(); j++) {
					g2d.setColor(greyGrad);
					int size = 25;
					int x = (int) ((path.get(j).getX() + 2) * sf - 0.5 * size);
					int y = screenY(path.get(j).getY() + 2) - (int) (0.5 * size);
					g2d.fillRect(x, y, size, size);
					g2d.setColor(Color.BLACK);
					g2d.drawRect(x, y, size, size);
				}
				
			}
		}
		
		// Draw the path of the focused agent on top so you can see the whole of the focused Agent's path to allow full inspection of each
		// Agent's path
		Agent fa = model.getFocusedAgent();
		ArrayList<SpacetimePoint> path = fa.getPathNoSG();
		for (int j = 0; j < path.size(); j++) {
			int fg = gs[model.getFocusedAgent().getIndex()];
			Color greyGrad = new Color(fg, fg, fg);
			g2d.setColor(greyGrad);
			int size = 25;
			int x = (int) ((path.get(j).getX() + 2) * sf - 0.5 * size);
			int y = screenY(path.get(j).getY() + 2) - (int) (0.5 * size);
			g2d.fillRect(x, y, size, size);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(x, y, size, size);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString("" + (fa.getIndex() + 1), (int) (rx(fa) * sf) - 3, screenY(ry(fa)) + 5);
		g2d.drawString("" + (fa.getIndex() + 1), (int) (gx(fa) * sf) - 3, screenY(gy(fa)) + 5);
		
		// Finally, draw the agents' starts and goals
		for (Agent a : model.getAgents()) {
			int d = 20;
			Node goal = a.getGoal();
			// System.out.println(s + " -> " + go);
			
			// Starts
			g2d.setColor(Color.RED);
			g2d.fillOval((int) (rx(a) * sf - 0.5 * d), screenY(ry(a)) - (int) (0.5 * d), d, d);
			g2d.setColor(Color.BLACK);
			g2d.drawOval((int) (rx(a) * sf - 0.5 * d), screenY(ry(a)) - (int) (0.5 * d), d, d);
			g2d.drawString("" + (a.getIndex() + 1), (int) (rx(a) * sf) - 3, screenY(ry(a)) + 5);
			
			// Goals
			if (goal != null) {
				g2d.setColor(Color.CYAN);
				g2d.fillOval((int) (gx(a) * sf - 0.5 * d), screenY(gy(a)) - (int) (0.5 * d), d, d);
				g2d.setColor(Color.BLACK);
				g2d.drawOval((int) (gx(a) * sf - 0.5 * d), screenY(gy(a)) - (int) (0.5 * d), d, d);
				g2d.drawString("" + (a.getIndex() + 1), (int) (gx(a) * sf) - 3, screenY(gy(a)) + 5);
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}
	
	/**
	 * Convert a y coordinate value to a pixel value on the screen. A helper method for the fact that 0,0 in a JPanel is top-left and 0,0 in
	 * my coordinate system is bottom-left
	 * @param yCo
	 *            y coordinate
	 * @return pixel coordinate for this y
	 */
	public int screenY(double yCo) {
		return (int) ((11 - yCo) * sf);
	}
	
	/**
	 * Given Agent's current x coordinate on the GUI. Adding two is necessary because there are extra padding rows and columns in the GUI to act as
	 * borders
	 * @param a
	 *            Agent
	 * @return current x coordinate
	 */
	public double rx(Agent a) {
		return a.getStart().getX() + 2;
	}
	
	/**
	 * Given Agent's current y coordinate on the GUI. Adding two is necessary because there are extra padding rows and columns in the GUI to act as
	 * borders
	 * @param a Agent
	 * @return current y coordinate
	 */
	public double ry(Agent a) {
		return a.getStart().getY() + 2;
	}
	
	/**
	 * Given Agent's goal x coordinate on the GUI. Adding two is necessary because there are extra padding rows and columns in the GUI to act as
	 * borders
	 * @param a Agent
	 * @return goal x coordinate
	 */
	public double gx(Agent a) {
		Node g = a.getGoal();
		if (g == null) {
			return -1.0;
		} else {
			return a.getGoal().getX() + 2;
		}
	}
	
	/**
	 * Given Agent's goal y coordinate on the GUI. Adding two is necessary because there are extra padding rows and columns in the GUI to act as
	 * borders
	 * @param a Agent
	 * @return goal y coordinate
	 */
	public double gy(Agent a) {
		Node g = a.getGoal();
		if (g == null) {
			return -1.0;
		} else {
			return a.getGoal().getY() + 2;
		}
	}
	
}
