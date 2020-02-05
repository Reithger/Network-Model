package display;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import network.Network;
import network.Node;
import network.Route;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class Map {
	
	private final static int DEFAULT_WIDTH = 800;
	private final static int DEFAULT_HEIGHT = 600;
	private final static int DEFAULT_ZOOM = 50;
	private final static Color GRID_COLOR = new Color(225, 225, 225);
	private final static Color NODE_COLOR = Color.blue;
	private final static Color ROUTE_COLOR = Color.red;
	private final static int REFRESH_RATE = 1000 / 30;
	
	private final static int CODE_MOVE_UP = 1;
	private final static int CODE_MOVE_LEFT = 2;
	private final static int CODE_MOVE_DOWN = 3;
	private final static int CODE_MOVE_RIGHT = 4;
	private final static int CODE_ZOOM_IN = 5;
	private final static int CODE_ZOOM_OUT = 6;
	private final static int CODE_NODE_RANGE = 500;

	private Network net;
	private WindowFrame frame;
	private ElementPanel panel;
	
	private int cX;
	private int cY;
	private int width;
	private int height;
	private double zoom;
	private Timer timer;
	
	public Map() {
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		cX = 0;
		cY = 0;
		zoom = DEFAULT_ZOOM;
		frame = new WindowFrame(width, height);
		panel = new ElementPanel(0, 0, width, height) {
			public void keyBehaviour(char in) {
				handleKeyboardInput(in);
			}
			
			public void clickBehaviour(int in) {
				handleClickInput(in);
			}
		};
		frame.addPanelToScreen(panel);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateMap();
			}
			
		}, 0, REFRESH_RATE);
	}
	
	public void setNetwork(Network in) {
		net = in;
	}
	
	public void updateMap() {
		if(net == null) {
			return;
		}
		int size = (int)(zoom * .3);
		for(Node n : net.getNodes()) {
			panel.addRectangle(n.getName() + "_rect", 2, getXPosition(n.getX()), getYPosition(n.getY()), size, size, true, NODE_COLOR);
		}
		for(Route r : net.getRoutes()) {
			Node a = r.getFirstNode();
			Node b = r.getSecondNode();
			panel.addLine(r.getName() + "_line", 1, getXPosition(a.getX()), getYPosition(a.getY()), getXPosition(b.getX()), getYPosition(b.getY()), (int)(zoom * .1), ROUTE_COLOR);
		}
		grid();
		overlay();
	}
	
	public void grid() {
		for(int i = cX - (int)(width / 2 / zoom); i <= cX + (int)(width / 2 / zoom); i++) {
			panel.addRectangle("grid_v_" + i + "", 0, getXPosition(i), 0, 1, height, false, GRID_COLOR);
		}
		for(int j = cY - (int)(height / 2 / zoom); j <= cY + (int)(height / 2 / zoom); j++) {
			panel.addRectangle("grid_h_" + j + "", 0, 0, getYPosition(j), width, 1, false, GRID_COLOR);
		}
	
	}
	
	public void overlay() {
		
	}
	
	public int getXPosition(double inX) {
		return (int)(width / 2 + (inX - cX) / (width / zoom / 2) * (width / 2));
	}
	
	public int getYPosition(double inY) {
		return (int)(height / 2 + (inY - cY) / (height / zoom / 2) * (height / 2));
	}
	
	public void handleKeyboardInput(char in) {
		switch(in) {
		case 'w' : 
			cY -= height / zoom / 3; 
			break;
		case 'a' : 
			cX -= width / zoom / 3; 
			break;
		case 's' : 
			cY += height / zoom / 3;
			break;
		case 'd' : 
			cX += width / zoom / 3; 
			break;
		case 'z' : 
			zoom += 5; 
			panel.removeElementPrefixed("grid");
			break;
		case 'x' : 
			if(zoom - 5 > 0)
				zoom -= 5;
			panel.removeElementPrefixed("grid");
			break;
		case 'p' : 
			timer.cancel(); 
			break;
		default : break;
	}
	}

	public void handleClickInput(int in) {
		switch(in) {
			case CODE_MOVE_UP:
				cY += height / zoom * 3;
				break;
			case CODE_MOVE_DOWN:
				cY -= height / zoom * 3;
				break;
			case CODE_MOVE_RIGHT:
				cX += width / zoom * 3;
				break;
			case CODE_MOVE_LEFT:
				cX -= width / zoom * 3;
				break;
			case CODE_ZOOM_IN:
				zoom += 5; 
				panel.removeElementPrefixed("grid");
				break;
			case CODE_ZOOM_OUT:
				if(zoom - 5 > 0)
					zoom -= 5;
				panel.removeElementPrefixed("grid");
				break;
			default: break;
		}
	}
	
}
