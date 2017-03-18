package model;

import java.awt.Color;

public class Piece {

	private int x;
	private int y;
	private final Color color;
	
	public Piece (int x, int y, Color color){
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public Piece (Piece pc){
		this.x = pc.getX();
		this.y = pc.getY();
		this.color = pc.getColor();
	}
	
	public int getX(){
		return x;
	}
	
	public int getPosition(){
		return x*9+y;
	}		
	
	public int getY(){
		return y;
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getScreenX(){
		int index = this.getPosition();
		return Board.nodes[index].getX();
	}
	
	public int getScreenY(){
		int index = this.getPosition();
		return Board.nodes[index].getY();
	}
	
	public void movePosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString(){
		return "Piece at: " + getPosition() + " with Color " + color;
	}
	
	@Override
	public boolean equals(Object other){
		Piece second = (Piece) other;
		return this.x == second.x && this.y == second.y && this.color == second.color;
	}
}
