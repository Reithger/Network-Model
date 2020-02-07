package display;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import network.Device;
import network.Network;
import network.Node;
import network.Route;
import network.message.Message;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class Map {
	
	private final static int DEFAULT_WIDTH = 800;
	private final static int DEFAULT_HEIGHT = 600;
	private final static int DEFAULT_ZOOM = 50;
	private final static int DEFAULT_NODE_SIZE = 20;
	private final static int DEFAULT_ROUTE_SIZE = 6;
	private final static int REFRESH_RATE = 1000 / 30;
	
	private final static Color GRID_COLOR = new Color(225, 225, 225);
	private final static Color NODE_COLOR = Color.blue;
	private final static Color ROUTE_COLOR = Color.red;
	private final static Color BUTTON_COLOR = new Color(133, 133, 133);
	private final static Color MESSAGE_COLOR = Color.black;
	private final static Color DEVICE_COLOR = Color.yellow;
	
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
	private int nodeSize;
	private int routeSize;
	private double zoom;
	private Timer timer;
	private boolean race;
	
	public Map() {
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		cX = 0;
		cY = 0;
		zoom = DEFAULT_ZOOM;
		nodeSize = DEFAULT_NODE_SIZE;
		routeSize = DEFAULT_ROUTE_SIZE;
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
		if(net == null || race) {
			return;
		}
		race = true;
		for(Node n : net.getNodes()) {
			drawNode(n);
		}
		for(Route r : net.getRoutes()) {
			drawRoute(r);
		}
		for(Device d : net.getDevices()) {
			drawDevice(d);
		}
		grid();
		overlay();
		race = false;
	}
	
	public void drawNode(Node n) {
		panel.addRectangle(n.getName() + "_rect", 2, getXPosition(n.getX()), getYPosition(n.getY()), nodeSize, nodeSize, true, NODE_COLOR);
	}
	
	public void drawRoute(Route r) {
		Node a = r.getFirstNode();
		Node b = r.getSecondNode();
		panel.addLine(r.getName() + "_line", 1, getXPosition(a.getX()), getYPosition(a.getY()), getXPosition(b.getX()), getYPosition(b.getY()), routeSize, ROUTE_COLOR);
		for(Message m : r.getMessages()) {
			double prog = r.progress(m);
			double rise = b.getY() - a.getY();
			double run = b.getX() - a.getX();
			double angle = Math.atan(rise / run);
			double x = prog * a.distance(b) * Math.cos(angle);
			double y = prog * a.distance(b) * Math.sin(angle);
			panel.addRectangle(r.getName() + "_message_" + m.getTimeStamp(), 2, getXPosition(x), getYPosition(y), nodeSize / 4, nodeSize / 4, true, MESSAGE_COLOR);
		}
	}

	public void drawDevice(Device d) {
		panel.addRectangle(d.getName() + "_rect", 2, getXPosition(d.getX()), getYPosition(d.getY()), nodeSize / 2, nodeSize / 2, true, DEVICE_COLOR);
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
		int ovX = width * 9 / 10;
		int ovY = height / 12;
		int size = width / 30;
		panel.addButton("butt_move_up", 5, ovX, ovY - size, size, size, BUTTON_COLOR, CODE_MOVE_UP, true);
		panel.addButton("butt_move_down", 5, ovX, ovY + size, size, size, BUTTON_COLOR, CODE_MOVE_DOWN, true);
		panel.addButton("butt_move_right", 5, ovX + size, ovY, size, size, BUTTON_COLOR, CODE_MOVE_RIGHT, true);
		panel.addButton("butt_move_left", 5, ovX - size, ovY, size, size, BUTTON_COLOR, CODE_MOVE_LEFT, true);
		
		panel.addButton("butt_zoom_in", 5, ovX - size, ovY + 5 * size / 2, size, size, BUTTON_COLOR, CODE_ZOOM_IN, true);
		panel.addButton("butt_zoom_out", 5, ovX + size, ovY + 5 * size / 2, size, size, BUTTON_COLOR, CODE_ZOOM_OUT, true);
	}
	
	public int getXPosition(double inX) {
		return (int)(width / 2 + (inX - cX) / (width / zoom / 2) * (width / 2));
	}
	
	public int getYPosition(double inY) {
		return (int)(height / 2 - (inY - cY) / (height / zoom / 2) * (height / 2));
	}
	
	public void handleKeyboardInput(char in) {
		switch(in) {
		case 'w' : 
			cY += getMoveY();
			break;
		case 'a' : 
			cX -= getMoveX(); 
			break;
		case 's' : 
			cY -= getMoveY();
			break;
		case 'd' : 
			cX += getMoveX(); 
			break;
		case 'z' : 
			zoom += getZoom(); 
			panel.removeElementPrefixed("grid");
			break;
		case 'x' : 
			if(zoom - getZoom() > 0)
				zoom -= getZoom();
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
				cY += getMoveY();
				break;
			case CODE_MOVE_DOWN:
				cY -= getMoveY();
				break;
			case CODE_MOVE_RIGHT:
				cX += getMoveX();
				break;
			case CODE_MOVE_LEFT:
				cX -= getMoveX();
				break;
			case CODE_ZOOM_IN:
				zoom += getZoom(); 
				panel.removeElementPrefixed("grid");
				break;
			case CODE_ZOOM_OUT:
				if(zoom - getZoom() > 0)
					zoom -= getZoom();
				panel.removeElementPrefixed("grid");
				break;
			default: break;
		}
	}

	public int getMoveX() {
		return (int)(width / zoom / 3);
	}
	
	public int getMoveY() {
		return (int)(height / zoom / 3);
	}
	
	public int getZoom() {
		return 5;
	}
	
}
