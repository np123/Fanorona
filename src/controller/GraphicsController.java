package controller;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import model.State;

public class GraphicsController implements MouseListener{	
	
	private view.UserInterface UI;
	private JFrame window;
	
	public GraphicsController(){
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		int width = (int) gd.getDefaultConfiguration().getBounds().getWidth();
		int height = (int) gd.getDefaultConfiguration().getBounds().getHeight();
		
		model.State current = new model.State();
		view.UserInterface UI = new view.UserInterface();
		this.UI = UI;
		JFrame window = new JFrame();
		this.window = window;
		window.setSize(width, height);
		window.add(UI);
		UI.addMouseListener(this);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		/* 
		 * Map the x and y coordinate of the click to the node
		 * Ignore if there is no node
		 * Otherwise process the move
		 * 
		 * */

		/*System.out.println("x-coordinate: " + e.getX());
		System.out.println("y-coordinate: " + e.getY());*/		
		
		int black = 0, white = 0;
		for (int i = 0; i < State.numPieces; i++){
			if (State.getPiece(i).getColor() == Color.BLACK) black++;
			else white++;
		}				
		
		Point click = new Point(e.getX(), e.getY());
		
		for (int x = 0; x < 45; x++){
			if (model.Board.getNode(x).distanceTo(click) < 50){
				//System.out.println(x);
				model.Board.processTurn(x, x % 9, x / 9);
			}
		}
		
		UI.update();////
		
		if (black == 0 || white == 0){
			System.exit(0);			
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		e.consume();
		/*System.out.println("x-coordinate: " + e.getX());
		System.out.println("y-coordinate: " + e.getY());*/
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		e.consume();
		/*System.out.println("x-coordinate: " + e.getX());
		System.out.println("y-coordinate: " + e.getY());*/
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		e.consume();
		/*System.out.println("x-coordinate: " + e.getX());
		System.out.println("y-coordinate: " + e.getY());*/
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		e.consume();
		/*System.out.println("x-coordinate: " + e.getX());
		System.out.println("y-coordinate: " + e.getY());*/
	}
	
}
