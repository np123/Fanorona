package controller;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

/**
 * The GraphicsController class communicates with the user and
 * reacts to input by calling appropriate methods in the
 * model and view packages
 *
 */
public class GraphicsController implements MouseListener{	
	
	private view.UserInterface UI;
	
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
		
		/* 
		 * Map the x and y coordinate of the click to the node
		 * Ignore if there is no node
		 * Otherwise process the move
		 * 
		 * */
		
		int black = 0, white = 0;
		for (int i = 0; i < model.State.getNumPieces(); i++){
			if (model.State.getPiece(i).getColor() == Color.BLACK) black++;
			else white++;
		}				
		
		Point click = new Point(e.getX(), e.getY());
		
		for (int x = 0; x < 45; x++){
			if (model.Board.getNode(x).distanceTo(click) < 50){
				model.Logic.processTurn(x, x % 9, x / 9);
			}
		}
		e.consume();
		UI.update();
		
		//One side or the other has won
		if (black == 0 || white == 0){
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
