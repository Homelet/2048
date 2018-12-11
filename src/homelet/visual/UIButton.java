package homelet.visual;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;
import homelet.game.ClickListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class UIButton extends ActionsManager implements LocatableRender{
	
	private       StringDrawer  title;
	private final Dimension     size;
	private final Point         vertex;
	private       ClickListener clickListener;
	private final Shape         backGroundShape;
	private final Display       display;
	
	UIButton(Display display, String title, Dimension size, Point vertex, ClickListener clickListener){
		this.size = size;
		this.vertex = vertex;
		this.display = display;
		this.title = new StringDrawer(title);
		this.title.setAlign(Alignment.CENTER);
		this.title.setTextAlign(Alignment.TOP);
		this.title.setFont(ScorePane.CLEAR_SANS_BOLD.deriveFont(25.f));
		Rectangle2D rectangle = GH.rectangle(false, size);
		this.title.setFrame(rectangle.getBounds());
		this.title.setColor(Display.DARKEST);
		this.backGroundShape = GH.rRectangle(false, rectangle, 5, 5);
		clickListener(clickListener);
	}
	
	private void clickListener(ClickListener clickListener){
		this.clickListener = clickListener;
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
		if(isHovering())
			g.setColor(Display.LIGHTEST);
		else
			g.setColor(Display.LIGHT);
		g.fill(backGroundShape);
		title.updateGraphics(g);
		try{
			title.validate();
			title.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMouseRelease(MouseEvent e){
		if(!display.finishScreen().displaying())
			clickListener.onClick();
	}
}
