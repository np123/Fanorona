package model;

import java.awt.Color;
import java.util.ArrayList;


public class Logic {

	/*
	 * First phase: Piece selection
	 * If a capture can be made, make sure appropriate piece is selected
	 * If no capture then select any piece
	 * Second phase: Destination selection
	 * If it was a regular move, then update and end
	 * If move was a capture:
	 * Third phase:
	 * Highlight the pieces that can be captured
	 * Make user choose the pieces to be removed
	 * Fourth (ongoing) phase:
	 * If possible, give user option to continue capture phase
	 * Keeps path (and direction) on a stack
	 * Exit when no legal continuances or user chooses to end turn
	 */


	//First phase
	public static void processTurn(int position){

		if (position == -1) return;
		if (State.getContinue() == true && position != State.getEndPosition()) {			
			return;
		} else if (State.getContinue() == true){
			State.setStartPosition(State.getEndPosition());
		}
		
		boolean take = checkCapture();
		
		// True when the current player can make a capture on his move
		if (take){																			//If there is a capture on the board
			
			Piece current = State.getPiece(position / 9, position % 9, State.getTurn());
			if (current == null) return;
			State.setSelected(current);
			
			// True when the selected piece can make a valid capture
			if (checkCapture(current)){
				State.setCurrentState(Phase.CAPTURE);
				State.setStartPosition(position);
				return;
			} else{				
				State.setStartPosition(-1);
				State.setCurrentState(Phase.SELECT);
				return;				
			}			
		} else {
			State.setCurrentState(Phase.MOVE);					//No capture on the board
			State.setSelected(State.getPiece(position / 9, position % 9, State.getTurn()));
			State.setStartPosition(position);
		}
	}


	//Second phase: no capture
	public static void makeMove(int position){

		/*
		 * Access the piece to be moved
		 * Ensure that the destination square is empty
		 */ 
		int start = State.getStartPosition();
		Piece moving = State.getPiece(0);
		for (int x = 0; x < State.getNumPieces(); x++){
			Piece current = State.getPiece(x);
			if (moving.getPosition() != start) moving = State.getPiece(x);				
			if (current.getPosition() == position){
				State.resetPhase();
				return;		
			}
		}
		
		// Ensure starting and destination nodes are adjacent
		if (!Node.isConnected(Board.getNode(position), Board.getNode(start))){
			State.resetPhase();
			return;
		}
		
		// Move the selected piece to its destination position and end current player's turn
		moving.movePosition(position / 9, position % 9);
		State.resetPhase();
		State.nextTurn();
	}

	//Second phase: capture
	public static void makeCapture(int end){

		int start = State.getStartPosition();		

		// Ensure starting and destination locations are adjacent
		if (!Node.isConnected(Board.getNode(start), Board.getNode(end))) {
			State.setCurrentState(Phase.SELECT);
			return;
		}
		
		// Ensure the target position is a valid destination node
		if (!State.validDestination(end)) {			
			State.setCurrentState(Phase.SELECT);
			return;
		}

		// Find and store the set of all pieces available to be captured with the current move
		boolean near = checkApproach(start,end);
		boolean far = checkWithdraw(start, end);
		if (near || far) {
			Piece moving = State.getPiece(start / 9, start % 9, State.getTurn());
			moving.movePosition(end / 9, end % 9);
			State.setEndPosition(end);
			State.addToPath(start);										//Marks start position as visited
			State.addToPath(end);										//Marks end position as visited
			State.setSelected(null);
			State.setCurrentState(Phase.GRAB);		
		}
	}

