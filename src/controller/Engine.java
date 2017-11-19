package controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;

import model.Board;
import model.Node;
import model.Piece;
import model.State;

public class Engine {		

	/*
	 * Step 1 Check for captures
	 * Step 12 Find all pieces that can be used for capture
	 * Step 13 For each piece find how many locations it can move to capture
	 * Step 14 For each destination test the type of capture (withdraw / approach)
	 * Step 15 If both withdraw and approach possible, split here
	 * Step 16 Make the capture - remove opponent pieces
	 * 
	 * Check for additional captures
	 * Repeat Steps 13 to 16 (the piece to move is now fixed)
	 * End when no more successive captures are possible			
	 * 
	 * No more captures possible 
	 * Step 17 Evaluate state, add score to cumulative potential value assigned to 1st move
	 * Step 18 Increase depth, switch turn, repeat all steps

	 * No captures are possible from the start
	 * Step 2 Check for number of available moves and split off each
	 * Step 21 Increase depth, switch turn, repeat all steps
	 * 
	 */

	private static Move bestMove;
		
	public static void makeMove(State original){
		State current = clone(original);
		
		analyze(current, 0);

		assert bestMove != null;
		System.out.println("best" + bestMove);
		
		original.getPiece(bestMove.pc).movePosition(bestMove.end / 9, bestMove.end % 9);
		
		if (bestMove instanceof CaptureMove){
			for (Piece pc: ((CaptureMove) bestMove).getCaptures()){
				original.removePiece(pc);
				original.setNumPieces(original.getNumPieces() - 1);
			}
		}
		
		original.nextTurn();	
	}

	public static State clone(State original){		
		State clone = new State(original);
		return clone;
	}


	private static double analyze (State board, int depth) {

		//if (depth == 0) System.out.println("Turn: " + board.getTurn());
		Options attackers = possibleCaptures(board);
		
		// True if no captures can be made
		if (attackers == null) {			
			attackers = possibleMoves(board);			
		}	
		
		if (depth > 3){
			//score.put(first_move, score.get(first_move) + 2 * evaluate(board));
			if (depth % 2 == 0) return 2 * evaluate(board, attackers);
			else return -2 * evaluate(board, attackers);
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		double worstScore = Double.MAX_VALUE;
		//Move bestMove = null;

		// Make and evaluate each possible move
		for (Move mv: attackers.getMoves()){
			//if (depth == 0) System.out.println("move " + mv);
			double total = 0.0;
			State position = clone(board);
			if (mv instanceof CaptureMove){
				testCapture(position, mv.getPiece(), mv.getEnd(), ((CaptureMove) mv).getCaptures());				
			} else {
				testMove(position, mv.getPiece(), mv.getEnd());
			}


			if (depth % 2 == 0) total += evaluate(position, attackers);
			else total -= evaluate(position, attackers);

			total += analyze(position, depth + 1);

			/*if (depth == 0) {
				System.out.println("TEST0");
				System.out.println(total);
				System.out.println(bestScore);
			}*/
			if (total > bestScore && depth == 0){
				//System.out.println("TEST");
				bestMove = mv;
				bestScore = total;
			}	
			if (total > bestScore)
				bestScore = total;

			if (total < worstScore)
				worstScore = total;
		}

		return (bestScore - worstScore);
	}

	private static void testMove(State position, Piece pc, int dest){
		position.getPiece(position.getPieces().indexOf(pc)).movePosition(dest / 9, dest % 9);
		position.nextTurn();
	}

	private static void testCapture(State position, Piece pc, int dest, Iterable<Piece> removes){		
		position.getPiece(pc).movePosition(dest / 9, dest % 9);
		for (Piece p: removes){
			position.removePiece(p);
		}
		position.nextTurn();
	}

	private static double evaluate (State board, Options movers){

		double score = 0.0;
		ArrayList<Integer> pos = new ArrayList<Integer>();
		double cpts = 0.0;
		double pcs = 0.0;

		if (movers.advanced){
			for (Move mv: movers.getMoves()){
				cpts = ((CaptureMove) mv).numCaptures();
				if (pos.contains(mv.start)) cpts = cpts * 0.85;
				else {
					pos.add(mv.start);
					pcs++;
				}
				score += (Math.pow(cpts, 1.7) + Math.pow(pcs, 1.2));
			}
		} else {
			for (Move mv: movers.getMoves()){				
				if (pos.contains(mv.start)) pcs += 0.3;
				else {
					pos.add(mv.start);
					pcs++;
				}
				score += Math.pow(pcs, 1.2);
			}
		}

		return score;
	}


	private static Options possibleMoves (State board){

		Options moves = new Options(false);

		for (Piece pc: board.getPieces()){
			if (pc.getColor() != board.getTurn()) continue;
			Node start = Board.getNode(pc.getPosition());

			for (Integer adj: start.getAdjacent()){
				if (board.checkPiece(adj / 9, adj % 9, null)){
					moves.add(new Move(pc, pc.getPosition(), adj));
				}
			}						
		}
		return moves;
	}	

	private static Options possibleCaptures(State board){

		Options opts = new Options(true);		
		Color opp;
		if (board.getTurn().equals(Color.WHITE)) opp = Color.BLACK;
		else opp = Color.WHITE;
		
		for (Piece pc: board.getPieces()){
			//System.out.println(pc + " x: " + pc.getX() + " y: " + pc.getY());			
			if (pc.getColor() != board.getTurn()) continue;			
			for (Integer adj: Board.getNode(pc.getPosition()).getAdjacent()){
				int x = adj / 9;
				int y = adj % 9;

				int xdir = x - pc.getX();
				int ydir = y - pc.getY();

				//System.out.println(adj + " x: " + x + " y: " + y);
				//System.out.println(xdir + " " + ydir);
				if (board.checkPiece(x, y, null)){					
					ArrayList<Piece> cps = new ArrayList<Piece>();
					x += xdir;
					y += ydir;	
					while (x >= 0 && x <= 9 && y >= 0 && y <= 9){						
						if (board.checkPiece(x, y, opp)) cps.add(new Piece(x, y, opp));
						else break;
						x += xdir;
						y += ydir;	
						//System.out.println(board.checkPiece(x, y, opp) + " " + (x*9+y) + " x: " + x + " y: " + y);						
					}
					if (cps.size() > 0){	
						opts.add(new CaptureMove(pc, pc.getPosition(), adj, cps));
					}
				}
			}
		}
		if (opts.movable.size() > 0 )return opts;
		else return null;
	}

}



class Move {

