package com.samchatfield.pathfinding;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.samchatfield.pathfinding.data.WorldMap;

/**
 * Class to perform route planning for a single agent within the warehouse. For now this returns a route in the form of a list of grid
 * poses. Subject to change based on requirements.
 * @author Sam
 */
public class SingleAgentPlanning implements AgentPlanning {
	
	private WorldMap map;
	
	/**
	 * Create a new instance of this route planning method on the given map data
	 * @param map
	 */
	public SingleAgentPlanning(WorldMap map) {
		this.map = map;
	}
	
	@Override
	public void computePlan(ArrayList<Agent> as) {
		Agent a = as.get(0);
		a.clearPath();
		SearchStrategy strat = new AStar(map);
		a.setPath(strat.pathfind(a.getStart(), a.getGoal()));
	}
	
	/**
	 * Test output example of a planned route, dummies a route (not in use anymore)
	 * @return fixed list of point
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private ArrayList<Point2D> fixedPath() {
		ArrayList<Point2D> list = new ArrayList<>();
		list.add(new Point(1, 0));
		list.add(new Point(2, 0));
		list.add(new Point(2, 1));
		list.add(new Point(2, 2));
		list.add(new Point(2, 3));
		list.add(new Point(1, 3));
		return list;
	}
	
}
