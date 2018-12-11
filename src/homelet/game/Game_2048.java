package homelet.game;

import homelet.GH.handlers.GH;
import homelet.GH.utils.Pictures;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.Renderable;
import homelet.visual.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("all")
public class Game_2048 extends ActionsManager implements Renderable, ExitAction{
	
	public static final int       TITLE_HEIGHT = 170;
	public static final int       GAP          = 15;
	public static final int       PIECE_SCALE  = 110;
	private final       int       initialPiece;
	private final       Display   display;
	final               Piece[][] board;
	final               int       targetValue;
	final               int       row, col;
	private boolean finished = false;
	private int     best;
	private int     score;
	
	public Game_2048(Display display, GameMode mode){
		this(display, mode.row, mode.col, mode.initialPiece, mode.targetValue);
	}
	
	public Game_2048(Display display, int row, int col, int initialPiece, int targetValue){
		this.display = display;
		this.targetValue = targetValue;
		this.row = row;
		this.col = col;
		this.initialPiece = initialPiece;
		board = PieceFactory.newBoard(this, display.getRenderManager(), initialPiece);
		initBest();
	}
	
	private void initBest(){
		String string = ToolBox.readTextFile(new File("data/2048.setup"));
		if(string == null){
			defaultBest();
			return;
		}
		this.best = Integer.parseInt(ToolBox.findAll(string, "<best>([\\d]*)</best>", 1));
		SwingUtilities.invokeLater(this::syncroBestPane);
	}
	
	private void defaultBest(){
		best = 0;
	}
	
