package pathfinding.visualisation;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import pathfinding.MultiRobotPlanning;
import pathfinding.RobotPlanning;
import pathfinding.SingleRobotPlanning;
import pathfinding.data.WarehouseMap;

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
		
//		GridMap map = MapUtils.createMarkingWarehouseMap();
//		MapData mapData = new MapData(map);
		WarehouseMap map = new WarehouseMap();
		
		// NOTE number of robots must be manually set here as it stands
		// Number of robots to use; 1 or more (only tested for up to 4 robots, should scale up to 6 though)
		// beyond 6 robots exceptions will likely be thrown because robots will try and start outside the map
		// other than this though it should theoretically work for more robots
		int robotNumber = 3;
		
		// Choose which planning method to use based on the number of robots
		// Single uses basic A*, multiple uses Cooperative A*, both with Manhattan distance as heuristic
		RobotPlanning rp = robotNumber == 1 ? new SingleRobotPlanning(map) : new MultiRobotPlanning(map);
		
		// Create model to interface with search classes and views to display information and map
		PathfindingModel model = new PathfindingModel(rp, map, robotNumber);
		MapView mapView = new MapView(model, map);
		IntructionsView instructionsView = new IntructionsView();
		ControlView controlView = new ControlView(model);
		
		model.addObserver(mapView);
		
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
