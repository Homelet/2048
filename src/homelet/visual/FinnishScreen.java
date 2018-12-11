package homelet.visual;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.Renderable;
import homelet.game.Game_2048;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

@SuppressWarnings("all")
public class FinnishScreen extends ActionsManager implements Renderable{
	
	private              boolean      displaying       = false;
	private final        Display      display;
	private static final Color        TRANS_BACKGROUND = new Color(238, 228, 218, 200);
	private final        StringDrawer title;
	private final        StringDrawer score;
	
	FinnishScreen(Display display){
		this.display = display;
		this.title = new StringDrawer();
		this.title.setColor(Display.DARK);
		this.title.setAlign(Alignment.KEEP_X_ON_CENTER);
		this.title.setTextAlign(Alignment.TOP);
		this.score = new StringDrawer();
		this.score.setColor(Display.DARK);
		this.score.setFont(ScorePane.CLEAR_SANS_BOLD.deriveFont(25.f));
		this.score.setAlign(Alignment.KEEP_X_ON_CENTER);
		this.score.setTextAlign(Alignment.TOP);
		this.score.setInsets(0, Game_2048.GAP * 2, Game_2048.GAP * 2, 0);
		this.score.setParagraphSpacing(40);
	}
	
	public boolean displaying(){
		return displaying;
	}
	
	public void display(boolean win){
		if(win){
			title.setFont(ScorePane.CLEAR_SANS_BOLD.deriveFont(60.f));
			title.initializeContents("Congratulations!");
		}else{
			title.setFont(ScorePane.CLEAR_SANS_BOLD.deriveFont(80.f));
			title.initializeContents("Game Over!");
		}
		score.initializeContents(
				"Your Score : " + display.game().score(),
				"Press Enter to Start a New Game!"
		);
		this.displaying = true;
	}
	
	@Override
	public boolean isTicking(){
		return displaying;
	}
	
	@Override
	public boolean isRendering(){
		return displaying;
	}
	
	@Override
	public void tick(){
	}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bounds = g.getClipBounds();
		g.setColor(TRANS_BACKGROUND);
		g.fill(bounds);
		title.updateGraphics(g);
		title.setFrame(bounds);
		title.setTextFrameVertex(new Point(0, bounds.height / 3));
		score.updateGraphics(g);
		score.setFrame(bounds);
		score.setTextFrameVertex(new Point(0, bounds.height / 4 * 3));
		try{
			score.validate();
			title.validate();
			score.draw();
			title.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onKeyRelease(KeyEvent e){
		if(!displaying)
			return;
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			displaying = false;
			display.game().newGame();
		}
	}
	
	@Override
	public void onMouseRelease(MouseEvent e){
		if(!displaying)
			return;
		displaying = false;
	}
}
