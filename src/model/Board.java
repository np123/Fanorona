package model;

import java.util.ArrayList;

public class Board {
	
	public static Node[] nodes = new Node[45];
	
	static {				
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 9; y++) {
				int xpos = view.UserInterface.getHorz(y);
				int ypos = view.UserInterface.getVert(x);
				nodes[x*9+y] = new Node(xpos,ypos, x*9+y);
			}
		}
		
		defineConnections();
		/*for (int x = 0; x < nodes[1].connections.size();x++){
			System.out.println(nodes[1].connections.get(x));
		}
		System.out.println("pos: " + nodes[1].getPosition());
		System.out.println(Node.isConnected(nodes[0], nodes[1]));*/
	}
	
	public static Node getNode(int x){
		return nodes[x];
	}
	
	public static void defineConnections(){
		for (int x = 0; x < 5; x++){
			for (int y = 0; y < 9; y++){				
				if (y < 8) nodes[x*9+y].addConnection(x*9+y+1);
				if (y >= 1) nodes[x*9+y].addConnection(x*9+y-1);
				if (x >= 1) nodes[x*9+y].addConnection((x-1)*9+y);
				if (x < 4) nodes[x*9+y].addConnection((x+1)*9+y);
			}
		}
		
		for (int x = 0; x < 5; x++){
			for (int y = 0; y < 9; y++){
				if ((x*9+y) % 2 != 0) continue;
				if (x > 0 && y > 0) nodes[x*9+y].addConnection((x-1)*9+y-1);
				if (x > 0 && y < 8) nodes[x*9+y].addConnection((x-1)*9+y+1);
				if (x < 4 && y < 8) nodes[x*9+y].addConnection((x+1)*9+y+1);
				if (x < 4 && y > 0) nodes[x*9+y].addConnection((x+1)*9+y-1);
			}
		}				
	}
	
	public static void processTurn (int index, int col, int row){
		
		if (State.selected == true){
			//System.out.println("Selected piece is: " + State.position);
			makeMove(index, col, row);
			return;
		}
		
		for (int x = 0; x < State.numPieces; x++){
			Piece current = State.getPiece(x);
			if (current.getPosition() == index){
				if (current.getColor() == State.getTurn()){
					State.selected = true;
					State.position = index;
				}
			}
		}
		
	}
	
	public static void makeMove (int index, int col, int row){
		
		Piece moving = State.getPiece(0);
		for (int x = 0; x < State.numPieces; x++){
			Piece current = State.getPiece(x);
			if (moving.getPosition() != State.position){
				moving = State.getPiece(x);	
			}			
			if (current.getPosition() == index){
				return;
			}			
		}
		
		/*for (int i = 0; i < State.numPieces; i++){
			if (State.getPiece(i).getPosition() == 13) System.out.println("Sportted" + State.getPiece(i).getColor());
		}*/
		
		captureMade(State.position, index);	//Makes capture
		
		//System.out.println("old: " + moving.getPosition());
		if (!Node.isConnected(nodes[State.position], nodes[index])) return;
		moving.movePosition(row, col);
		State.selected = false;
		State.position = -1;
		State.nextTurn();
		//System.out.println("new: " + moving.getPosition());
				
	}
	
	private static void captureMade(int start, int end){
		/*if (end - start == 9);	//Move down
		if (start - end == 9);	//Move up
		if (end - start == 1);	//Move right
		if (start - end == 1);	//Move left
		if (end - start == 10);	//Move down and right
		if (end - start == 8);	//Move down and left
		if (start - end == 8);	//Move up and right
		if (start - end == 10);	//Move up and left
*/		
		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;
		
		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;
		boolean exit = false;
		
		ArrayList<Integer> captures = new ArrayList<Integer>();
		
		//Approaching piece
		for (int x = endRow, y = endCol; x < 5 && y < 9 && x >= 0 && y >= 0; x+= heightDiff, y+=widthDiff){
			if (exit == true) break;
			if (x == endRow && y == endCol) continue;
			if (x == startRow && y == startCol) continue;
			int location = x*9+y;
			//System.out.println("Test loc: " + location);
			for (Piece current : State.pieces){
				//Make a list of locations of pieces to be captured
				if (current.getPosition() == location && current.getColor() != State.getTurn()){
					//System.out.println("TEST");
					captures.add(location);
				}
				//If the next piece in line is of same colour dont do anything
				if (current.getPosition() == location && current.getColor() == State.getTurn()/* && captures.size() == 0*/){
					exit = true;
					break;
				}
			}
		}
		exit = false;
		//Going away from piece
		for (int x = endRow, y = endCol; x < 5 && y < 9 && x >= 0 && y >= 0; x-= heightDiff, y-=widthDiff){
			if (exit == true) break;
			if (x == endRow && y == endCol) continue;
			if (x == startRow && y == startCol) continue;
			int location = x*9+y;			
			//System.out.println("Test loc1: " + location);
			for (Piece current : State.pieces){
				//Make a list of locations of pieces to be captured
				if (current.getPosition() == location && current.getColor() != State.getTurn()){
					captures.add(location);
				}
				//If the next piece in line is of same colour dont do anything
				if (current.getPosition() == location && current.getColor() == State.getTurn() && captures.size() == 0) return;
				else if (current.getPosition() == location && current.getColor() == State.getTurn()) {
					exit = true;
					break;
				}
			}
		}
		
		//Make capture
		
		//System.out.println(captures.toString());
		for (int pos : captures){
			for (int i = 0; i < State.numPieces; i++){
				if (State.getPiece(i).getPosition() == pos){
					State.pieces.remove(State.getPiece(i));
					State.numPieces -= 1;
				}				
			}
			/*for (Piece current : State.pieces){
				if (current.getPosition() == pos) State.pieces.remove(current);
			}*/
		}
		
	}
	
}
