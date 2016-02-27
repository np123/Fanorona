package model;

/**
 * The Board class contains the representation of the game board
 * and also the logic for processing moves 
 *
 */
public class Board {
	
	protected static Node[] nodes = new Node[45];		
	
	public static Node getNode(int x){
		return nodes[x];
	}
}
