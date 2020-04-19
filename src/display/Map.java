package display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import network.Device;
import network.Network;
import network.Node;
import network.Route;
import network.message.Message;
import network.protocol.message.SendProtocol;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class Map {
	
	private final static int DEFAULT_WIDTH = 800;
	private final static int DEFAULT_HEIGHT = 600;
	private final static int DEFAULT_WIDTH_POPUP = 200;
	private final static int DEFAULT_HEIGHT_POPUP_SEGMENT = 30;
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
	private final static Font OVERLAY_FONT = new Font("Serif", Font.BOLD, 24);
	
	private final static int CODE_MOVE_UP = 1;
	private final static int CODE_MOVE_LEFT = 2;
	private final static int CODE_MOVE_DOWN = 3;
	private final static int CODE_MOVE_RIGHT = 4;
	private final static int CODE_ZOOM_IN = 5;
	private final static int CODE_ZOOM_OUT = 6;
	private final static int CODE_START = 7;
	private final static int CODE_PAUSE = 8;
	private final static int CODE_SPEED_UP = 18;
	private final static int CODE_SLOW_DOWN = 19;
	private final static int CODE_ADD_NODE = 9;
	private final static int CODE_ADD_ROUTE = 10;
	private final static int CODE_ADD_DEVICE = 11;
	private final static int CODE_REMOVE_NODE = 12;
	private final static int CODE_REMOVE_ROUTE = 13;
	private final static int CODE_REMOVE_DEVICE = 14;
	private final static int CODE_EDIT_ITEM = 15;
	
	
	private final static int CODE_POPUP_WINDOW_INITIAL = 100;
	
	private final static int CODE_RANGE_SIZE = 500;
	private final static int CODE_NODE_RANGE = CODE_RANGE_SIZE * 1;
	private final static int CODE_ROUTE_RANGE = CODE_RANGE_SIZE * 2;
	private final static int CODE_DEVICE_RANGE = CODE_RANGE_SIZE * 3;
	private final static int CODE_MESSAGE_RANGE = CODE_RANGE_SIZE * 4;

	private final static int INTERACTION_ENTITY_TYPE_NODE = 0;
	private final static int INTERACTION_ENTITY_TYPE_ROUTE = 1;
	private final static int INTERACTION_ENTITY_TYPE_DEVICE = 2;
	private final static int INTERACTION_ENTITY_TYPE_MESSAGE = 3;
	private final static int INTERACTION_ENTITY_TYPE_COUNT = 4;
	private final static int NODE_STATE_DEFAULT = 0;
	private final static int NODE_STATE_DISPLAY_NO = 0;
	private final static int NODE_STATE_DISPLAY_YES = 1;
	private final static int DEVICE_STATE_DEFAULT = 0;
	private final static int DEVICE_STATE_DISPLAY_NO = 0;
	private final static int DEVICE_STATE_DISPLAY_YES = 1;
	
	private Network net;
	private WindowFrame frame;
	private ElementPanel panel;
	
	private int cX;
	private int cY;
	private int selectX;
	private int selectY;
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
	private int age;
	
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
			
			public void clickBehaviour(int in, int x, int y) {
				if(in == -1) {
					selectX = x;
					selectY = y;
				}
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
				age++;
				if(age > 300) {
					age = 0;
					panel.removeElementPrefixed("");
				}
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
	
	public void setSendProtocol(SendProtocol in) {
		net.setSendProtocol(in);
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
				panel.addButton("active_display_" + code + "_button", 9, x, y, width / 5, height / 6, code, true);
				panel.addRectangle("active_display_" + code + "_rect", 10, x, y, width / 5, height / 6, true, Color.WHITE, Color.BLACK);
				panel.addText("active_display_" + code + "_text", 11, x, y, width / 5, height / 6, n.toString(), DISPLAY_FONT, true, true, true);
				break;
			default:
		}
		panel.addButton(n.getName() + "_interact", 5, x, y, nodeSize, nodeSize, codeMap.get(n.getName()), true);
	}
	
	public void drawRoute(Route r) {
		Node a = r.getFirstNode();
		Node b = r.getSecondNode();
		panel.removeElementPrefixed("line_message_" + r.getName());
		panel.addLine("line_" + r.getName(), 1, getXPosition(a.getX()), getYPosition(a.getY()), getXPosition(b.getX()), getYPosition(b.getY()), routeSize, ROUTE_COLOR);
		ArrayList<Message> messages = new ArrayList<Message>(r.getMessages());
		for(int i = 0; i < messages.size(); i++) {
			Message m = messages.get(i);
			double x1, x2, y1, y2;
			boolean direction = m.getDestination().equals(a.getAddress());
			if(!direction) {
				x1 = a.getX();
				x2 = b.getX();
				y1 = a.getY();
				y2 = b.getY();
			}
			else {
				x2 = a.getX();
				x1 = b.getX();
				y2 = a.getY();
				y1 = b.getY();
			}
			double prog = r.progress(m);
			double rise = y2 - y1;
			double run = x2 - x1;
			double angle = Math.atan2(rise, run);
			double x = x1 + prog * a.distance(b) * Math.cos(angle);
			double y = y1 + prog * a.distance(b) * Math.sin(angle);

			panel.addRectangle("line_message_" + r.getName() + m.getTimeStamp(), 2, getXPosition(x), getYPosition(y), nodeSize / 4, nodeSize / 4, true, MESSAGE_COLOR);
		}
	}

	public void drawDevice(Device d) {
		int x = getXPosition(d.getX());
		int y = getYPosition(d.getY());
		if(codeMap.get(d.getName()) == null) {
			codeMap.put(d.getName(), CODE_DEVICE_RANGE + interactCount[INTERACTION_ENTITY_TYPE_DEVICE]);
			interactCount[INTERACTION_ENTITY_TYPE_DEVICE]++;
		}
		panel.addRectangle(d.getName() + "_rect", 3, x, y, nodeSize / 2, nodeSize / 2, true, DEVICE_COLOR);
		panel.addButton(d.getName() + "_interact", 5, x, y, nodeSize, nodeSize, codeMap.get(d.getName()), true);
		
		int code = codeMap.get(d.getName());
		if(codeState.get(code) == null) {
			codeState.put(code, NODE_STATE_DEFAULT);
		}
		switch(codeState.get(code)) {
			case NODE_STATE_DISPLAY_NO:
				break;
			case NODE_STATE_DISPLAY_YES:
				panel.addButton("active_display_" + code + "_button", 9, x, y, width / 4, height / 5, code, true);
				panel.addRectangle("active_display_" + code + "_rect", 10, x, y, width / 4, height / 5, true, Color.WHITE, Color.BLACK);
				panel.addText("active_display_" + code + "_text", 11, x, y, width / 4, height / 5, d.toString(), DISPLAY_FONT, true, true, true);
				break;
			default:
				break;
	}
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
		int runningY = ovY;
		int yDiff = 2 * size;
		
		if(selectX >= 0) {
			panel.addRectangle("select_position", 9, selectX, selectY, size, size, true, new Color(180, 40, 10));
		}
		else {
			panel.removeElement("select_position");
		}
		
		//-- Navigation  --------------------------------------
		
			//Move viewpoint
		panel.addImageButton("butt_move_up", 5, ovX, runningY - size, true, "/assets/UI/up_arrow.png", CODE_MOVE_UP);
		panel.addImageButton("butt_move_right", 5, ovX + size, runningY, true, "/assets/UI/right_arrow.png", CODE_MOVE_RIGHT);
		panel.addImageButton("butt_move_down", 5, ovX, runningY + size, true, "/assets/UI/down_arrow.png", CODE_MOVE_DOWN);
		panel.addImageButton("butt_move_left", 5, ovX - size, runningY, true, "/assets/UI/left_arrow.png", CODE_MOVE_LEFT);
		panel.addImage("UI_ring_movement", 5, ovX, runningY, true, "/assets/UI/UI_ring.png");
		
		runningY += yDiff + size;
		
			//Zoom in/out
		panel.addImageButton("butt_zoom_in", 5, ovX - size, runningY, true, "/assets/UI/zoom_in.png", CODE_ZOOM_IN);
		panel.addImageButton("butt_zoom_out", 5, ovX + size, runningY, true, "/assets/UI/zoom_out.png", CODE_ZOOM_OUT);

		runningY += yDiff;
		
		//-- Control  -----------------------------------------
		
			//Pause/start
		panel.addImageButton("butt_pause", 5, ovX - size, runningY, true, "/assets/UI/pause.png", CODE_PAUSE);
		panel.addImageButton("butt_start", 5, ovX + size, runningY, true, "/assets/UI/start.png", CODE_START);
		
		runningY += yDiff;
		
			//Speed-up/Slow-down
		panel.addImageButton("butt_slow_down", 5, ovX - size, runningY, true, "/assets/UI/slow_down.png", CODE_SLOW_DOWN);
		panel.addImageButton("butt_speed_up", 5, ovX + size, runningY, true, "/assets/UI/speed_up.png", CODE_SPEED_UP);
		
		runningY += yDiff;
		
			//Add/remove Node
		panel.addImageButton("butt_add_node", 5, ovX - size, runningY, true, "/assets/UI/add_node.png", CODE_ADD_NODE);
		panel.addImageButton("butt_remove_node", 5, ovX + size, runningY, true, "/assets/UI/remove_node.png", CODE_REMOVE_NODE);
		
		runningY += yDiff;
		
			//Add/remove Route
		panel.addImageButton("butt_add_route", 5, ovX - size, runningY, true, "/assets/UI/add_route.png", CODE_ADD_ROUTE);
		panel.addImageButton("butt_remove_route", 5, ovX + size, runningY, true, "/assets/UI/remove_route.png", CODE_REMOVE_ROUTE);
		
		runningY += yDiff;
		
			//Add/remove Device
		panel.addImageButton("butt_add_device", 5, ovX - size, runningY, true, "/assets/UI/add_device.png", CODE_ADD_DEVICE);
		panel.addImageButton("butt_remove_device", 5, ovX + size, runningY, true, "/assets/UI/remove_device.png", CODE_REMOVE_DEVICE);
		
		runningY += yDiff;
		
			//See active element
		
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
		return (int)(width / 2 + (inX - cX) * zoom);
	}
	
	public double referenceFrameX(int inX) {
		return (inX - (width / 2)) / zoom + cX;
	}
	
	public int getYPosition(double inY) {
		return (int)(height / 2 - (inY - cY) * zoom);
	}
	
	public double referenceFrameY(int inY) {
		return ((-1 * inY + (height / 2)) / zoom) + cY;
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
				case CODE_SPEED_UP:
					net.speedUp();
					break;
				case CODE_SLOW_DOWN:
					net.slowDown();
					break;
				case CODE_ADD_NODE:
					String[] labels = new String[] {"name", "address", "X", "Y"};
					String[] descriptions = new String[] {"Name", "Address", "X", "Y"};
					String[] defaults = new String[] {"", "", referenceFrameX(selectX)+"", referenceFrameY(selectY)+""};
					ElementPanel nodeAdd = new ElementPanel(0, 0, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labels.length + 3)) {
						public void keyBehaviour(char in) {
							
						}
						
						public void clickBehaviour(int in, int x, int y) {
							if(in - CODE_POPUP_WINDOW_INITIAL == labels.length) {
								Node n = new Node(this.getElementStoredText("node_creation_name"), this.getElementStoredText("node_creation_address"), Double.parseDouble(this.getElementStoredText("node_creation_X")), Double.parseDouble(this.getElementStoredText("node_creation_Y")));
								n.setCommunicationProtocol(net.getSendProtocol(net.SEND_PROTOCOL_TOPTOBOTTOM));
								net.addNode(n);
								selectX = -1;
								selectY = -1;
								this.getParentFrame().disposeFrame();
							}
							else if(in - CODE_POPUP_WINDOW_INITIAL == labels.length + 1) {
								this.getParentFrame().disposeFrame();
							}
						}
					};
					addTextEntrySelection(nodeAdd, "node_creation", "Create Node", labels, descriptions, defaults, CODE_POPUP_WINDOW_INITIAL);
					break;
				case CODE_ADD_ROUTE:
					String[] labelsRoute = new String[] {"node_a", "node_b", "upload", "stream"};
					String[] descriptionsRoute = new String[] {"Node A", "Node B", "Upload Speed", "Stream Speed"};
					String[] defaultsRoute = new String[] {"", "", "1.0", "1.0"};
					ElementPanel routeAdd = new ElementPanel(0, 0, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labelsRoute.length + 3)) {
						public void keyBehaviour(char in) {
							
						}
						
						public void clickBehaviour(int in, int x, int y) {
							if(in - CODE_POPUP_WINDOW_INITIAL == labelsRoute.length) {
								net.addRoute(this.getElementStoredText("route_creation_node_a"), this.getElementStoredText("route_creation_node_b"), Double.parseDouble(this.getElementStoredText("route_creation_upload")), Double.parseDouble(this.getElementStoredText("route_creation_stream")));
								this.getParentFrame().disposeFrame();
							}
							else if(in - CODE_POPUP_WINDOW_INITIAL == labelsRoute.length + 1) {
								this.getParentFrame().disposeFrame();
							}
						}
					}; 
					addTextEntrySelection(routeAdd, "route_creation", "Create Route", labelsRoute, descriptionsRoute, defaultsRoute, CODE_POPUP_WINDOW_INITIAL);
					break;
				case CODE_ADD_DEVICE:
					String[] labelsDev = new String[] {"name", "address", "contact", "X", "Y"};
					String[] descriptionsDev = new String[] {"Name", "Address", "Contact", "X", "Y"};
					String[] defaultsDev = new String[] {"", "", "", referenceFrameX(selectX)+"", referenceFrameY(selectY)+""};
					ElementPanel devAdd = new ElementPanel(0, 0, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labelsDev.length + 3)) {
						public void keyBehaviour(char in) {
							
						}
						
						public void clickBehaviour(int in, int x, int y) {
							if(in - CODE_POPUP_WINDOW_INITIAL == labelsDev.length) {
								net.addDevice(this.getElementStoredText("device_creation_name"), this.getElementStoredText("device_creation_address"), getElementStoredText("device_creation_contact"), Double.parseDouble(this.getElementStoredText("device_creation_X")), Double.parseDouble(this.getElementStoredText("device_creation_Y")), net.getSendProtocol(net.SEND_PROTOCOL_TOPTOBOTTOM), net.getMessagePattern(net.MESSAGE_PATTERN_EVENSPREAD));
								selectX = -1;
								selectY = -1;
								this.getParentFrame().disposeFrame();
							}
							else if(in - CODE_POPUP_WINDOW_INITIAL == labelsDev.length + 1) {
								this.getParentFrame().disposeFrame();
							}
						}
					};
					addTextEntrySelection(devAdd, "device_creation", "Create Node", labelsDev, descriptionsDev, defaultsDev, CODE_POPUP_WINDOW_INITIAL);
					break;
				case CODE_REMOVE_NODE:
					String[] labelsRemoveNode = new String[] {"node"};
					String[] descriptionsRemoveNode = new String[] {"Node"};
					String[] defaultsRemoveNode = new String[] {""};
					ElementPanel nodeRemove = new ElementPanel(0, 0, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labelsRemoveNode.length + 3)) {
						public void keyBehaviour(char in) {
							
						}
						
						public void clickBehaviour(int in, int x, int y) {
							if(in - CODE_POPUP_WINDOW_INITIAL == labelsRemoveNode.length) {
								net.removeNode(this.getElementStoredText("node_removal_node"));
								clearScreen();
								this.getParentFrame().disposeFrame();
							}
							else if(in - CODE_POPUP_WINDOW_INITIAL == labelsRemoveNode.length + 1) {
								this.getParentFrame().disposeFrame();
							}
						}
					}; 
					addTextEntrySelection(nodeRemove, "node_removal", "Delete Node", labelsRemoveNode, descriptionsRemoveNode, defaultsRemoveNode, CODE_POPUP_WINDOW_INITIAL);
					break;
				case CODE_REMOVE_ROUTE:
					String[] labelsRemoveRoute = new String[] {"route"};
					String[] descriptionsRemoveRoute = new String[] {"Route"};
					String[] defaultsRemoveRoute = new String[] {""};
					ElementPanel routeRemove = new ElementPanel(0, 0, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labelsRemoveRoute.length + 3)) {
						public void keyBehaviour(char in) {
							
						}
						
						public void clickBehaviour(int in, int x, int y) {
							if(in - CODE_POPUP_WINDOW_INITIAL == labelsRemoveRoute.length) {
								net.removeRoute(this.getElementStoredText("route_removal_route"));
								clearScreen();
								this.getParentFrame().disposeFrame();
							}
							else if(in - CODE_POPUP_WINDOW_INITIAL == labelsRemoveRoute.length + 1) {
								this.getParentFrame().disposeFrame();
							}
						}
					}; 
					addTextEntrySelection(routeRemove, "route_removal", "Delete Route", labelsRemoveRoute, descriptionsRemoveRoute, defaultsRemoveRoute, CODE_POPUP_WINDOW_INITIAL);
					break;
				case CODE_REMOVE_DEVICE:
					String[] labelsRemoveDevice = new String[] {"device"};
					String[] descriptionsRemoveDevice = new String[] {"Device"};
					String[] defaultsRemoveDevice = new String[] {""};
					ElementPanel deviceRemove = new ElementPanel(0, 0, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labelsRemoveDevice.length + 3)) {
						public void keyBehaviour(char in) {
							
						}
						
						public void clickBehaviour(int in, int x, int y) {
							if(in - CODE_POPUP_WINDOW_INITIAL == labelsRemoveDevice.length) {
								net.removeDevice(this.getElementStoredText("device_removal_device"));
								clearScreen();
								this.getParentFrame().disposeFrame();
							}
							else if(in - CODE_POPUP_WINDOW_INITIAL == labelsRemoveDevice.length + 1) {
								this.getParentFrame().disposeFrame();
							}
						}
					}; 
					addTextEntrySelection(deviceRemove, "device_removal", "Delete Device", labelsRemoveDevice, descriptionsRemoveDevice, defaultsRemoveDevice, CODE_POPUP_WINDOW_INITIAL);
					break;
				case CODE_EDIT_ITEM:
					break;
				default: 
					break;
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
				case 3:
					if(codeState.get(in) == DEVICE_STATE_DISPLAY_NO) {
						codeState.put(in, DEVICE_STATE_DISPLAY_YES);
					}
					else {
						codeState.put(in, DEVICE_STATE_DISPLAY_NO);
						panel.removeElementPrefixed("active_display_");
					}
					break;
			}
		}
	}
	
	public void addTextEntrySelection(ElementPanel p, String name, String header, String[] labels, String[] descriptions, String[] defaults, int code) {
		WindowFrame popupFrame = new WindowFrame(DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * (labels.length + 3));
		popupFrame.setExitOnClose(false);
				
		int x = DEFAULT_WIDTH_POPUP / 2;
		int y = DEFAULT_HEIGHT_POPUP_SEGMENT;
				
		p.addText(name + "_header", 15, x, y / 2, DEFAULT_WIDTH_POPUP / 2, DEFAULT_HEIGHT_POPUP_SEGMENT, header, DISPLAY_FONT, true, true, true);
		
		for(int i = 0; i < labels.length; i++) {
			y += DEFAULT_HEIGHT_POPUP_SEGMENT;
			
			p.addText(name + "_" + labels[i] + "_text", 15, x, y - DEFAULT_HEIGHT_POPUP_SEGMENT * 3 / 5, DEFAULT_WIDTH_POPUP, DEFAULT_HEIGHT_POPUP_SEGMENT * 3 / 5, descriptions[i], DISPLAY_FONT, true, true, true);
			p.addRectangle(name + "_" + labels[i] + "_back", 14, x, y, DEFAULT_WIDTH_POPUP * 3 / 5, DEFAULT_HEIGHT_POPUP_SEGMENT * 3 / 5, true, new Color(130, 130, 130));
			p.addTextEntry(name + "_" + labels[i], 15, x, y, DEFAULT_WIDTH_POPUP * 3 / 5, DEFAULT_HEIGHT_POPUP_SEGMENT * 4 / 5, code++, defaults[i], DISPLAY_FONT, true, true, true);
		}

		y += DEFAULT_HEIGHT_POPUP_SEGMENT;
		
		p.addButton(name + "_butt", 15, x - DEFAULT_WIDTH_POPUP / 4, y, DEFAULT_WIDTH_POPUP / 4, DEFAULT_HEIGHT_POPUP_SEGMENT * 3 / 5, new Color(0, 130, 0), code++, true);
		p.addButton(name + "_butt_cancel", 15, x + DEFAULT_WIDTH_POPUP / 4, y, DEFAULT_WIDTH_POPUP / 4, DEFAULT_HEIGHT_POPUP_SEGMENT * 3 / 5, new Color(130, 0, 0), code, true);
		popupFrame.reservePanel("panel", p); 
	}

	public void clearScreen() {
		panel.removeElementPrefixed("");
	}
	
}
