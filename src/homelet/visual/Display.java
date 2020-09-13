package homelet.visual;

//import com.apple.eawt.Application;
import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.Pictures;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.JCanvas;
import homelet.GH.visual.RenderManager;
import homelet.game.ExitAction;
import homelet.game.GameMode;
import homelet.game.Game_2048;
import homelet.game.PictureKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Display extends JFrame implements ExitAction{
	
	public final static Color         DARKEST  = new Color(0x776e65);
	public final static Color         DARK     = new Color(0x8f7a66);
	public static final Color         LIGHT    = new Color(0xbbada0);
	public static final Color         LIGHTEST = new Color(0xf9f6f2);
	public static final ImageIcon     ICON     = new ImageIcon(Pictures.get(PictureKey.ICON).image());
	private             JCanvas       canvas;
	private             Game_2048     game;
	private             ScorePane     best;
	private             ScorePane     score;
	private             FinnishScreen finishScreen;
	private             AboutThis     aboutThis;
	
	public Display(GameMode mode) throws HeadlessException{
		super("2048");
		setName("2048");
		setSize(mode.boardDI);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		this.canvas = new JCanvas("Canvas");
		this.canvas.getCanvasThread().setPrintNoticeInConsole(true);
		this.canvas.getCanvasThread().setFPS(50);
		ToolBox.setPreferredSize(canvas, mode.boardDI);
		// initialize the game
		int gameWidth = mode.col * (Game_2048.GAP + Game_2048.PIECE_SCALE) + Game_2048.GAP;
		this.game = new Game_2048(this, mode);
		Dimension scoreD     = new Dimension(85, 55);
		Dimension bestD      = new Dimension(110, 55);
		Dimension newGameD   = new Dimension(scoreD.width + bestD.width + Game_2048.GAP, 85);
		Dimension aboutThisD = new Dimension(gameWidth, 13);
		Point scoreV = new Point(
				gameWidth - Game_2048.GAP * 2 - bestD.width - scoreD.width,
				Game_2048.GAP
		);
		Point bestV = new Point(
				gameWidth - Game_2048.GAP - bestD.width,
				scoreV.y
		);
		Point newGameV = new Point(
				scoreV.x,
				scoreV.y + scoreD.height + Game_2048.GAP
		);
		this.score = new ScorePane("Score", scoreD, scoreV);
		this.best = new ScorePane("Best", bestD, bestV);
		UIButton newGame = new UIButton(this, "New Game", newGameD, newGameV, game::newGame);
		this.finishScreen = new FinnishScreen(this);
		this.aboutThis = new AboutThis(this, aboutThisD);
		getRenderManager().addPreTargets(best);
		getRenderManager().addPreTargets(score);
		getRenderManager().addPreTargets(newGame);
		getRenderManager().addPostTargets(finishScreen);
		getRenderManager().addPreTargets(aboutThis);
		// add to screen
		JPanel                   panel    = new JPanel();
		Layouter.GridBagLayouter layouter = new GridBagLayouter(panel);
		layouter.put(layouter.instanceOf(canvas, 0, 0).setWeight(100, 100).setFill(Fill.BOTH).setAnchor(Anchor.CENTER));
		this.setContentPane(panel);
		if(SystemTray.isSupported()){
			SystemTray systemTray = SystemTray.getSystemTray();
			try{
				systemTray.add(new TrayIcon(Pictures.get(PictureKey.TRAY_ICON).image()));
			}catch(AWTException e){
				e.printStackTrace();
			}
		}
//		// is mac
//		String OsName = System.getProperty("os.name");
//		if(OsName.contains("Mac")){
//			Image       icon_image = Pictures.get(PictureKey.ICON).image();
//			Application app        = Application.getApplication();
//			app.setAboutHandler(aboutEvent->aboutThis.showAboutGame());
//			app.disableSuddenTermination();
//			app.setQuitHandler((quitEvent, quitResponse)->{
//				onQuitGame();
//				quitResponse.performQuit();
//			});
//			app.setDockIconImage(icon_image);
//		}
		this.setIconImage(Pictures.get(PictureKey.ICON).image());
		pack();
	}
	
	public RenderManager getRenderManager(){
		return canvas.getCanvasThread().getRenderManager();
	}
	
	public void showDisplay(){
		this.setVisible(true);
		this.canvas.startRendering();
	}
	
	private void onQuitGame(){
		onExit();
		game.onExit();
		System.out.println("Exiting!");
	}
	
	public Game_2048 game(){
		return game;
	}
	
	public ScorePane best(){
		return best;
	}
	
	public ScorePane score(){
		return score;
	}
	
	public FinnishScreen finishScreen(){
		return finishScreen;
	}
	
	@Override
	public void onExit(){
		canvas.stopRendering();
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e){
		if(e.getID() == WindowEvent.WINDOW_CLOSING)
			onQuitGame();
		super.processWindowEvent(e);
	}
}