	//Third phase: select pieces to be removed
	public static void grabPiece(int position){		

		// True when pieces can be captured upon approach
		if (State.getApproachCapturable() != null)
			for (Piece taken : State.getApproachCapturable()){
				if (taken.getPosition() == position){
					approachCapture();
					State.setApproachCapturable(null);
					State.setWithdrawCapturable(null);	
					int pos = State.getEndPosition();					
					if (!checkCapture(State.getPiece(pos / 9,pos % 9,State.getTurn()))){
						State.setContinue(false);
						State.resetPhase();
						State.nextTurn();
						return;
					} else {
						State.setContinue(true);
						State.setSelected(State.getPiece(pos / 9,pos % 9,State.getTurn()));
						State.setCurrentState(Phase.SELECT);
						return;
					}
				}
			}
		
		// True when pieces can be captured upon withdrawal
		if (State.getWithdrawCapturable() != null)
			for (Piece taken : State.getWithdrawCapturable()){
				if (taken.getPosition() == position){
					withdrawCapture();
					State.setApproachCapturable(null);
					State.setWithdrawCapturable(null);
					int pos = State.getEndPosition();
					if (!checkCapture(State.getPiece(pos / 9,pos % 9,State.getTurn()))){						
						State.setContinue(false);
						State.resetPhase();
						State.nextTurn();
						return;
					} else {
						State.setContinue(true);
						State.setSelected(State.getPiece(pos / 9,pos % 9,State.getTurn()));
						State.setCurrentState(Phase.SELECT);
						return;
					}
				}
			}

	}
	
	// Performs a capture by approach, removing the appropriate pieces
	private static void approachCapture(){
		for (Piece taken : State.getApproachCapturable()){
			State.getPieces().remove(taken);
			State.setNumPieces(State.getNumPieces() - 1);
		}
	}

	// Performs a capture by withdrawal, removing the appropriate pieces
	private static void withdrawCapture(){
		for (Piece taken : State.getWithdrawCapturable()){
			State.getPieces().remove(taken);
			State.setNumPieces(State.getNumPieces() - 1);
		}
	}


	/**
	 * Checks if an approach capture can be made
	 * @param start starting position of piece being moved
	 * @param end final position of piece being moved 
	 */
	private static boolean checkApproach(int start, int end){
		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;

		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;

		ArrayList<Piece> approach = new ArrayList<Piece>();

		//Approaching piece
		for (int x = endRow, y = endCol; x < 5 && y < 9 && x >= 0 && y >= 0; x+= heightDiff, y+=widthDiff){
			if (x == endRow && y == endCol) continue;
			if (x == startRow && y == startCol) continue;
			boolean madeCapture = false;
			int location = x*9+y;
			
			//Make a list of locations of pieces to be captured
			for (Piece current : State.getPieces()){								
				if (current.getPosition() == location){
					if (current.getColor() != State.getTurn()) {						
						approach.add(current);
						madeCapture = true;
					}
				}			
			}
			if (!madeCapture) {
				break;
			}
		}
		State.setApproachCapturable(approach); 
		return approach.size() > 0;
	}

	/**
	 * Checks if a capture by withdrawal can be made
	 * @param start starting position of piece being moved
	 * @param end final position of piece being moved 
	 */
	private static boolean checkWithdraw(int start, int end){
		int startCol = start % 9; //left-to-right
		int startRow = start / 9; //up-down
		int endCol = end % 9;
		int endRow = end / 9;

		int heightDiff = endRow - startRow;
		int widthDiff = endCol - startCol;
		boolean madeCapture = false;

		ArrayList<Piece> withdraw = new ArrayList<Piece>();

		Color opponent;
		if (State.getTurn() == Color.BLACK) opponent = Color.WHITE;
		else opponent = Color.BLACK;


		for (int x = startRow - heightDiff, y = startCol - widthDiff; x < 5 && y < 9 && x >= 0 && y >= 0; x-= heightDiff, y-=widthDiff){
			madeCapture = false;
			for (Piece current : State.getPieces()){
				if (current.getColor() == opponent)
					if (current.getX() == x && current.getY() == y){
						withdraw.add(current);					//Opponent piece found
						madeCapture = true;
					}
			}
			if (!madeCapture) break;
		}
		State.setWithdrawCapturable(withdraw);
		return withdraw.size() > 0;
	}

