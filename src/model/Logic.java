package model;

import java.awt.Color;
import java.util.ArrayList;

public class Logic {

	public static void processTurn (int index, int col, int row){

		//Selecting pieces to be taken off the board
		if (State.grabPiece == true){
			selectCapture(State.startPosition, State.endPosition, index);
			makeMove(State.endPosition, State.endPosition%9, State.endPosition/9);
			return;
		}
		
		//There is a possible capture. Check that the piece selected can make a capture move
		if (State.capture == true && State.selected == false){
			for (Piece current : State.pieces){
				if (current.getPosition() == index && current.getColor() == State.getTurn()){
					if (checkCapture(current)){
						State.selected = true;
						State.startPosition = index;
						return;
					}
				}
			}
			return;
		}
		
		//Piece that will be making capture is selected. index is where the piece will end up
		if (State.capture == true){
			if (!Node.isConnected(Board.nodes[State.startPosition], Board.nodes[index])) return;
			if ((State.approachCapture && State.withdrawCapture) == false){
				if (State.approachCapture) approachCapture(State.startPosition, index);
				else withdrawCapture(State.startPosition, index);	
				State.capture = false;
			} else {
				System.out.println("PROB");
				State.grabPiece = true;
				State.endPosition = index;
				return;
			}
		}
		
		//If no capture to be made
		if (State.selected == true){
			makeMove(index, col, row);
			return;
		}		
		
		//Selects piece for non-capture move
		for (int x = 0; x < State.getNumPieces(); x++){
			Piece current = State.getPiece(x);
			if (current.getPosition() == index)
				if (current.getColor() == State.getTurn()){
					State.selected = true;
					State.startPosition = index;
				}
		}

	}

	private static void makeMove (int index, int col, int row){

		Piece moving = State.getPiece(0);
		for (int x = 0; x < State.getNumPieces(); x++){
			Piece current = State.getPiece(x);
			if (moving.getPosition() != State.startPosition) moving = State.getPiece(x);				
			if (current.getPosition() == index) return;		
		}

		//captureMade(State.startPosition, index);	//Makes capture

		if (!Node.isConnected(Board.nodes[State.startPosition], Board.nodes[index])) return;
		moving.movePosition(row, col);
		State.selected = false;
		State.startPosition = -1;
		State.nextTurn();				
	}

	private static void captureMade(int start, int end){
		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;

		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;
		boolean exit = false;

		ArrayList<Integer> approach = new ArrayList<Integer>();

		//Approaching piece
		for (int x = endRow, y = endCol; x < 5 && y < 9 && x >= 0 && y >= 0; x+= heightDiff, y+=widthDiff){
			if (exit == true) break;
			if (x == endRow && y == endCol) continue;
			if (x == startRow && y == startCol) continue;
			boolean madeCapture = false;
			int location = x*9+y;
			for (Piece current : State.pieces){
				//Make a list of locations of pieces to be captured
				if (current.getPosition() == location){
					if (current.getColor() != State.getTurn()) {						
						approach.add(location);
						madeCapture = true;
					}
					else {
						//If the next piece in line is of same colour stop
						exit = true;
						break;
					}
				}			
			}
			if (!madeCapture) {
				exit = true;
				break;
			}
		}
		exit = false;

		ArrayList<Integer> withdraw = new ArrayList<Integer>();

		//Going away from piece
		for (int x = endRow, y = endCol; x < 5 && y < 9 && x >= 0 && y >= 0; x-= heightDiff, y-=widthDiff){
			if (exit == true) break;
			if (x == endRow && y == endCol) continue;
			if (x == startRow && y == startCol) continue;
			int location = x*9+y;			
			for (Piece current : State.pieces){
				//Make a list of locations of pieces to be captured
				if (current.getPosition() == location && current.getColor() != State.getTurn()){
					withdraw.add(location);
				}
				//If the next piece in line is of same colour dont do anything
				if (withdraw.size() == 0) {
					exit = true;
					break;
				}
				else if (current.getPosition() == location && current.getColor() == State.getTurn() && withdraw.size() == 0) return;
				else if (current.getPosition() == location && current.getColor() == State.getTurn()) {
					exit = true;
					break;
				}
			}
		}

		//Make capture
		//if (approach.size() > 0 && withdraw.size() > 0) selectCapture();

		for (int pos : approach)
			for (int i = 0; i < State.getNumPieces(); i++)
				if (State.getPiece(i).getPosition() == pos){
					State.pieces.remove(State.getPiece(i));
					State.setNumPieces(State.getNumPieces() - 1);
				}							

		for (int pos : withdraw)
			for (int i = 0; i < State.getNumPieces(); i++)
				if (State.getPiece(i).getPosition() == pos){
					State.pieces.remove(State.getPiece(i));
					State.setNumPieces(State.getNumPieces() - 1);
				}								
	}


