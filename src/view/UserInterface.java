package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
/*import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;*/
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
//import javax.swing.JFrame;
import javax.swing.JPanel;

public class UserInterface extends JPanel {

	final static int windowHeight;
	final static int windowWidth;
	
	final static int boardWidth;
	final static int boardHeight;
	final static int tileSize;
	final static int startWidth;
	final static int startHeight;

	final static int[] horz;
	final static int[] vert;
	
	static BufferedImage boardImage = null;
	static BufferedImage backImage = null;
	
	static {	
	
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		windowWidth = (int) gd.getDefaultConfiguration().getBounds().getWidth();
		windowHeight = (int) gd.getDefaultConfiguration().getBounds().getHeight();
		
		boardHeight = 8*windowHeight/10;
		boardWidth = 8*windowWidth/10;
		tileSize = 75;				
		
		startWidth = (windowWidth - (8*windowWidth/10))/4 - windowWidth/tileSize;
		startHeight = (windowHeight - (8*windowHeight/10))/2 - windowHeight/tileSize;
		
		horz = new int[9];
		for (int x = 0; x < 9; x++){
			horz[x] = startWidth + (x + 1) * (boardWidth - startWidth)/10; 
		}
		
		vert = new int[5];
		for (int x = 0; x < 5; x++){
			vert[x] = startHeight + (x + 1) * (boardHeight - startHeight)/5; 
		}
		
		try {
			boardImage = ImageIO.read(new File("resources/texture3.bmp"));
			backImage = ImageIO.read(new File("resources/texture2.bmp"));
		} catch (IOException e) {			
			System.out.println("Error: Image files missing from resource");
			e.printStackTrace();
		}
	}	
	
	public UserInterface(){
		
	}		
	
	public static int getHorz(int x){
		return horz[x];
	}
	
	public static int getVert(int x){
		return vert[x];
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBackground(g);
		drawBoard(g);		
		drawRows(g);
		drawDiagonals(g);
		drawNodes(g);		
		drawSpots(g);
	}
	
	public void update(){
		super.repaint();
	}
	
	private void drawRows(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(4.5f));
		
		for (int x = 0; x < horz.length; x++){
			g2d.drawLine(horz[x], vert[0], horz[x], vert[vert.length-1]);
		}
		
		for (int x = 0; x < vert.length; x++){
			g2d.drawLine(horz[0], vert[x], horz[horz.length-1], vert[x]);
		}
		
	}
	
	private void drawDiagonals(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		for (int x = 0; x < 44; x++){
			for (int y = x + 1; y < 45; y++){				
				if (model.Node.isConnected(model.Board.getNode(x), model.Board.getNode(y))){
					g2d.drawLine(model.Board.getNode(x).getX(), model.Board.getNode(x).getY(),model.Board.getNode(y).getX(), model.Board.getNode(y).getY());
				}
			}
			
		}
	}
	
	private void drawBoard(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;								
				
		g2d.setPaint(new TexturePaint(boardImage, new Rectangle(tileSize,tileSize)));
		g2d.fillRect(startWidth, startHeight, boardWidth, boardHeight);
	}
	
	private void drawBackground(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setPaint(new TexturePaint(backImage, new Rectangle(75,75)));		
		g2d.fillRect(0,0,windowWidth,windowHeight);		
	}
	
	private void drawSpots(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		Color cream = new Color (255,229,204);
		g2d.setColor(cream);
		
		for (int x = 0; x < model.State.numPieces; x++){
			if (model.State.getPiece(x).getColor() == Color.BLACK) g2d.setColor(Color.RED);
			if (model.State.getPiece(x).getColor() == Color.WHITE) g2d.setColor(Color.BLUE);
			g.fillOval(model.State.getPiece(x).getScreenX() - 25, model.State.getPiece(x).getScreenY() - 25, 50, 50);
		}
		
	}
	
	private void drawNodes(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		drawCircles(g2d);
		drawOutline(g2d);
	}
	
	private void drawCircles(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		Color cream = new Color (255,229,204);
		g2d.setColor(cream);
				
		for (int x = 0; x < 45; x++){			
			g.fillOval(model.Board.getNode(x).getX() - 25, model.Board.getNode(x).getY() - 25, 50, 50);
		}
		
	}
	
	private void drawOutline(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(4.5f));
				
		for (int x = 0; x < 45; x++){			
			g.drawOval(model.Board.getNode(x).getX() - 25, model.Board.getNode(x).getY() - 25, 50, 50);
		}
		
	}
	
	
	
}
