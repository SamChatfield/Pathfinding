package pathfinding.visualisation;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The panel for the profoundly instructive JLabel at the top of the screen
 * @author Sam
 */
@SuppressWarnings("serial")
public class IntructionsView extends JPanel {
	
	public IntructionsView() {
		super();
		
		JLabel ins = new JLabel("<html><center>Click a node to place goal</center></html>");
		
		add(ins);
	}
		
}
