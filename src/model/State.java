package model;

import java.awt.Color;
import java.util.ArrayList;

public class State {

	private static Color turn;	//1 = white, 2 = black
	public static ArrayList<Piece> pieces = new ArrayList<Piece>();
	public static int numPieces;
	public static boolean selected = false;
	public static int position;
	
	static {
		turn = Color.WHITE;

		Board currentPosition = new Board();
		initPieces();
		numPieces = pieces.size();
		
		/*for (int x = 0; x < numPieces; x++){
			System.out.println("x: " + pieces.get(x).getX() + " y: " + pieces.get(x).getY() + " c: " + pieces.get(x).getColor() + " pos: " + pieces.get(x).getPosition());
		}*/
	}

	public static Piece getPiece(int x){
		return pieces.get(x);
	}
	
	public static void nextTurn(){
		if (turn == Color.WHITE) turn = Color.BLACK;
		else if (turn == Color.BLACK) turn = Color.WHITE;
	}
	
	private static void initPieces(){

		for (int x = 0; x < 2; x++){
			for (int y = 0; y < 9; y++){
				pieces.add(new Piece(x,y,Color.BLACK));
				//pieces.add(new Piece(5 - x,y,Color.WHITE));
			}
		}
		
		for (int x = 3; x < 5; x++){
			for (int y = 0; y < 9; y++){
				//pieces.add(new Piece(x,y,Color.BLACK));
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

}