	private void writeBest(File file){
		file.setWritable(true);
		try(FileWriter writer = new FileWriter(file)){
			writer.write("<best>" + best + "</best>\n");
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Finished Writing SetUp : " + file.setReadOnly());
	}
	
	@Override
	public void onExit(){
		writeBest(new File("data/2048.setup"));
	}
	
	private void best(int best){
		this.best = Math.max(this.best, best);
	}
	
	private int best(){
		return best;
	}
	
	public void newGame(){
		PieceFactory.newGame(this, initialPiece);
	}
	
	void addScore(int score){
		setScore(this.score + score);
	}
	
	private void setScore(int newScore){
		this.score = newScore;
		display.score().setScore(newScore);
	}
	
	public int score(){
		return score;
	}
	
	void reset(){
		finished = false;
		setScore(0);
	}
	
	void finish(boolean win){
		if(win){
			System.out.println("Win");
		}else{
			System.out.println("Lose");
		}
		best(score);
		display.finishScreen().display(win);
		syncroBestPane();
		finished = true;
	}
	
	private void syncroBestPane(){
		display.best().setScore(best());
	}
	
	@Override
	public void tick(){}
	
	private static final Point TITLE_VERTEX = new Point(GAP, GAP);
	
	@Override
	public void render(Graphics2D g){
		g.setColor(Display.DARK);
		g.fill(g.getClipBounds());
		GH.draw(g, Pictures.get(PictureKey.TITLE).image(), TITLE_VERTEX, null);
	}
	
	boolean hasNextMove(boolean[][] checked, int row, int col){
		if(checked[row][col])
			return false;
		Piece current = board[row][col];
		checked[row][col] = true;
		boolean hasNext = false;
		for(int i = 0; i < 4; i++){
			Piece next = null;
			switch(i){
				case 0:
					next = findNextNonEmptyInCol(row, col);
					break;
				case 1:
					next = findLastNonEmptyInCol(row, col);
					break;
				case 2:
					next = findNextNonEmptyInRow(row, col);
					break;
				case 3:
					next = findLastNonEmptyInRow(row, col);
					break;
			}
			if(next == null)
				continue;
			if(current.canJoin(next)){
				hasNext = true;
				break;
			}else{
				hasNext |= hasNextMove(checked, next.row, next.col);
			}
		}
		return hasNext;
	}
	
	private static final long SAFR_VALUE = 150;
	private              long lastCall;
	
	@Override
	public void onKeyRelease(KeyEvent e){
		if(System.currentTimeMillis() - lastCall <= SAFR_VALUE)
			return;
		if(display.finishScreen().displaying() || finished)
			return;
		switch(e.getKeyCode()){
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				moveUp();
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				moveLeft();
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				moveDown();
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				moveRight();
				break;
		}
		lastCall = System.currentTimeMillis();
	}
	
	private void moveUp(){
		boolean moved = false;
		for(int col = 0; col < this.col; col++){
			for(int row = 0; row < this.row; row++){
				Piece current = board[row][col];
				if(current.empty())
					continue;
				// first find the next non empty and check if able to join
				Piece nextNonEmpty = findNextNonEmptyInRow(row, col);
				// next find the last empty and swap with it
				Piece lastEmpty = findLastEmptyInRow(row, col);
				// if null means the following are all empty,
				if(nextNonEmpty != null){
					boolean joined = false;
					if(current.canJoin(nextNonEmpty)){
						current.join(nextNonEmpty);
						joined = true;
						moved = true;
					}
					if(lastEmpty != null){
						current.animateSlideEffect(lastEmpty.row, lastEmpty.col, joined);
						moved = true;
					}else{
						if(joined)
							current.animateScaleEffect();
					}
				}else{
					if(lastEmpty != null){
						current.animateSlideEffect(lastEmpty.row, lastEmpty.col, false);
						moved = true;
					}
					break;
				}
			}
		}
		if(moved)
			PieceFactory.nextPiece(this);
		PieceFactory.checkGameFinished(this);
	}
	
	private void moveDown(){
		boolean moved = false;
		for(int col = 0; col < this.col; col++){
			for(int row = this.row - 1; row >= 0; row--){
				Piece current = board[row][col];
				if(current.empty())
					continue;
				// first find the next non empty and check if able to join
				Piece lastNonEmpty = findLastNonEmptyInRow(row, col);
				// next find the last empty and swap with it
				Piece nextEmpty = findNextEmptyInRow(row, col);
				// if null means the following are all empty,
				if(lastNonEmpty != null){
					boolean joined = false;
					if(current.canJoin(lastNonEmpty)){
						current.join(lastNonEmpty);
						joined = true;
						moved = true;
					}
					if(nextEmpty != null){
						current.animateSlideEffect(nextEmpty.row, nextEmpty.col, joined);
						moved = true;
					}else{
						if(joined)
							current.animateScaleEffect();
					}
				}else{
					if(nextEmpty != null){
						current.animateSlideEffect(nextEmpty.row, nextEmpty.col, false);
						moved = true;
					}
					break;
				}
			}
		}
		if(moved)
			PieceFactory.nextPiece(this);
		PieceFactory.checkGameFinished(this);
	}
	
	private void moveLeft(){
		boolean moved = false;
		for(int row = 0; row < this.row; row++){
			for(int col = 0; col < this.col; col++){
				Piece current = board[row][col];
				if(current.empty())
					continue;
				// first find the next non empty and check if able to join
				Piece nextNonEmpty = findNextNonEmptyInCol(row, col);
				// next find the last empty and swap with it
				Piece lastEmpty = findLastEmptyInCol(row, col);
				// if null means the following are all empty,
				if(nextNonEmpty != null){
					boolean joined = false;
					if(current.canJoin(nextNonEmpty)){
						current.join(nextNonEmpty);
						joined = true;
						moved = true;
					}
					if(lastEmpty != null){
						current.animateSlideEffect(lastEmpty.row, lastEmpty.col, joined);
						moved = true;
					}else{
						if(joined)
							current.animateScaleEffect();
					}
				}else{
					if(lastEmpty != null){
						current.animateSlideEffect(lastEmpty.row, lastEmpty.col, false);
						moved = true;
					}
					break;
				}
			}
		}
		if(moved)
			PieceFactory.nextPiece(this);
		PieceFactory.checkGameFinished(this);
	}
	
	private void moveRight(){
		boolean moved = false;
		for(int row = 0; row < this.row; row++){
			for(int col = this.col - 1; col >= 0; col--){
				Piece current = board[row][col];
				if(current.empty())
					continue;
				// first find the next non empty and check if able to join
				Piece lastNonEmpty = findLastNonEmptyInCol(row, col);
				// next find the last empty and swap with it
				Piece nextEmpty = findNextEmptyInCol(row, col);
				// if null means the following are all empty,
				if(lastNonEmpty != null){
					boolean joined = false;
					if(current.canJoin(lastNonEmpty)){
						current.join(lastNonEmpty);
						joined = true;
						moved = true;
					}
					if(nextEmpty != null){
						current.animateSlideEffect(nextEmpty.row, nextEmpty.col, joined);
						moved = true;
					}else{
						if(joined)
							current.animateScaleEffect();
					}
				}else{
					if(nextEmpty != null){
						current.animateSlideEffect(nextEmpty.row, nextEmpty.col, false);
						moved = true;
					}
					break;
				}
			}
		}
		if(moved)
			PieceFactory.nextPiece(this);
		PieceFactory.checkGameFinished(this);
	}
	
	private Piece findNextNonEmptyInRow(int row, int col){
		for(int i = row + 1; i < this.row; i++){
			Piece piece = board[i][col];
			if(!piece.empty()){
				return piece;
			}
		}
		return null;
	}
	
	private Piece findLastNonEmptyInRow(int row, int col){
		for(int i = row - 1; i >= 0; i--){
			Piece piece = board[i][col];
			if(!piece.empty()){
				return piece;
			}
		}
		return null;
	}
	
	private Piece findNextNonEmptyInCol(int row, int col){
		for(int i = col + 1; i < this.col; i++){
			Piece piece = board[row][i];
			if(!piece.empty()){
				return piece;
			}
		}
		return null;
	}
	
	private Piece findLastNonEmptyInCol(int row, int col){
		for(int i = col - 1; i >= 0; i--){
			Piece piece = board[row][i];
			if(!piece.empty()){
				return piece;
			}
		}
		return null;
	}
	
	private Piece findNextEmptyInRow(int row, int col){
		Piece lastEmpty = null;
		for(int i = row + 1; i < this.row; i++){
			Piece piece = board[i][col];
			if(piece.empty())
				lastEmpty = piece;
			else
				break;
		}
		return lastEmpty;
	}
	
	private Piece findLastEmptyInRow(int row, int col){
		Piece lastEmpty = null;
		for(int i = row - 1; i >= 0; i--){
			Piece piece = board[i][col];
			if(piece.empty())
				lastEmpty = piece;
			else
				break;
		}
		return lastEmpty;
	}
	
	private Piece findNextEmptyInCol(int row, int col){
		Piece lastEmpty = null;
		for(int i = col + 1; i < this.col; i++){
			Piece piece = board[row][i];
			if(piece.empty())
				lastEmpty = piece;
			else
				break;
		}
		return lastEmpty;
	}
	
	private Piece findLastEmptyInCol(int row, int col){
		Piece lastEmpty = null;
		for(int i = col - 1; i >= 0; i--){
			Piece piece = board[row][i];
			if(piece.empty())
				lastEmpty = piece;
			else
				break;
		}
		return lastEmpty;
	}
	
	void print(){
		System.out.print("==========\n[\n");
		for(int row = 0; row < this.row; row++){
			for(int col = 0; col < this.col; col++){
				Piece piece = board[row][col];
				System.out.print(piece.value() + "(" + piece.getVertex(null).x + "/" + piece.getVertex(null).y + "),\t\t");
			}
			System.out.println();
		}
		System.out.print("]\n");
	}
}