	private static void selectCapture(int start, int end, int capture){
		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;

		int selectX = capture % 9;
		int selectY = capture / 9;
		
		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;
		
		for (int x = 0; x < 9; x++){
			for (int y = 0; y < 5; y++){
				if (selectX == endRow + x*widthDiff && selectY == endCol + y*heightDiff) {
					approachCapture(State.startPosition, end);
					State.selected = false;
					State.capture = false;
					State.grabPiece = false;
					return;
				}
				if (selectX == endRow - x*widthDiff && selectY == endCol - y*heightDiff) {
					withdrawCapture(State.startPosition, end);
					State.selected = false;
					State.capture = false;
					State.grabPiece = false;
					return;
				}
			}
		}	
	}

	private static boolean checkCapture(Piece current){
		Color color = current.getColor();
		int x = current.getX();
		int y = current.getY();
		Color opp;
		
		int[] found = new int[8];
		ArrayList<?>[] found1 = new ArrayList<?>[8];
		for (int i = 0; i < 8; i++) found1[i] = new ArrayList<Node>();
		
		//x is Row #, y is Col #
		
		//Approach capture
		for (int i = 0; i < 8; i++) found[i] = 0;
		for (int disp = 1; disp < 3; disp++){
			if (disp == 1) opp = null;
			else{
				if (color == Color.BLACK) opp = Color.WHITE;
				else opp = Color.BLACK;
			}				
			if (State.getPiece(x + disp, y, opp)) found[0]++;
			if (State.getPiece(x - disp, y, opp)) found[1]++;
			if (State.getPiece(x, y + disp, opp)) found[2]++;
			if (State.getPiece(x, y - disp, opp)) found[3]++;
			if (State.getPiece(x + disp, y + disp, opp)) found[4]++;
			if (State.getPiece(x - disp, y - disp, opp)) found[5]++;
			if (State.getPiece(x + disp, y - disp, opp)) found[6]++;
			if (State.getPiece(x - disp, y + disp, opp)) found[7]++;				
		}
		for (int i = 0; i < 8; i++){
			if (found[i] == 2){
				State.approachCapture = true;
			}
		}
		
		
		//Withdraw capture
		for (int i = 0; i < 8; i++) found[i] = 0;
		for (int disp = -1; disp < 2; disp+=2){
			if (disp == -1) opp = null;
			else{
				if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
				else opp = Color.BLACK;
			}				
			if (State.getPiece(x + disp, y, opp)) found[0]++;
			if (State.getPiece(x - disp, y, opp)) found[1]++;
			if (State.getPiece(x, y + disp, opp)) found[2]++;
			if (State.getPiece(x, y - disp, opp)) found[3]++;
			if (State.getPiece(x + disp, y + disp, opp)) found[4]++;
			if (State.getPiece(x - disp, y - disp, opp)) found[5]++;
			if (State.getPiece(x + disp, y - disp, opp)) found[6]++;
			if (State.getPiece(x - disp, y + disp, opp)) found[7]++;						
		}
		for (int i = 0; i < 8; i++){
			if (found[i] == 2){
				State.withdrawCapture = true;
			}
		}		
		return (State.approachCapture == true || State.withdrawCapture == true);			
	}
	
	
	/**
	 * Checks if there are any valid capture moves for the current player
	 * @return true if capture found, otherwise false
	 */
	public static boolean checkCapture(){

		int[] found = new int[8];
		Color opp;
		if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
		else opp = Color.BLACK;		

		//Approach capture check
		for (Piece current : State.pieces){
			if (current.getColor() != State.getTurn()) continue;
			int x = current.getX();
			int y = current.getY();
			
			for (int i = 0; i < 8; i++) found[i] = 0;
			for (int disp = 1; disp < 3; disp++){
				if (disp == 1) opp = null;
				else{
					if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
					else opp = Color.BLACK;
				}				
				if (State.getPiece(x + disp, y, opp)) found[0]++;
				if (State.getPiece(x - disp, y, opp)) found[1]++;
				if (State.getPiece(x, y + disp, opp)) found[2]++;
				if (State.getPiece(x, y - disp, opp)) found[3]++;
				if (State.getPiece(x + disp, y + disp, opp)) found[4]++;
				if (State.getPiece(x - disp, y - disp, opp)) found[5]++;
				if (State.getPiece(x + disp, y - disp, opp)) found[6]++;
				if (State.getPiece(x - disp, y + disp, opp)) found[7]++;			
			}
			for (int i = 0; i < 8; i++){
				if (found[i] == 2) return true;
			}
		}		
		
		//Withdraw capture check
		for (Piece current : State.pieces){
			if (current.getColor() != State.getTurn()) continue;
			int x = current.getX();
			int y = current.getY();
			
			for (int i = 0; i < 8; i++) found[i] = 0;
			for (int disp = -1; disp < 2; disp+=2){
				if (disp == -1) opp = null;
				else{
					if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
					else opp = Color.BLACK;
				}				
				if (State.getPiece(x + disp, y, opp)) found[0]++;
				if (State.getPiece(x - disp, y, opp)) found[1]++;
				if (State.getPiece(x, y + disp, opp)) found[2]++;
				if (State.getPiece(x, y - disp, opp)) found[3]++;
				if (State.getPiece(x + disp, y + disp, opp)) found[4]++;
				if (State.getPiece(x - disp, y - disp, opp)) found[5]++;
				if (State.getPiece(x + disp, y - disp, opp)) found[6]++;
				if (State.getPiece(x - disp, y + disp, opp)) found[7]++;					
			}
			for (int i = 0; i < 8; i++){
				if (found[i] == 2) return true;
			}
		}
		return false;
	}

