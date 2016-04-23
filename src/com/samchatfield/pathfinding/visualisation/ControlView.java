package com.samchatfield.pathfinding.visualisation;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.samchatfield.pathfinding.Agent;

/**
 * The right-hand panel with the radio buttons to select the focused agent and the buttons to compute plan and move to goals
 * @author Sam
 */
@SuppressWarnings("serial")
public class ControlView extends JPanel implements ActionListener {
	
	private final PathfindingModel model;
	
	/**
	 * Create a new instance adding instructions, radio buttons and buttons all in a grid layout of 20 rows to keep the buttons from being
	 * too tall
	 * @param model
	 */
	public ControlView(PathfindingModel model) {
		super();
		this.model = model;
		
		JLabel ins = new JLabel("<html><center>Select agent:&nbsp&nbsp<br></center></html>");
		setLayout(new GridLayout(20, 1));
		add(ins);
		
		JRadioButton[] rbs = new JRadioButton[model.getAgentNumber()];
		ButtonGroup bg = new ButtonGroup();
		for (int i = 0; i < model.getAgentNumber(); i++) {
			rbs[i] = new JRadioButton("" + (i + 1));
			rbs[i].setActionCommand("" + i);
			bg.add(rbs[i]);
			rbs[i].addActionListener(this);
			add(rbs[i]);
		}
		rbs[model.getFocusedAgent().getIndex()].setSelected(true);
		
		JButton compute = new JButton("Compute");
		compute.addActionListener(e -> model.computePaths());
		
		JButton move = new JButton("Move");
		move.addActionListener(e -> model.animateToGoals());
		
		add(compute);
		add(move);
	}
	
	/**
	 * Called when a radio button is clicked, set the focused agent to the one that was clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int clicked = Integer.parseInt(e.getActionCommand());
		Agent a = model.getAgents().get(clicked);
		model.setFocusedAgent(a);
	}
	
}
