package homelet.game;

import homelet.GH.utils.ToolBox;
import homelet.GH.visual.RenderManager;

class PieceFactory{
	
	private PieceFactory(){}
	
	static Piece[][] newBoard(Game_2048 game, RenderManager manager, int initialPiece){
		int row = game.row;
		int col = game.col;
		manager.addPreTargets(game);
		Piece[][] board = new Piece[row][col];
		for(int i = 0; i < row * col; i++){
			int   aRow = i / col;
			int   aCol = i % col;
			Piece piece;
			if(i < initialPiece)
				piece = newPiece(game);
			else
				piece = emptyPiece(game);
			piece.initPosition(aRow, aCol);
			manager.addTargets(piece);
			board[aRow][aCol] = piece;
		}
		fisherYates(board, row, col);
		return board;
	}
	
	static void newGame(Game_2048 game, int initialPiece){
		int row = game.row;
		int col = game.col;
		for(int i = 0; i < row * col; i++){
			int   aRow  = i / col;
			int   aCol  = i % col;
			Piece piece = game.board[aRow][aCol];
			if(i < initialPiece)
				piece.setValue(newValue());
			else
				piece.setValue(0);
		}
		fisherYates(game.board, row, col);
		game.reset();
	}
	
	private static void fisherYates(Piece[][] board, int row, int col){
		// fisher-yates
		for(int max = row * col, index = max - 1; index >= 0; index--){
			// from [0, index] rand fetch num
			int rand = (int) ToolBox.random(0, index + 1);
			swap(board, rand / col, rand % col, index / col, index % col);
		}
	}
	
	private static void swap(Piece[][] board, int row1, int col1, int row2, int col2){
		Piece p1 = board[row1][col1];
		Piece p2 = board[row2][col2];
		board[row1][col1] = p2;
		board[row2][col2] = p1;
		p1.initPosition(row2, col2);
		p2.initPosition(row1, col1);
	}
	
	static void nextPiece(Game_2048 game){
		int newValue = newValue();
		// TODO Performance
		int   leftSpace    = countLeftSpace(game);
		int   randIndex    = (int) ToolBox.random(0, leftSpace);
		Piece theChosenOne = navigateTo(game, randIndex);
		if(theChosenOne != null){
			theChosenOne.setValue(newValue);
			theChosenOne.animateBornEffect();
		}
	}
	
	static void checkGameFinished(Game_2048 game){
		int leftSpace = countLeftSpace(game);
		if(leftSpace == 0)
			checkHasNextMove(game);
	}
	
	private static void checkHasNextMove(Game_2048 game){
		boolean[][] finished = new boolean[game.row][game.col];
		if(!game.hasNextMove(finished, 0, 0))
			game.finish(false);
	}
	
	private static Piece navigateTo(Game_2048 game, int no){
		for(int row = 0; row < game.row; row++){
			for(int col = 0; col < game.col; col++){
				Piece piece = game.board[row][col];
				if(piece.empty()){
					if(no == 0)
						return piece;
					no--;
				}
			}
		}
		return null;
	}
	
	private static int newValue(){
		boolean isFour = ((int) ToolBox.random(0, 2)) == 1;
		return isFour ? 4 : 2;
	}
	
	private static Piece newPiece(Game_2048 game){
		// gen either four or two
		return new Piece(game, newValue());
	}
	
	private static Piece emptyPiece(Game_2048 game){
		return new Piece(game, 0);
	}
	
	private static int countLeftSpace(Game_2048 game){
		int counter = 0;
		for(int row = 0; row < game.row; row++){
			for(int col = 0; col < game.col; col++){
				if(game.board[row][col].empty())
					counter++;
			}
		}
		return counter;
	}
}
