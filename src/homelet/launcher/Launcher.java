package homelet.launcher;

import homelet.game.GameMode;
import homelet.visual.Display;

import java.awt.*;
import java.io.File;

public class Launcher{
	
	public static String PATH = new File(System.getProperty("java.class.path")).getParentFile().getPath();
	
	public static void main(String[] args){
		System.out.println(PATH);
		EventQueue.invokeLater(()->{
			GameMode gameMode;
			if(args.length == 1){
				switch(args[0].trim().toLowerCase()){
					default:
					case "legacy":
					case "legacy2048":
					case "legacy_2048":
					case "2048":
						gameMode = GameMode.LEGACY_2048;
						break;
					case "super":
					case "super2048":
					case "super_2048":
						gameMode = GameMode.SUPER_2048;
						break;
				}
			}else{
				gameMode = GameMode.LEGACY_2048;
			}
			Display display = new Display(gameMode);
			display.showDisplay();
		});
	}
}
