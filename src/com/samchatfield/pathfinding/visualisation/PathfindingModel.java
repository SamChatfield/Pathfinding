package com.samchatfield.pathfinding.visualisation;

import java.util.ArrayList;
import java.util.Observable;

import com.samchatfield.pathfinding.Agent;
import com.samchatfield.pathfinding.AgentPlanning;
import com.samchatfield.pathfinding.data.Node;
import com.samchatfield.pathfinding.data.SpacetimePoint;
import com.samchatfield.pathfinding.data.WorldMap;
import com.samchatfield.pathfinding.exception.InvalidCoordinateException;

/**
 * Model part of MVC design which interfaces with the route planning classes and stores an ArrayList of 'Agents' including its position, stored path and priority (index)
 * @author Sam
 */
public class PathfindingModel extends Observable {
	
	private final AgentPlanning rp;
	private final WorldMap map;
	private final int agentNumber;
	private ArrayList<Agent> agents;
	private Agent focusedAgent;
	private boolean agentsMoving;
	
	public PathfindingModel(AgentPlanning ap, WorldMap map, int agentNumber) {
		this.rp = ap;
		this.map = map;
		this.agentNumber = agentNumber;
		agentsMoving = false;
		
		// Initialise Agents ArrayList
		agents = new ArrayList<>(agentNumber);
		
		// Add new Agents initially with null start and goal and then set the start along a line at the bottom of the map
		for (int i = 0; i < agentNumber; i++) {
			agents.add(new Agent(null, null, i));
			Agent a = agents.get(i);
			a.setStart(map.getNodes().get(i * 2));
			try {
				a.setStart(map.nodeAt(i * 2, 0));
			} catch (InvalidCoordinateException e) {
				System.err.println("Error placing agents on map");
			}
		}
		// Set the initially focused Agent to the highest priority one
		focusedAgent = agents.get(0);
		System.out.println(focusedAgent);
	}
	
	/**
	 * Compute the paths of all the agents from the route planning class
	 */
	public void computePaths() {
		rp.computePlan(agents);
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Return the list of Agents containing their paths, positions and goals
	 * @return Agent list
	 */
	public ArrayList<Agent> getAgents() {
		return agents;
	}
	
	/**
	 * Return the map representation
	 * @return map
	 */
	public WorldMap getMap() {
		return map;
	}
	
	/**
	 * Return the number of agents involved in the search
	 * @return Agent number
	 */
	public int getAgentNumber() {
		return agentNumber;
	}
	
	/**
	 * Return the Agent that is currently focused
	 * @return Agent that is focused
	 */
	public Agent getFocusedAgent() {
		return focusedAgent;
	}
	
	/**
	 * Return whether there are agents moving in the simulator
	 * @return Agents moving
	 */
	public boolean agentsMoving() {
		return agentsMoving;
	}
	
	/**
	 * Change the Agent that has focus and instruct the GUI to update accordingly
	 * @param focusedAgent
	 *            new Agent to give focus to
	 */
	public void setFocusedAgent(Agent focusedAgent) {
		this.focusedAgent = focusedAgent;
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Set the goal of the given agent if it's not already the goal of another agent and instruct GUI to update
	 * @param a
	 *            the agent
	 * @param goal
	 *            the goal
	 */
	public void setGoal(Agent a, Node goal) {
		boolean occupied = false;
		
		// Check that the proposed new goal isn't the goal of another agent
		// or the current position of another agent that isn't going to move
		// (you should be able to set a goal at the current position of a agent that IS going to move though)
		for (Agent oa : agents) {
			if (oa != a && goal != null && (goal.equals(oa.getGoal()) || (oa.getGoal() == null && goal.equals(oa.getStart())))) {
				occupied = true;
			}
		}
		
		// As long as none of the above conditions are violated, change the goal, clear the path and update GUI
		if (!occupied) {
			a.setGoal(goal);
			a.clearPath();
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Move one Agent to its goal
	 * @param a
	 *            Agent to move
	 */
	@Deprecated
	public void moveToGoal(Agent a) {
		a.setStart(a.getGoal());
		setGoal(a, null);
		a.getPath().clear();
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Move all agents to goals and update the GUI (jump to goals, no animation) No longer in use since addition of animateToGoals()
	 */
	@Deprecated
	public void moveToGoals() {
		for (Agent a : agents) {
			if (a.getGoal() != null) {
				a.setStart(a.getGoal());
				setGoal(a, null);
				a.getPath().clear();
			}
		}
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Start a new thread to animate the agents to their goals using the animateToGoalsThread method
	 */
	public void animateToGoals() {
		Thread t = new Thread(() -> animateToGoalsThread());
		t.start();
	}
	
	/**
	 * Animate the agents to their goals
	 */
	public void animateToGoalsThread() {
		// Set the time in milliseconds between each step of the route
		long msStepTime = 1000; // 1 second
		long lastTime = 0;
		long currentTime = 0;
		
		// Let the buttons know that a route is in progress so they can disable themselves
		agentsMoving = true;
		setChanged();
		notifyObservers();
		
		// Check that at least one agent has a goal (otherwise there's no point executing this)
		if (oneHasGoal()) {
			// Step number to know when to stop looping and to know what step of the route to execute
			int stepNum = 0;
			
			// The longest path of any of the agents (we need to stop looping after this)
			int longestPath = longestPath();
			
			// Loop until all agents are at their goals
			while (stepNum < longestPath) {
				// Change the current time of this loop
				currentTime = System.currentTimeMillis();
				
				// If it has been longer than the time for 1 step then execute the next step
				if (currentTime - lastTime > msStepTime) {
					// Iterate through the agents and if they have a goal and their path isn't empty yet then execute
					// their next move according to the current step number
					for (Agent a : agents) {
						if (a.getGoal() != null && !a.getPathNoS().isEmpty()) {
							step(a, stepNum);
						}
					}
					// Increment the step number, update last time and notify GUI to update
					stepNum++;
					lastTime = currentTime;
					setChanged();
					notifyObservers();
				}
			}
			// When everything has ended clear the paths
			for (Agent a : agents) {
				a.getPath().clear();
				setGoal(a, null);
			}
			// Tell the buttons they can enable themselves again
			agentsMoving = false;
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Returns true if at least one agent has a goal
	 * @return
	 */
	private boolean oneHasGoal() {
		for (Agent a : agents) {
			if (a.getGoal() != null)
				return true;
		}
		return false;
	}
	
	/**
	 * Perform one step through the path for one particular agent at a particular step number
	 * @param a
	 *            the Agent to step
	 * @param n
	 *            the step number that we are on
	 */
	private void step(Agent a, int n) {
		// The point we are stepping to from the path of the agent including the goal but not the start (getPathNoS)
		SpacetimePoint p = a.getPathNoS().get(n);
		
		// Create and find the node on the map where this point is and set the Agent's new position to it
		Node node;
		try {
			node = map.nodeAt(p.getX(), p.getY());
			a.setStart(node);
			if (a.getStart().equals(a.getGoal())) {
				setGoal(a, null);
			}
		} catch (InvalidCoordinateException e) {
			// Shouldn't be thrown as CA* only adds nodes in the map to the path
			e.printStackTrace();
			System.err.println("Tried to move to an invalid node in model.step()");
		}
	}
	
	/**
	 * Returns the length of the longest path across all agents
	 * @return longest path
	 */
	private int longestPath() {
		int l = 0;
		for (Agent a : agents) {
			int al = a.getPathNoS().size();
			if (al > l)
				l = al;
		}
		return l;
	}
	
}
