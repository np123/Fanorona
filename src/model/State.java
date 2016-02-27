package model;

import java.awt.Color;
import java.util.ArrayList;

public class State {

	private static Color turn;	//1 = white, 2 = black
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
	public static ArrayList<Piece> capturable = new ArrayList<Piece>();
	private static int numPieces;
	public static boolean selected = false;
	public static boolean capture = false;
	public static int startPosition;
	public static boolean approachCapture = false;
	public static boolean withdrawCapture = false;
	public static boolean grabPiece = false;
	public static int endPosition;
	
	static {
		turn = Color.WHITE;

		try {
			Board.class.newInstance();
			BoardSetup.class.newInstance();
		} catch (Exception e){
			e.printStackTrace();
		}
		initPieces();
		setNumPieces(pieces.size());
	}

	public static Piece getPiece(int x){
		return pieces.get(x);
	}
	
	public static Boolean getPiece(int x, int y, Color c){
		for (Piece current : pieces){
			if (current.getX() == x && current.getY() == y && current.getColor() == c) return true;
			if (current.getX() == x && current.getY() == y && current.getColor() != c) return false;
		}
		if (c == null) return true;
		return false;
	}
	
	public static void nextTurn(){
		if (turn == Color.WHITE) turn = Color.BLACK;
		else if (turn == Color.BLACK) turn = Color.WHITE;
	}
	
	private static void initPieces(){

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
	
	public static Color getTurn(){
		return turn;
	}

	public static int getNumPieces() {
		return numPieces;
	}

	public static void setNumPieces(int numPieces) {
		State.numPieces = numPieces;
	}

}
