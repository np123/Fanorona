package controller;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import model.State;
import model.Phase;

/**
 * The GraphicsController class communicates with the user and
 * reacts to input by calling appropriate methods in the
 * model and view packages
 *
 */
public class GraphicsController implements MouseListener{	

	private view.UserInterface UI;
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

		model.State s = new model.State();
		view.UserInterface UI = new view.UserInterface();
		UI.setLayout(new view.Layout());		

		endTurn = new JButton();
		endTurn.setText("End Turn");
		endTurn.setName("END");
		endTurn.setEnabled(false);
		UI.add(endTurn);
		
		this.UI = UI;
		
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

		// Ends the current players turn
		if (e.getSource().equals(endTurn)) {
			State.setContinue(false);
			State.resetPhase();
			State.nextTurn();
		}
		
		/* 
		 * Map the x and y coordinate of the click to the node
		 * Ignore if there is no node
		 * Otherwise process the move
		 * 
		 * */	
		
		Point clicked = new Point(e.getX(), e.getY());
		int position = -1;

		for (int x = 0; x < 45; x++){
			if (model.Board.getNode(x).distanceTo(clicked) < 50){
				position = x;	
				break;
			}
		}
		
		if (position == -1) return;

		Phase current = State.getCurrentState();		
		
		switch (current){
		case SELECT:
			model.Logic.processTurn(position);
			break;
		case CAPTURE:
			model.Logic.makeCapture(position);
			break;
		case GRAB:
			model.Logic.grabPiece(position);
			break;
		case MOVE:
			model.Logic.makeMove(position);
			break;		
		default:
			break;
		}
		
		/* Allow the current to player to end turn instead
		 * of continuing his/her capture phase 
		 */
		if (State.getContinue() == true)
			endTurn.setEnabled(true);
		else
			endTurn.setEnabled(false);
		
		UI.update();								

		int black = 0, white = 0;
		for (int i = 0; i < model.State.getNumPieces(); i++){
			if (model.State.getPiece(i).getColor() == Color.BLACK) black++;
			else white++;
		}	

		//One side or the other has won
		if (black == 0 || white == 0){			
			UI.update();					//Insert code for displaying a win and/or starting new game
			System.exit(0);			
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		e.consume();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();
	}	
}
