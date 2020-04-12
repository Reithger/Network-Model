package network;

import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import network.message.Message;
import network.protocol.device.MessagePattern;
import network.protocol.message.SendProtocol;

public class Network {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final int DEFAULT_REFRESH_RATE = 30;
	
//---  Instance Variables   -------------------------------------------------------------------

	private HashMap<String, Node> nodes;
	private HashMap<String, Device> devices;
	private HashMap<String, Route> routes;
	private Timer timer;
	private boolean running;
	public static int clock;
	private SendProtocol protocolSend;
	private static int refreshRate;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Network() {
		nodes = new HashMap<String, Node>();
		devices = new HashMap<String, Device>();
		routes = new HashMap<String, Route>();
		timer = new Timer();
		running = false;
		clock = 0;
		refreshRate = DEFAULT_REFRESH_RATE;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void start() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runNetwork();
			}
		}, 0, 1000 / refreshRate);
		running = true;
	}
	
	public void stop() {
		timer.cancel();
		timer = new Timer();
		running = false;
	}
	
	private void runNetwork() {
		clock += 1;
		for(Node n : nodes.values()) {
			n.operate();
		}
		for(Route r : routes.values()) {
			r.operate();
		}
		for(Device d : devices.values()) {
			d.operate();
		}
	}
	
	public void sendMessageNode(String source, String target, String message) {
		Message m = new Message();
		m.setBody(message);
		m.addDestination(new Address(target));
		nodes.get(source).receive(m);
	}
	
	public void setSendProtocol(SendProtocol in) {
		protocolSend = in;
	}
	
	public void speedUp() {
		stop();
		refreshRate += 15;
		start();
	}
	
	public void slowDown() {
		if(refreshRate > 15) {
			stop();
			refreshRate -= 15;
			start();
		}
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	public void addNode(Node add) {
		nodes.put(add.getName(), add);
	}

	public void addNode(String name, String address, double x, double y, SendProtocol sP) {
		Node n = new Node(name, address, x, y);
		n.setCommunicationProtocol(sP);
		addNode(n);
	}
	
	public void addDevice(Device add) {
		devices.put(add.getName(), add);
	}

	public void addDevice(String name, String address, double x, double y, MessagePattern mP) {
		Device d = new Device(name, address, x , y, mP);
		addDevice(d);
	}
	
	public void addRoute(Route add) {
		routes.put(add.getName(), add);
	}
	
	public void addRoute(String nodeA, String nodeB, double upSpeed, double strmSpd) {
		Route r = nodes.get(nodeA).connect(nodes.get(nodeB), upSpeed, strmSpd);
		addRoute(r);
	}

//---  Remover Methods   -----------------------------------------------------------------------
	
	public void removeNode(Node remove) {
		nodes.remove(remove.getName());
	}
	
	public void removeNode(String remove) {
		nodes.remove(remove);
	}
	
	public void removeDevice(Device remove) {
		devices.remove(remove.getName());
	}
	
	public void removeDevice(String remove) {
		devices.remove(remove);
	}
	
	public void removeRoute(Route remove) {
		routes.remove(remove.getName());
	}
	
	public void removeRoute(String remove) {
		routes.remove(remove);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	public boolean getRunningStatus() {
		return running;
	}
	
	public Collection<Node> getNodes(){
		return nodes.values();
	}
	
	public Collection<Route> getRoutes(){
		return routes.values();
	}
	
	public Collection<Device> getDevices(){
		return devices.values();
	}
	
	public static int getClock() {
		return clock;
	}
	
	public static int getRefreshRate() {
		return DEFAULT_REFRESH_RATE;
	}
	
	public SendProtocol getSendProtocol() {
		return protocolSend;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("---Nodes---\n");
		for(Node n : nodes.values()) {
			sb.append(" > " + n.toString());
		}
		sb.append("\n---Devices---\n");
		for(Device d : devices.values()) {
			sb.append(" > " + d.toString());
		}
		sb.append("\n---Routes---\n");
		for(Route r : routes.values()) {
			sb.append(" > " + r.toString());
		}
		return sb.toString();
	}
	
}
