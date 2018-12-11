package homelet.game;

import java.awt.*;

public enum GameMode{
	LEGACY_2048(4, 4, 20848, 2),
	SUPER_2048(6, 6, 2048, 4);
	public final int row, col, targetValue, initialPiece;
	public final Dimension boardDI;
	
	GameMode(int row, int col, int targetValue, int initialPiece){
		this.boardDI = calDi(row, col);
		this.row = row;
		this.col = col;
		this.targetValue = targetValue;
		this.initialPiece = initialPiece;
	}
	
	private Dimension calDi(int row, int col){
		int width  = row * (Game_2048.PIECE_SCALE + Game_2048.GAP) + Game_2048.GAP;
		int height = col * (Game_2048.PIECE_SCALE + Game_2048.GAP) + Game_2048.GAP + Game_2048.TITLE_HEIGHT;
		return new Dimension(width, height);
	}
}
