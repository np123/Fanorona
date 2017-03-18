package controller;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import model.Phase;

/**
 * The GraphicsController class communicates with the user and
 * reacts to input by calling appropriate methods in the
 * model and view packages
 *
 */
public class GraphicsController extends MouseAdapter {	

	private view.UserInterface UI;
	private model.State state;
	private JButton endTurn;	

	/**
	 * Instantiates a GraphicsController and configures the window
	 */
	public GraphicsController(){

		//Sets window height and width based on device graphics settings
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		int width = (int) gd.getDefaultConfiguration().getBounds().getWidth();
		int height = (int) gd.getDefaultConfiguration().getBounds().getHeight();

		state = new model.State();
		Logic.setState(state);
		view.UserInterface UI = new view.UserInterface(state);
		UI.setLayout(new view.Layout());		

		// Button for signaling end of player's turn
		endTurn = new JButton();
		endTurn.setText("End Turn");
		endTurn.setName("END");
		endTurn.setEnabled(false);
		UI.add(endTurn);		
		
		//Creates new JFrame and sets state to visible
		JFrame window = new JFrame();
		window.setSize(width, height);
		window.add(UI);
		UI.addMouseListener(this);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);	
	}


	/**
	 * Determines if mouse was clicked while overtop a node
	 * Calls {@link model.Board#processTurn(int, int, int)} to process event
	 * Triggers update of the user interface
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		/* 
		 * Ends the current players turn when
		 * the 'End Turn' button is clicked
		 */		
		if (e.getSource().equals(endTurn)) {
			state.setContinue(false);
			state.resetPhase();
			state.nextTurn();
		}
		
		/* 
		 * Map the x and y coordinate of the click to the node
		 * Ignore if there is no node
		 * Otherwise process the move
		 * 
		 */
		Point clicked = new Point(e.getX(), e.getY());
		int position = -1;

		for (int x = 0; x < 45; x++){
			if (model.Board.getNode(x).distanceTo(clicked) < 50){
				position = x;	
				break;
			}
		}
		
		// Click is not on a valid node on the board
		if (position == -1) return;

		// Select the action to perform based upon the current state
		Phase current = state.getCurrentState();				
		switch (current){
		case SELECT:
			controller.Logic.processTurn(position);
			break;
		case CAPTURE:
			controller.Logic.makeCapture(position);
			break;
		case GRAB:
			controller.Logic.grabPiece(position);
			break;
		case MOVE:
			controller.Logic.makeMove(position);
			break;		
		default:
			break;
		}
		
		/* 
		 * Allow the current to player to end turn instead
		 * of continuing his/her capture phase 
		 */
		if (state.getContinue() == true)
			endTurn.setEnabled(true);
		else
			endTurn.setEnabled(false);
		
		UI.update();								

		
		/*
		 * Count pieces for each side to determine if game has ended
		 */
		int black = 0, white = 0;
		for (int i = 0; i < state.getNumPieces(); i++){
			if (state.getPiece(i).getColor() == Color.BLACK) black++;
			else white++;
		}	

		//True if either side has won
		if (black == 0 || white == 0){			
			UI.update();					//Insert code for displaying a win and/or starting new game
			System.exit(0);	
			//TODO Add proper response to game ended event
		}

	}	
}
