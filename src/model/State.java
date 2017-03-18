package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

public class State {

	private Color turn;	//P1 = white, P2 = black
	private ArrayList<Piece> pieces = new ArrayList<Piece>();	
	private boolean continues = false;
	private int numPieces;
	private int startPosition;	
	private int endPosition; 
	private Piece selected;
	private Phase currentState = Phase.SELECT;
	private ArrayList<Piece> approachCapturable;
	private ArrayList<Piece> withdrawCapturable;
	private LinkedList<Integer> destination = new LinkedList<Integer>();
	private LinkedList<Integer> path = new LinkedList<Integer>();
	
	
	static {
		try {
			Board.class.newInstance();
			BoardSetup.class.newInstance();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public State(){
		turn = Color.WHITE;		
		initPieces();
		setNumPieces(pieces.size());
	}
	
	public State(State original){
		this.turn = original.turn;
		this.numPieces = original.numPieces;
		for (Piece pc: original.getPieces())
			this.pieces.add(new Piece(pc));
		assert this.pieces.size() == original.getPieces().size();
	}
	
	public Piece getPiece(Piece pc){
		int index;
		if ((index = pieces.indexOf(pc)) != -1)
			return pieces.get(index);
		else
			return null;
	}
	
	// Based on index not position
	public Piece getPiece(int x){
		return pieces.get(x);
	}
	
	public Piece getPiece(int x, int y, Color c){
		for (Piece current : getPieces()){
			if (current.getX() == x && current.getY() == y && current.getColor() == c) return current;
		}
		return null;
	}
	
	public Boolean checkPiece(int x, int y, Color c){
		
		for (Piece current : pieces){			
			if (current.getX() == x && current.getY() == y && current.getColor() == c) return true;
			else if (current.getX() == x && current.getY() == y) return false;
		}		
		if (c == null) return true;
		return false;
	}
	
	public void nextTurn(){
		if (turn == Color.WHITE) turn = Color.BLACK;
		else if (turn == Color.BLACK) turn = Color.WHITE;
	}
	
	public void resetPhase(){
		setSelected(null);
		setEndPosition(-1);
		setStartPosition(-1);
		setCurrentState(Phase.SELECT);
	}	
	
	private void initPieces(){
		
		for (int x = 0; x < 2; x++){
			for (int y = 0; y < 9; y++){
				pieces.add(new Piece(x,y,Color.BLACK));
			}
		}		
		for (int x = 3; x < 5; x++){
			for (int y = 0; y < 9; y++){
				pieces.add(new Piece(x,y,Color.WHITE));
			}
		}
		
		pieces.add(new Piece(2,1,Color.BLACK));
		pieces.add(new Piece(2,3,Color.BLACK));
		pieces.add(new Piece(2,6,Color.BLACK));
		pieces.add(new Piece(2,8,Color.BLACK));
		pieces.add(new Piece(2,0,Color.WHITE));
		pieces.add(new Piece(2,2,Color.WHITE));
		pieces.add(new Piece(2,5,Color.WHITE));
		pieces.add(new Piece(2,7,Color.WHITE));
	}
	
	public Color getTurn(){
		return turn;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public void setNumPieces(int numPieces) {
		this.numPieces = numPieces;
	}

	public ArrayList<Piece> getPieces() {
		return pieces;
	}
	
	public void removePiece(Piece pc){
		pieces.remove(pc);
	}

	public void setPieces(ArrayList<Piece> pieces) {
		this.pieces = pieces;
	}

	public ArrayList<Piece> getWithdrawCapturable() {
		return withdrawCapturable;
	}

	public void setWithdrawCapturable(ArrayList<Piece> withdrawCapturable) {
		this.withdrawCapturable = withdrawCapturable;
	}

	public ArrayList<Piece> getApproachCapturable() {
		return approachCapturable;
	}

	public void setApproachCapturable(ArrayList<Piece> approachCapturable) {
		this.approachCapturable = approachCapturable;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	
	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int startPosition) {
		this.endPosition = startPosition;
	}

	public Phase getCurrentState() {
		return currentState;
	}

	public void setCurrentState(Phase currentState) {
		this.currentState = currentState;
	}

	public Piece getSelected() {
		return selected;
	}

	public void setSelected(Piece selected) {
		this.selected = selected;
	}

	public void addToPath(Integer position){
		path.add(position);
	}
	
	public boolean pathContains(Integer position){
		return path.contains(position);
	}
	
	public void emptyPath(){
		path = new LinkedList<Integer>();
	}
	
	public void addDestination(Integer position){
		destination.add(position);
	}
	
	public boolean validDestination(Integer position){
		return destination.contains(position);
	}
	
	public void emptyTarget(){
		destination = new LinkedList<Integer>();
	}

	public void setContinue(boolean cont) { 
		continues = cont;
	}
	
	public boolean getContinue(){
		return continues;
	}		
	
	public LinkedList<Integer> getPath(){
		return path;
	}
	
}