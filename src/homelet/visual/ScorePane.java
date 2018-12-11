package homelet.visual;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.interfaces.LocatableRender;

import java.awt.*;
import java.io.IOException;

@SuppressWarnings("all")
public class ScorePane implements LocatableRender{
	
	static Font CLEAR_SANS_BOLD;
	
	static{
		try{
			CLEAR_SANS_BOLD = Font.createFont(Font.TRUETYPE_FONT, ScorePane.class.getResource("/resource/font/Clear Sans Bold.ttf").openStream());
		}catch(FontFormatException | IOException e){
			e.printStackTrace();
		}
	}
	
	private final StringDrawer title;
	private final StringDrawer score;
	private final Dimension    size;
	private final Point        vertex;
	private final Shape        shape;
	
	ScorePane(String title, Dimension size, Point vertex){
		this(title, 0, size, vertex);
	}
	
	private ScorePane(String title, int score, Dimension size, Point vertex){
		this.size = size;
		this.vertex = vertex;
		this.shape = GH.rRectangle(false, GH.rectangle(false, size), 5, 5);
		int breakPoint = (int) (size.getHeight() / (13 + 25) * 13);
		this.title = new StringDrawer(title);
		this.title.setFont(CLEAR_SANS_BOLD.deriveFont(13.f));
		this.title.setAlign(Alignment.TOP);
		this.title.setTextAlign(Alignment.TOP);
		this.title.setColor(Display.DARKEST);
		this.score = new StringDrawer();
		this.score.setFont(CLEAR_SANS_BOLD.deriveFont(25.f));
		this.score.setAlign(Alignment.KEEP_X_ON_CENTER);
		this.score.setTextFrameVertex(new Point(0, breakPoint));
		this.score.setTextAlign(Alignment.TOP);
		this.score.setColor(Display.DARKEST);
		setScore(score);
	}
	
	public void setScore(int score){
		synchronized(this.score){
			this.score.initializeContents(String.valueOf(score));
		}
	}
	
	@Override
	public Dimension getSize(){
		return size;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		return vertex;
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		g.setColor(Display.LIGHT);
		g.fill(shape);
		Rectangle rectangle = g.getClipBounds();
		title.updateGraphics(g);
		score.updateGraphics(g);
		title.setFrame(rectangle);
		score.setFrame(rectangle);
		try{
			title.validate();
			score.validate();
			title.draw();
			score.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
	}
}
