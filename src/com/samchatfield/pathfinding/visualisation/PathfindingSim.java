package com.samchatfield.pathfinding.visualisation;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.samchatfield.pathfinding.AgentPlanning;
import com.samchatfield.pathfinding.MultiAgentPlanning;
import com.samchatfield.pathfinding.SingleAgentPlanning;
import com.samchatfield.pathfinding.data.WorldMap;

/**
 * Main class to launch the GUI visualisation of route planning. GUI build using MVC design paradigm (not conventionally perfect I concede)
 * @author Sam
 */
@SuppressWarnings("serial")
public class PathfindingSim extends JPanel {
	
	public PathfindingSim() {
		super();
		setLayout(new BorderLayout());
		
		// Create the GridMap instance to use for the warehouse and pass into helper class 'MapData'
		// to build search nodes and other search-related functionality
		
		WorldMap map = new WorldMap();
		
		// NOTE number of agents must be manually set here as it stands
		// Number of agents to use; 1 or more (only tested for up to 4 agents, should scale up to 6 though)
		// beyond 6 agents exceptions will likely be thrown because agents will try and start outside the map
		// other than this though it should theoretically work for more agents
		int agentNumber = 3;
		
		// Choose which planning method to use based on the number of agents
		// Single uses basic A*, multiple uses Cooperative A*, both with Manhattan distance as heuristic
		AgentPlanning ap = agentNumber == 1 ? new SingleAgentPlanning(map) : new MultiAgentPlanning(map);
		
		// Create model to interface with search classes and views to display information and map
		PathfindingModel model = new PathfindingModel(ap, map, agentNumber);
		MapView mapView = new MapView(model, map);
		IntructionsView instructionsView = new IntructionsView();
		ControlView controlView = new ControlView(model);
		
		model.addObserver(mapView);
		model.addObserver(controlView);
		
		add(mapView, BorderLayout.CENTER);
		add(instructionsView, BorderLayout.NORTH);
		add(controlView, BorderLayout.EAST);
	}
	
	// public void paintComponent(Graphics g) {
	// System.out.println(getWidth() + "x" + getHeight());
	// }
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Pathfinding");
		frame.setSize(1070, 765);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PathfindingSim sim = new PathfindingSim();
		
		frame.add(sim);
		frame.setVisible(true);
	}
	
}
