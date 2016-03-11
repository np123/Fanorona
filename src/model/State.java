package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

public class State {

	private static Color turn;	//P1 = white, P2 = black
	private static ArrayList<Piece> pieces = new ArrayList<Piece>();	
	private static boolean continues = false;
	private static int numPieces;
	private static int startPosition;	
	private static int endPosition; 
	private static Piece selected;
	private static Phase currentState = Phase.SELECT;
	private static ArrayList<Piece> approachCapturable;
	private static ArrayList<Piece> withdrawCapturable;
	private static LinkedList<Integer> destination = new LinkedList<Integer>();
	private static LinkedList<Integer> path = new LinkedList<Integer>();
	
	
	static {
		turn = Color.WHITE;

		try {
			Board.class.newInstance();
			BoardSetup.class.newInstance();
		} catch (Exception e){
			e.printStackTrace();
		}
		initPieces();
		setNumPieces(getPieces().size());
	}

	public static Piece getPiece(int x){
		return getPieces().get(x);
	}
	
	public static Piece getPiece(int x, int y, Color c){
		for (Piece current : getPieces()){
			if (current.getX() == x && current.getY() == y && current.getColor() == c) return current;
		}
		return null;
	}
	
	public static Boolean checkPiece(int x, int y, Color c){
		
		for (Piece current : getPieces()){			
			if (current.getX() == x && current.getY() == y && current.getColor() == c) return true;
			else if (current.getX() == x && current.getY() == y) return false;
		}		
		if (c == null) return true;
		return false;
	}
	
	public static void nextTurn(){
		if (turn == Color.WHITE) turn = Color.BLACK;
		else if (turn == Color.BLACK) turn = Color.WHITE;
	}
	
	public static void resetPhase(){
		setSelected(null);
		setEndPosition(-1);
		setStartPosition(-1);
		setCurrentState(Phase.SELECT);
	}
	
	private static void initPieces(){

		for (int x = 0; x < 2; x++){
			for (int y = 0; y < 9; y++){
				getPieces().add(new Piece(x,y,Color.BLACK));
			}
		}
		
		for (int x = 3; x < 5; x++){
			for (int y = 0; y < 9; y++){
				getPieces().add(new Piece(x,y,Color.WHITE));
			}
		}

		getPieces().add(new Piece(2,1,Color.BLACK));
		getPieces().add(new Piece(2,3,Color.BLACK));
		getPieces().add(new Piece(2,6,Color.BLACK));
		getPieces().add(new Piece(2,8,Color.BLACK));
		getPieces().add(new Piece(2,0,Color.WHITE));
		getPieces().add(new Piece(2,2,Color.WHITE));
		getPieces().add(new Piece(2,5,Color.WHITE));
		getPieces().add(new Piece(2,7,Color.WHITE));
	}
	
	public static Color getTurn(){
		return turn;
	}

	public static int getNumPieces() {
		return numPieces;
	}

	public static void setNumPieces(int numPieces) {
		State.numPieces = numPieces;
	}

	public static ArrayList<Piece> getPieces() {
		return pieces;
	}

	public static void setPieces(ArrayList<Piece> pieces) {
		State.pieces = pieces;
	}

	public static ArrayList<Piece> getWithdrawCapturable() {
		return withdrawCapturable;
	}

	public static void setWithdrawCapturable(ArrayList<Piece> withdrawCapturable) {
		State.withdrawCapturable = withdrawCapturable;
	}

	public static ArrayList<Piece> getApproachCapturable() {
		return approachCapturable;
	}

	public static void setApproachCapturable(ArrayList<Piece> approachCapturable) {
		State.approachCapturable = approachCapturable;
	}

	public static int getStartPosition() {
		return startPosition;
	}

	public static void setStartPosition(int startPosition) {
		State.startPosition = startPosition;
	}
	
	public static int getEndPosition() {
		return endPosition;
	}

	public static void setEndPosition(int startPosition) {
		State.endPosition = startPosition;
	}

	public static Phase getCurrentState() {
		return currentState;
	}

	public static void setCurrentState(Phase currentState) {
		State.currentState = currentState;
	}

	public static Piece getSelected() {
		return selected;
	}

	public static void setSelected(Piece selected) {
		State.selected = selected;
	}

	public static void addToPath(Integer position){
		path.add(position);
	}
	
	public static boolean pathContains(Integer position){
		return path.contains(position);
	}
	
	public static void emptyPath(){
		path = new LinkedList<Integer>();
	}
	
	public static void addDestination(Integer position){
		destination.add(position);
	}
	
	public static boolean validDestination(Integer position){
		return destination.contains(position);
	}
	
	public static void emptyTarget(){
		destination = new LinkedList<Integer>();
	}

	public static void setContinue(boolean cont) { 
		continues = cont;
	}
	
	public static boolean getContinue(){
		return continues;
	}
	
	public static LinkedList<Integer> getPath(){
		return path;
	}
	
}