package model;

/**
 * The BoardSetup class is responsible for creating and 
 * managing the nodes on the board
 *
 */
public class BoardSetup extends Board {

	static {				
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 9; y++) {
				int xpos = view.UserInterface.getHorz(y);
				int ypos = view.UserInterface.getVert(x);
				nodes[x*9+y] = new Node(xpos,ypos, x*9+y);
			}
		}		
		defineConnections();
	}		
	
	private static void defineConnections(){
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
	
}
