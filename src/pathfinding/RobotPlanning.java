package pathfinding;

import java.util.ArrayList;

/**
 * Abstract route planning type which has instances SingleRobotPlanning and MultiRobotPlanning
 * @author Sam
 */
public interface RobotPlanning {
	
	/**
	 * Compute the whole plan of all of the Agents' movement from their current positions to their goals (the ArrayList will be modified in
	 * the process)
	 * @param as
	 *            ArrayList of Agents of the system
	 */
	void computePlan(ArrayList<Agent> as);
	
}
