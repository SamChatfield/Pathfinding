package pathfinding;

import java.util.ArrayList;
import java.util.Hashtable;

import pathfinding.data.SpacetimePoint;
import pathfinding.data.WarehouseMap;

/**
 * Route planning method for multiple robots using Cooperative A* with the reservation table implemented as a hash table of points (with
 * time dimension) and Agents, and using Manhattan distance as the heuristic.
 * 
 * NOTE ideally I would have used A* as the heuristic for the Cooperative A* but I didn't manage to get this implemented in time. Which is
 * unfortunate because I feel, in more ways than just having a better heuristic, the system could've benefited from first doing A* on an
 * Agent and then overall CA*. I think this would also help in the reduction of the amount of edge cases where the route planning could
 * fail.
 * 
 * @author Sam
 */
public class MultiRobotPlanning implements RobotPlanning {
	
	private final WarehouseMap map;
	private final Hashtable<SpacetimePoint, Agent> resTable;
	
	/**
	 * Create a new instance of this route planning method using the given map data and initialise the reservation table
	 * @param map
	 */
	public MultiRobotPlanning(WarehouseMap map) {
		this.map = map;
		resTable = new Hashtable<>();
	}
	
	@Override
	public void computePlan(ArrayList<Agent> as) {
		// Clear the reservation table of any leftover reservations from the last computation
		resTable.clear();
		
		// Reserve the spaces where a stationary robot is
		for (Agent a : as) {
			// An estimate of the maximum number of time steps a route will take.
			// This is part of my diabolically hacky solution to getting robots to plan around other stationary robots
			int estMaxTime = 100;
			
			// If a robot doesn't have a goal (i.e. it isn't going to move) reserve its position for estMaxTime (100) time steps
			if (a.getGoal() == null) {
				for (int i = 0; i < estMaxTime; i++) {
					resTable.put(new SpacetimePoint(a.getStart(), i), a);
				}
			}
			// Otherwise if a robot does have a goal reserve its goal for estMaxTime (100) time steps.
			// This is not ideal as it's my quick, non-optimal (and again, hacky) fix to getting robots to not crash into a robot that's
			// finished its route and is sitting at its goal.
			//
			// It has the poor side effect of also meaning that even if the robot hasn't reached its goal yet other robots would still avoid it
			// this is an instance of somewhere where using A* as the heuristic would help because I could then have a better estimate than just
			// 100 time steps of when the robot will reach its goal and only reserve a few time steps after this (or I might even know when it's
			// likely that the other robots will be done if I know their heuristic A* route length too and thus for how long to reserve the goal)
			else {
				for (int i = 0; i < estMaxTime; i++) {
					resTable.put(new SpacetimePoint(a.getGoal(), i), a);
				}
			}
		}
		
		// Iterate through the Agents.
		// If they have a goal and their path is either empty or null then create a new object of Cooperative A* and pathfind for this Agent
		// from its start to its goal, setting its path to the result of this. Also add all of the points (with time dimension) of this
		// path to the reservation table.
		for (Agent a : as) {
			// System.out.println(a);
			if (a.getGoal() != null && (a.getPath().isEmpty() || a.getPath() == null)) {
				a.clearPath();
				SearchStrategy strat = new CAStar(map, a, resTable);
				a.setPath(strat.pathfind(a.getStart(), a.getGoal()));
				for (SpacetimePoint p : a.getPath()) {
					resTable.put(p, a);
				}
			}
		}
	}
	
}