	public static boolean checkCapture(Piece current){
		Color color = current.getColor();
		int x = current.getX();						//x is Row #, y is Col #
		int y = current.getY();
		Color opp;
		
		boolean approachCapture = false;
		boolean withdrawCapture = false;
		
		ArrayList<ArrayList<Node>> found1 = new ArrayList<ArrayList<Node>>();
		for (int i = 0; i < 8; i++) found1.add(new ArrayList<Node>());			
	

		//Approach capture
		for (int i = 0; i < 8; i++) found1.set(i, new ArrayList<Node>());
		for (int disp = 1; disp < 3; disp++){
			if (disp == 1) opp = null;
			else{
				if (color == Color.BLACK) opp = Color.WHITE;
				else opp = Color.BLACK;
			}			
			if (State.checkPiece(x + disp, y, opp) && ((x+disp)*9+y) >= 0 && ((x+disp)*9+y) < 45) found1.get(0).add(Board.getNode((x+disp)*9+y));
			if (State.checkPiece(x - disp, y, opp) && ((x-disp)*9+y) >= 0 && ((x-disp)*9+y) < 45) found1.get(1).add(Board.getNode((x-disp)*9+y));
			if (State.checkPiece(x, y + disp, opp) && (x*9+y+disp) >= 0 && (x*9+y+disp) < 45) found1.get(2).add(Board.getNode(x*9+y+disp));
			if (State.checkPiece(x, y - disp, opp) && (x*9+y-disp) >= 0 && (x*9+y-disp) < 45) found1.get(3).add(Board.getNode(x*9+y-disp));
			if (State.checkPiece(x + disp, y + disp, opp) && ((x+disp)*9+y+disp) >= 0 && ((x+disp)*9+y+disp) < 45) found1.get(4).add(Board.getNode((x+disp)*9+y+disp));
			if (State.checkPiece(x - disp, y - disp, opp) && ((x-disp)*9+y-disp) >= 0 && ((x-disp)*9+y-disp) < 45) found1.get(5).add(Board.getNode((x-disp)*9+y-disp));
			if (State.checkPiece(x + disp, y - disp, opp) && ((x+disp)*9+y-disp) >= 0 && ((x+disp)*9+y-disp) < 45) found1.get(6).add(Board.getNode((x+disp)*9+y-disp));
			if (State.checkPiece(x - disp, y + disp, opp) && ((x-disp)*9+y+disp) >= 0 && ((x-disp)*9+y+disp) < 45) found1.get(7).add(Board.getNode((x-disp)*9+y+disp));				
		}
		for (int i = 0; i < 8; i++){			
			if (found1.get(i).size() == 2){
				for (int j = 1; j < found1.get(i).size();j++) {					
					if (Node.isConnected(found1.get(i).get(j), found1.get(i).get(j-1))){
						if (State.pathContains(found1.get(i).get(j-1).getPosition())) {
							continue;
						}
						State.addDestination(found1.get(i).get(j-1).getPosition());
						approachCapture = true;
					}
				}
			}
		}

		for (int i = 0; i < 8; i++) found1.set(i,new ArrayList<Node>());			
		//x is Row #, y is Col #

		//Withdraw capture
		for (int i = 0; i < 8; i++) found1.set(i, new ArrayList<Node>());
		for (int disp = -1; disp < 2; disp+=2){
			if (disp == -1) opp = null;
			else{
				if (color == Color.BLACK) opp = Color.WHITE;
				else opp = Color.BLACK;
			}
			if (State.checkPiece(x + disp, y, opp) && ((x+disp)*9+y) >= 0 && ((x+disp)*9+y) < 45) found1.get(0).add(Board.getNode((x+disp)*9+y));
			if (State.checkPiece(x - disp, y, opp) && ((x-disp)*9+y) >= 0 && ((x-disp)*9+y) < 45) found1.get(1).add(Board.getNode((x-disp)*9+y));
			if (State.checkPiece(x, y + disp, opp) && (x*9+y+disp) >= 0 && (x*9+y+disp) < 45) found1.get(2).add(Board.getNode(x*9+y+disp));
			if (State.checkPiece(x, y - disp, opp) && (x*9+y-disp) >= 0 && (x*9+y-disp) < 45) found1.get(3).add(Board.getNode(x*9+y-disp));
			if (State.checkPiece(x + disp, y + disp, opp) && ((x+disp)*9+y+disp) >= 0 && ((x+disp)*9+y+disp) < 45) found1.get(4).add(Board.getNode((x+disp)*9+y+disp));
			if (State.checkPiece(x - disp, y - disp, opp) && ((x-disp)*9+y-disp) >= 0 && ((x-disp)*9+y-disp) < 45) found1.get(5).add(Board.getNode((x-disp)*9+y-disp));
			if (State.checkPiece(x + disp, y - disp, opp) && ((x+disp)*9+y-disp) >= 0 && ((x+disp)*9+y-disp) < 45) found1.get(6).add(Board.getNode((x+disp)*9+y-disp));
			if (State.checkPiece(x - disp, y + disp, opp) && ((x-disp)*9+y+disp) >= 0 && ((x-disp)*9+y+disp) < 45) found1.get(7).add(Board.getNode((x-disp)*9+y+disp));				
		}
		for (int i = 0; i < 8; i++){			
			if (found1.get(i).size() == 2){
				for (int j = 1; j < found1.get(i).size();j++) {					
					if (Node.isConnected(found1.get(i).get(j), Board.getNode(x*9+y)) && Node.isConnected(found1.get(i).get(j-1),Board.getNode(x*9+y))){
						if (State.pathContains(found1.get(i).get(j).getPosition())) {
							continue;
						}
						State.addDestination(found1.get(i).get(j-1).getPosition());
						withdrawCapture = true;
					}
				}
			}
		}	
		if (!approachCapture && !withdrawCapture) State.emptyPath();
		return (approachCapture || withdrawCapture);			
	}


