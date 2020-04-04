package display;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
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
	
	private final static Font DISPLAY_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Font OVERLAY_FONT = new Font("Serif", Font.BOLD, 36);
	
	private final static int CODE_MOVE_UP = 1;
	private final static int CODE_MOVE_LEFT = 2;
	private final static int CODE_MOVE_DOWN = 3;
	private final static int CODE_MOVE_RIGHT = 4;
	private final static int CODE_ZOOM_IN = 5;
	private final static int CODE_ZOOM_OUT = 6;
	private final static int CODE_START = 7;
	private final static int CODE_PAUSE = 8;
	private final static int CODE_RANGE_SIZE = 500;
	private final static int CODE_NODE_RANGE = CODE_RANGE_SIZE * 1;
	private final static int CODE_ROUTE_RANGE = CODE_RANGE_SIZE * 2;
	private final static int CODE_MESSAGE_RANGE = CODE_RANGE_SIZE * 3;

	private final static int INTERACTION_ENTITY_TYPE_COUNT = 3;
	private final static int INTERACTION_ENTITY_TYPE_NODE = 0;
	private final static int INTERACTION_ENTITY_TYPE_ROUTE = 1;
	private final static int INTERACTION_ENTITY_TYPE_MESSAGE = 2;
	private final static int NODE_STATE_DEFAULT = 0;
	private final static int NODE_STATE_DISPLAY_NO = 0;
	private final static int NODE_STATE_DISPLAY_YES = 1;
	
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
	private HashMap<String, Integer> codeMap;
	private HashMap<Integer, Integer> codeState;
	private int[] interactCount;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Map() {
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		cX = 0;
		cY = 0;
		zoom = DEFAULT_ZOOM;
		nodeSize = DEFAULT_NODE_SIZE;
		routeSize = DEFAULT_ROUTE_SIZE;
		codeMap = new HashMap<String, Integer>();
		codeState = new HashMap<Integer, Integer>();
		interactCount = new int[INTERACTION_ENTITY_TYPE_COUNT];
		frame = new WindowFrame(width, height);
		panel = new ElementPanel(0, 0, width, height) {
			public void keyBehaviour(char in) {
				handleKeyboardInput(in);
			}
			
			public void clickBehaviour(int in) {
				handleClickInput(in);
			}
		};
		frame.reservePanel("map", panel);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateMap();
				command();
			}
			
		}, 0, REFRESH_RATE);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void command() {
		return;
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
	
//---  Draw Elements   ------------------------------------------------------------------------
	
	public void drawNode(Node n) {
		int x = getXPosition(n.getX());
		int y = getYPosition(n.getY());
		panel.addRectangle(n.getName() + "_rect", 3, x, y, nodeSize, nodeSize, true, NODE_COLOR);
		if(codeMap.get(n.getName()) == null) {
			codeMap.put(n.getName(), CODE_NODE_RANGE + interactCount[INTERACTION_ENTITY_TYPE_NODE]);
			interactCount[INTERACTION_ENTITY_TYPE_NODE]++;
		}
		int code = codeMap.get(n.getName());
		if(codeState.get(code) == null) {
			codeState.put(code, NODE_STATE_DEFAULT);
		}
		switch(codeState.get(code)) {
			case NODE_STATE_DISPLAY_NO:
				break;
			case NODE_STATE_DISPLAY_YES:
				panel.addButton("active_display_" + code + "_button", 9, x, y, width / 5, height / 8, code, true);
				panel.addRectangle("active_display_" + code + "_rect", 10, x, y, width / 5, height / 8, true, Color.WHITE, Color.BLACK);
				panel.addText("active_display_" + code + "_text", 11, x, y, width / 5, height / 8, n.toString(), DISPLAY_FONT, true, true, true);
				break;
			default:
		}
		panel.addButton(n.getName() + "_interact", 5, x, y, nodeSize, nodeSize, codeMap.get(n.getName()), true);
	}
	
	public void drawRoute(Route r) {
		Node a = r.getFirstNode();
		Node b = r.getSecondNode();
		panel.removeElementPrefixed("line_" + r.getName());
		panel.addLine("line_" + r.getName(), 1, getXPosition(a.getX()), getYPosition(a.getY()), getXPosition(b.getX()), getYPosition(b.getY()), routeSize, ROUTE_COLOR);
		for(Message m : r.getMessages()) {
			double x1, x2, y1, y2;
			if(m.getDestination().equals(a.getAddress())) {
				x1 = b.getX();
				x2 = a.getX();
				y1 = b.getY();
				y2 = a.getY();
			}
			else {
				x2 = b.getX();
				x1 = a.getX();
				y2 = b.getY();
				y1 = a.getY();
			}
			double prog = r.progress(m);
			double rise = y2 - y1;
			double run = x2 - x1;
			double angle = Math.atan(rise / run);
			double x = x1 + prog * a.distance(b) * Math.cos(angle);
			double y = y1 + prog * a.distance(b) * Math.sin(angle);

			panel.addRectangle("line_" + r.getName() + "_message_" + m.getTimeStamp(), 2, getXPosition(x), getYPosition(y), nodeSize / 4, nodeSize / 4, true, MESSAGE_COLOR);
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
		panel.addText("butt_zoom_in_text", 6, ovX - size, ovY + 5 * size / 2, 2*size, 2*size, "+", OVERLAY_FONT, true, true, true);
		panel.addButton("butt_zoom_out", 5, ovX + size, ovY + 5 * size / 2, size, size, BUTTON_COLOR, CODE_ZOOM_OUT, true);
		panel.addText("butt_zoom_out_text", 6, ovX + size, ovY + 5 * size / 2, 2*size, 2*size, "-", OVERLAY_FONT, true, true, true);
		
		panel.addButton("butt_pause", 5, ovX - size, ovY + 5 * size, size, size, BUTTON_COLOR, CODE_PAUSE, true);
		panel.addButton("butt_start", 5, ovX + size, ovY + 5 * size, size, size, BUTTON_COLOR, CODE_START, true);
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getMoveX() {
		return (int)(width / zoom / 3);
	}
	
	public int getMoveY() {
		return (int)(height / zoom / 3);
	}
	
	public int getZoom() {
		return 5;
	}
	
	public Network getNetwork() {
		return net;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
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
		if(in < CODE_RANGE_SIZE) {
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
				case CODE_PAUSE:
					System.out.println("STOP");
					net.stop();
					break;
				case CODE_START:
					System.out.println("START");
					net.start();
					break;
				default: break;
			}
		}
		else {
			switch((int)(in / CODE_RANGE_SIZE)) {
				case 0:
					break;
				case 1:
					if(codeState.get(in) == NODE_STATE_DISPLAY_NO) {
						codeState.put(in, NODE_STATE_DISPLAY_YES);
					}
					else {
						codeState.put(in, NODE_STATE_DISPLAY_NO);
						panel.removeElementPrefixed("active_display_");
					}
					break;
				case 2:
					break;
			}
		}
	}

}