	private static void approachCapture(int start, int end){
		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;

		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;
		boolean exit = false;

		ArrayList<Integer> approach = new ArrayList<Integer>();

		//Approaching piece
		for (int x = endRow + heightDiff, y = endCol + widthDiff; x < 5 && y < 9 && x >= 0 && y >= 0; x+= heightDiff, y+=widthDiff){
			if (exit == true) break;
			boolean madeCapture = false;
			int location = x*9+y;

			//Search pieces for the ones that will be captured
			for (Piece current : State.pieces){
				if (current.getPosition() == location){
					if (current.getColor() != State.getTurn()) {						
						approach.add(location);
						madeCapture = true;
					}
					else {
						//If the next piece in line is of same colour stop
						exit = true;
						break;
					}
				}			
			}
			if (!madeCapture) break;
		}
		for (int pos : approach)
			for (int i = 0; i < State.getNumPieces(); i++)
				if (State.getPiece(i).getPosition() == pos){
					State.pieces.remove(State.getPiece(i));
					State.setNumPieces(State.getNumPieces() - 1);
				}	
	}

	private static void withdrawCapture(int start, int end){

		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;

		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;
		boolean exit = false;
		ArrayList<Integer> withdraw = new ArrayList<Integer>();

		//Going away from piece
		for (int x = endRow, y = endCol; x < 5 && y < 9 && x >= 0 && y >= 0; x-= heightDiff, y-=widthDiff){
			if (exit == true) break;
			if (x == endRow && y == endCol) continue;
			if (x == startRow && y == startCol) continue;
			int location = x*9+y;			
			for (Piece current : State.pieces){
				//Make a list of locations of pieces to be captured
				if (current.getPosition() == location && current.getColor() != State.getTurn()){
					withdraw.add(location);
				}
				//If the next piece in line is of same colour dont do anything
				if (withdraw.size() == 0) {
					exit = true;
					break;
				}
				else if (current.getPosition() == location && current.getColor() == State.getTurn() && withdraw.size() == 0) return;
				else if (current.getPosition() == location && current.getColor() == State.getTurn()) {
					exit = true;
					break;
				}
			}
		}
		for (int pos : withdraw)
			for (int i = 0; i < State.getNumPieces(); i++)
				if (State.getPiece(i).getPosition() == pos){
					State.pieces.remove(State.getPiece(i));
					State.setNumPieces(State.getNumPieces() - 1);
				}


	}

}