	/**
	 * Checks if there are any valid capture moves for the current player
	 * @return true if capture found, otherwise false
	 */
	public static boolean checkCapture(){
	
		Color opp;
		if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
		else opp = Color.BLACK;		

		//Approach capture check
		ArrayList<ArrayList<Node>> found1 = new ArrayList<ArrayList<Node>>();
		for (int i = 0; i < 8; i++) found1.add(new ArrayList<Node>());			
		//x is Row #, y is Col #

		//Approach capture
		for (Piece current : State.getPieces()){
			if (current.getColor() != State.getTurn()) continue;
			int x = current.getX();
			int y = current.getY();
			for (int i = 0; i < 8; i++) found1.set(i, new ArrayList<Node>());//found[i] = 0;
			for (int disp = 1; disp < 3; disp++){
				if (disp == 1) opp = null;
				else{
					if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
					else opp = Color.BLACK;
				}
				//Check for empty spot followed by opponent piece in every direction
				if (State.checkPiece(x + disp, y, opp) && ((x+disp)*9+y) >= 0 && ((x+disp)*9+y) < 45) found1.get(0).add(Board.getNode((x+disp)*9+y));
				if (State.checkPiece(x - disp, y, opp) && ((x-disp)*9+y) >= 0 && ((x-disp)*9+y) < 45) found1.get(1).add(Board.getNode((x-disp)*9+y));
				if (State.checkPiece(x, y + disp, opp) && (x*9+y+disp) >= 0 && (x*9+y+disp) < 45) found1.get(2).add(Board.getNode(x*9+y+disp));
				if (State.checkPiece(x, y - disp, opp) && (x*9+y-disp) >= 0 && (x*9+y-disp) < 45) found1.get(3).add(Board.getNode(x*9+y-disp));
				if (State.checkPiece(x + disp, y + disp, opp) && ((x+disp)*9+y+disp) >= 0 && ((x+disp)*9+y+disp) < 45) found1.get(4).add(Board.getNode((x+disp)*9+y+disp));
				if (State.checkPiece(x - disp, y - disp, opp) && ((x-disp)*9+y-disp) >= 0 && ((x-disp)*9+y-disp) < 45) found1.get(5).add(Board.getNode((x-disp)*9+y-disp));
				if (State.checkPiece(x + disp, y - disp, opp) && ((x+disp)*9+y-disp) >= 0 && ((x+disp)*9+y-disp) < 45) found1.get(6).add(Board.getNode((x+disp)*9+y-disp));
				if (State.checkPiece(x - disp, y + disp, opp) && ((x-disp)*9+y+disp) >= 0 && ((x-disp)*9+y+disp) < 45) found1.get(7).add(Board.getNode((x-disp)*9+y+disp));				
			}
			for (int i = 0; i < 8; i++)				
				if (found1.get(i).size() == 2)
					for (int j = 1; j < found1.get(i).size();j++)							//Limit to only connected nodes
						if (Node.isConnected(found1.get(i).get(j), found1.get(i).get(j-1))) return true;					
		}


		//Withdraw capture check
		for (Piece current : State.getPieces()){
			if (current.getColor() != State.getTurn()) continue;
			int x = current.getX();
			int y = current.getY();
			for (int i = 0; i < 8; i++) found1.set(i, new ArrayList<Node>());//found[i] = 0;
			for (int disp = -1; disp < 2; disp+=2){
				if (disp == 1) opp = null;
				else{
					if (State.getTurn() == Color.BLACK) opp = Color.WHITE;
					else opp = Color.BLACK;
				}
				//Check for empty spot followed by opponent piece in every direction
				if (State.checkPiece(x + disp, y, opp) && ((x+disp)*9+y) >= 0 && ((x+disp)*9+y) < 45) found1.get(0).add(Board.getNode((x+disp)*9+y));
				if (State.checkPiece(x - disp, y, opp) && ((x-disp)*9+y) >= 0 && ((x-disp)*9+y) < 45) found1.get(1).add(Board.getNode((x-disp)*9+y));
				if (State.checkPiece(x, y + disp, opp) && (x*9+y+disp) >= 0 && (x*9+y+disp) < 45) found1.get(2).add(Board.getNode(x*9+y+disp));
				if (State.checkPiece(x, y - disp, opp) && (x*9+y-disp) >= 0 && (x*9+y-disp) < 45) found1.get(3).add(Board.getNode(x*9+y-disp));
				if (State.checkPiece(x + disp, y + disp, opp) && ((x+disp)*9+y+disp) >= 0 && ((x+disp)*9+y+disp) < 45) found1.get(4).add(Board.getNode((x+disp)*9+y+disp));
				if (State.checkPiece(x - disp, y - disp, opp) && ((x-disp)*9+y-disp) >= 0 && ((x-disp)*9+y-disp) < 45) found1.get(5).add(Board.getNode((x-disp)*9+y-disp));
				if (State.checkPiece(x + disp, y - disp, opp) && ((x+disp)*9+y-disp) >= 0 && ((x+disp)*9+y-disp) < 45) found1.get(6).add(Board.getNode((x+disp)*9+y-disp));
				if (State.checkPiece(x - disp, y + disp, opp) && ((x-disp)*9+y+disp) >= 0 && ((x-disp)*9+y+disp) < 45) found1.get(7).add(Board.getNode((x-disp)*9+y+disp));				
			}
			for (int i = 0; i < 8; i++)
				if (found1.get(i).size() == 2)
					for (int j = 1; j < found1.get(i).size();j++)							//Limit to only connected Nodes
						if (Node.isConnected(found1.get(i).get(j), found1.get(i).get(j-1))) return true;
							
		}
					
		return false;
	}

}