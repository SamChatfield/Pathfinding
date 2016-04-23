package com.samchatfield.pathfinding;

import java.util.ArrayList;
import java.util.Hashtable;

import com.samchatfield.pathfinding.data.SpacetimePoint;
import com.samchatfield.pathfinding.data.WarehouseMap;

/**
 * Route planning method for multiple agents using Cooperative A* with the reservation table implemented as a hash table of points (with
 * time dimension) and Agents, and using Manhattan distance as the heuristic.
 * 
 * @author Sam
 */
public class MultiAgentPlanning implements AgentPlanning {
	
	private final WarehouseMap map;
	private final Hashtable<SpacetimePoint, Agent> resTable;
	
	/**
	 * Create a new instance of this route planning method using the given map data and initialise the reservation table
	 * @param map
	 */
	public MultiAgentPlanning(WarehouseMap map) {
		this.map = map;
		resTable = new Hashtable<>();
	}
	
	@Override
	public void computePlan(ArrayList<Agent> as) {
		// Clear the reservation table of any leftover reservations from the last computation
		resTable.clear();
		
		// Reserve the spaces where a stationary agent is
		for (Agent a : as) {
			// An estimate of the maximum number of time steps a route will take.
			// This is part of my diabolically hacky solution to getting agents to plan around other stationary agents
			int estMaxTime = 100;
			
			// If an agent doesn't have a goal (i.e. it isn't going to move) reserve its position for estMaxTime (100) time steps
			if (a.getGoal() == null) {
				for (int i = 0; i < estMaxTime; i++) {
					resTable.put(new SpacetimePoint(a.getStart(), i), a);
				}
			}
			// Otherwise if an agent does have a goal, reserve its goal for estMaxTime (100) time steps.
			// This is not ideal as it's my quick, non-optimal (and again, hacky) fix to getting agents to not crash into a agent that's
			// finished its route and is sitting at its goal.
			//
			// It has the poor side effect of also meaning that even if the agent hasn't reached its goal yet other agents would still avoid it
			// this is an instance of somewhere where using A* as the heuristic would help because I could then have a better estimate than just
			// 100 time steps of when the agent will reach its goal and only reserve a few time steps after this (or I might even know when it's
			// likely that the other agents will be done if I know their heuristic A* route length too and thus for how long to reserve the goal)
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
