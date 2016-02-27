package model;

import java.awt.Point;
import java.util.ArrayList;

public class Node {

	private final int x;
	private final int y;
	private final int position;
	
	private ArrayList<Integer> connections = new ArrayList<Integer>();
	
	public Node(int x, int y, int position, ArrayList<Integer> joints){
		this.x = x;
		this.y = y;
		this.position = position;
		this.connections = joints;
	}
	
	public Node(int x, int y, int position){
		this.x = x;
		this.y = y;
		this.position = position;
	}
	
	public void addConnection(int pos){
		connections.add(pos);
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public static boolean isConnected(Node first, Node second){
				
		for (int i = 0; i < first.connections.size(); i++){
			if (first.connections.get(i) == second.getPosition()) return true;			
		}
		return false;
	}
	
	public int getPosition(){
		return position;
	}
	
	public int distanceTo(Point p){
		return (int) Math.sqrt(Math.pow(this.getX() - p.getX(), 2) + Math.pow(this.getY() - p.getY(), 2));
	}
}
