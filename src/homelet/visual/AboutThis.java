package homelet.visual;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class AboutThis extends ActionsManager implements LocatableRender{
	
	private static final String       title   = String.valueOf((char) 0x00A9) + " 2018 HomeletWei";
	private static final String       message = "2048\nTo Report a Bug or Provide Feedback Please Contact:\nGame Author: homeletwei@163.com (Mr. Wei)\nAll Rights Reserved.";
	private final        StringDrawer aboutThis;
	private final        Dimension    size;
	private final        Display      display;
	
	AboutThis(Display display, Dimension size){
		this.display = display;
		aboutThis = new StringDrawer(title);
		aboutThis.setFont(ScorePane.CLEAR_SANS_BOLD.deriveFont((float) size.height));
		aboutThis.setColor(Display.LIGHTEST);
		aboutThis.setAlign(Alignment.CENTER);
		aboutThis.setTextAlign(Alignment.TOP);
		this.size = size;
	}
	
	@Override
	public Dimension getSize(){
		return size;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		Point p = Alignment.BOTTOM.getVertex(false, rectangle, new Rectangle(size));
		p.translate(0, -1);
		return p;
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		if(!isHovering() || display.finishScreen().displaying())
			return;
		aboutThis.updateGraphics(g);
		aboutThis.setFrame(g.getClipBounds());
		try{
			aboutThis.validate();
			aboutThis.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMouseRelease(MouseEvent e){
		if(display.finishScreen().displaying())
			return;
		showAboutGame();
	}
	
	void showAboutGame(){
		JOptionPane.showMessageDialog(display, message, "About Game", JOptionPane.INFORMATION_MESSAGE, Display.ICON);
	}
}
