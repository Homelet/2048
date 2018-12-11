package homelet.game;

import homelet.GH.utils.Pictures;

public enum PictureKey{
	P_0("/resource/assets/0.png"),
	P_2("/resource/assets/2.png"),
	P_4("/resource/assets/4.png"),
	P_8("/resource/assets/8.png"),
	P_16("/resource/assets/16.png"),
	P_32("/resource/assets/32.png"),
	P_64("/resource/assets/64.png"),
	P_128("/resource/assets/128.png"),
	P_256("/resource/assets/256.png"),
	P_512("/resource/assets/512.png"),
	P_1024("/resource/assets/1024.png"),
	P_2048("/resource/assets/2048.png"),
	TITLE("/resource/assets/title.png"),
	ICON("/resource/icon/icon.png"),
	TRAY_ICON("/resource/icon/trayIcon.png");
	
	PictureKey(String file){
		Pictures.put(this, file);
	}
	
	public static PictureKey get(int i){
		switch(i){
			default:
			case 0:
				return P_0;
			case 2:
				return P_2;
			case 4:
				return P_4;
			case 8:
				return P_8;
			case 16:
				return P_16;
			case 32:
				return P_32;
			case 64:
				return P_64;
			case 128:
				return P_128;
			case 256:
				return P_256;
			case 512:
				return P_512;
			case 1024:
				return P_1024;
			case 2048:
				return P_2048;
		}
	}
}
