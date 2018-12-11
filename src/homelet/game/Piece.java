package homelet.game;

import homelet.GH.handlers.GH;
import homelet.GH.utils.Animate.Accelerator;
import homelet.GH.utils.Animate.AnimateFinnishListener;
import homelet.GH.utils.Animate.PointAnimator;
import homelet.GH.utils.Animate.SizeAnimator;
import homelet.GH.utils.Pictures;
import homelet.GH.visual.interfaces.LocatableRender;

import java.awt.*;

public class Piece implements LocatableRender{
	
	private static final Dimension                            DEFAULT_PIECE_SIZE         = new Dimension(Game_2048.PIECE_SCALE, Game_2048.PIECE_SCALE);
	private static final Dimension                            DEFAULT_ANIMATE_PIECE_SIZE = new Dimension(Game_2048.PIECE_SCALE + 20, Game_2048.PIECE_SCALE + 20);
	private static final Dimension                            UPSCALE_PIECE_SIZE         = new Dimension(DEFAULT_PIECE_SIZE.width + 2 * Game_2048.GAP, DEFAULT_PIECE_SIZE.height + 2 * Game_2048.GAP);
	private static final Point                                DEFAULT_VERTEX             = new Point(Game_2048.GAP, Game_2048.GAP);
	private static final Point                                DEFAULT_IMAGE_VERTEX       = new Point(DEFAULT_VERTEX.x + Game_2048.PIECE_SCALE / 2, DEFAULT_VERTEX.y + Game_2048.PIECE_SCALE / 2);
	private final        SizeAnimator                         sizeAnimator;
	private final        PointAnimator                        vertexAnimator;
	private final        AnimateFinnishListener<SizeAnimator> sizeAnimation;
	private final        AnimateFinnishListener<SizeAnimator> bornAnimation;
	//	final                PointAnimator pointAnimator;
	private final        Game_2048                            game;
	private              int                                  value;
	private              PictureKey                           pictureKey;
	int row, col;
	
	Piece(Game_2048 game, int initializeValue){
		this.game = game;
		sizeAnimator = new SizeAnimator();
		sizeAnimator.accelerator(Accelerator.LINEAR);
		sizeAnimator.from(DEFAULT_PIECE_SIZE);
		sizeAnimation = sizeAnimator->{
			sizeAnimator.onFinnish(size->{
				size.onFinnish(null);
				size.to(DEFAULT_PIECE_SIZE).animate(50);
			});
			sizeAnimator.to(DEFAULT_ANIMATE_PIECE_SIZE).animate(50);
		};
		bornAnimation = sizeAnimator->{
			sizeAnimator.onFinnish(null);
			sizeAnimator.from(0, 0).to(DEFAULT_PIECE_SIZE).animate(100);
		};
		vertexAnimator = new PointAnimator();
		vertexAnimator.accelerator(Accelerator.LINEAR);
		setValue(initializeValue);
	}
	
	void animateScaleEffect(){
		sizeAnimator.onFinnish(sizeAnimation).onFinnish();
	}
	
	void animateBornEffect(){
		sizeAnimator.onFinnish(bornAnimation).onFinnish();
	}
	
	void animateSlideEffect(int row, int col, boolean doScaleAfter){
		swap(row, col);
		vertexAnimator.onFinnish((vertexAnimator)->{
			vertexAnimator.onFinnish((vertexAnimator1)->{
				if(doScaleAfter)
					animateScaleEffect();
			});
			vertexAnimator.to(x(col), y(row)).animate(100);
		}).onFinnish();
	}
	
	void initPosition(int row, int col){
		setPosition(row, col);
		vertexAnimator.from(x(col), y(row));
	}
	
	private void setPosition(int row, int col){
		this.row = row;
		this.col = col;
	}
	
	private double x(int col){
		return col * (Game_2048.PIECE_SCALE + Game_2048.GAP);
	}
	
	private double y(int row){
		return row * (Game_2048.PIECE_SCALE + Game_2048.GAP) + Game_2048.TITLE_HEIGHT;
	}
	
	int value(){
		return value;
	}
	
	boolean empty(){
		return value == 0;
	}
	
	void setValue(int value){
		this.value = value;
		pictureKey = PictureKey.get(value);
		if(value >= game.targetValue)
			game.finish(true);
	}
	
	boolean canJoin(Piece another){
		return another.value == value;
	}
	
	void join(Piece another){
		int newValue = value + another.value;
		game.addScore(newValue);
		setValue(newValue);
		another.setValue(0);
	}
	
	private synchronized void swap(int row, int col){
		game.board[this.row][this.col] = game.board[row][col];
		game.board[row][col] = this;
		game.board[this.row][this.col].initPosition(this.row, this.col);
		game.board[row][col].setPosition(row, col);
	}
	
	@Override
	public Dimension getSize(){
		return UPSCALE_PIECE_SIZE;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		return vertexAnimator.current();
	}
	
	@Override
	public void tick(){
		sizeAnimator.tick();
		vertexAnimator.tick();
	}
	
	@Override
	public void render(Graphics2D g){
		GH.draw(g, Pictures.get(pictureKey).image(), DEFAULT_IMAGE_VERTEX, sizeAnimator.current(), null);
	}
}