	static int instances = 0;
	static ArrayList<Move> moves = new ArrayList<Move>();

	Piece pc = null;
	final int start;
	final int end;
	final int id;

	public Move(Piece pc, int start, int end){
		this.pc = pc;
		this.start = start;
		this.end = end;
		this.id = instances;
		moves.add(this);
		instances++;
	}

	public Piece getPiece(){
		return pc;
	}

	public int getStart(){
		return start;
	}

	public int getEnd(){
		return end;
	}

	@Override
	public boolean equals(Object second){
		Move other = (Move) second;
		return this.start == other.getStart() && this.end == other.getEnd(); 
	}

	@Override
	public int hashCode(){
		return this.id;
	}

	@Override
	public String toString(){
		return "Start: " + this.start + " , End: " + this.end + " Piece: " + pc.getColor(); 
	}

}


class CaptureMove extends Move {

	private ArrayList<Piece> captures;

	public CaptureMove(Piece pc, int start, int end, ArrayList<Piece> takes){
		super(pc, start, end);
		this.captures = takes;
	}

	public Iterable<Piece> getCaptures(){
		return captures;
	}

	public int numCaptures(){
		return captures.size();
	}
}



// A Collector for Move instances

class Options {

	ArrayList<Move> movable;
	boolean advanced = false;

	public Options(boolean type){
		this.movable = new ArrayList<Move>();
		this.advanced = type;
	}

	public boolean isCapture(){
		return this.advanced;
	}

	public void add(Move mv){
		movable.add(mv);
	}

	public void add(CaptureMove mv){
		movable.add(mv);		
	}

	public void clear(){
		movable = new ArrayList<Move>();
	}

	public Iterable<Move> getMoves(){
		return movable;
	}

}